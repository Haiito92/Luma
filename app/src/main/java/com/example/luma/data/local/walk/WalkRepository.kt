package com.example.luma.data.local.walk

import android.os.SystemClock
import android.util.Log
import com.example.luma.data.local.auth.AuthRepository
import com.example.luma.data.local.LatLng
import com.example.luma.data.local.LocationRepository
import com.example.luma.data.local.Place
import com.example.luma.data.local.Route
import com.example.luma.data.local.auth.AuthState
import com.example.luma.data.local.distanceMeters
import com.example.luma.data.local.guardian.GuardedResult
import com.example.luma.data.local.guardian.GuardiansRepository
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.GeoPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class WalkRepository(
    private val authRepository: AuthRepository,
    private val locationRepository: LocationRepository,
    private val guardiansRepository: GuardiansRepository,
    private val appScope: CoroutineScope,
    private val db: FirebaseFirestore
) {

    private val walks by lazy { db.collection("walks") }

    private val _activeWalk = MutableStateFlow<Walk?>(null)
    val activeWalk : StateFlow<Walk?> = _activeWalk.asStateFlow()

    private var publishJob: Job? = null

    suspend fun startWalk(destination: Place, route: Route): StartWalkResult {
        val me = authRepository.currentUser.value ?: return StartWalkResult.NotSignedIn
        if (_activeWalk.value != null) return StartWalkResult.AlreadyInProgress
        if (route.points.size < 2 ) return StartWalkResult.InvalidRoute

        return try{

            val guardiansId = guardiansRepository.getGuardiansIds()
            val walkRef = walks.document()

            walkRef.set(
                mapOf(
                    "walkerId" to me.documentId,
                    "walkerFullName" to "${me.name} ${me.lastName}",
                    "guardiansIds" to guardiansId,
                    "status" to "active",
                    "sosActive" to false,
                    "sosAt" to -1,
                    "destination" to mapOf(
                        "name" to destination.name,
                        "location" to GeoPoint(destination.latLng.latitude, destination.latLng.longitude)
                    ),
                    "startedAt" to FieldValue.serverTimestamp(),
                    "route" to mapOf(
                        "points" to route.points.map { point ->
                            "location" to GeoPoint(point.latitude, point.longitude)
                        },
                        "distanceMeters" to route.distanceMeters,
                        "durationSeconds" to route.durationSeconds
                    )
                )
            ).await()

            val walk = Walk(walkRef.id, destination, route)
            _activeWalk.value = walk
            startPublishing(walk.id)
            return StartWalkResult.Started(walk)
        }
        catch (e: FirebaseFirestoreException){
            StartWalkResult.Failed
        }
    }

    private fun startPublishing(walkId: String){
        publishJob?.cancel()

        publishJob = appScope.launch {
            var lastSentAt = 0L
            var lastSent: LatLng? = null

            locationRepository.locationUpdates().collect { location ->
                val now = SystemClock.elapsedRealtime()
                val elapsed = now - lastSentAt
                val moved = lastSent?.let { it.distanceMeters(location.latLng) >= 10.0 } ?: true

                if((elapsed >= 15_000 && moved) || elapsed >= 60_000){
                    lastSentAt = now
                    lastSent = location.latLng
                    walks.document(walkId).update(
                        mapOf(
                            "position" to GeoPoint(location.latLng.latitude, location.latLng.longitude),
                            "positionAt" to FieldValue.serverTimestamp()
                        )
                    )
                }
            }
        }
    }

    suspend fun finishWalk() : FinishWalkResult {
        val walk = _activeWalk.value ?: return FinishWalkResult.NoActiveWalk
        publishJob?.cancel()
        publishJob = null
        _activeWalk.value = null

        return try {

            walks.document(walk.id).delete().await()
            FinishWalkResult.Success
        }
        catch (e: FirebaseFirestoreException){
            FinishWalkResult.Failed
        }
    }

    suspend fun cancelWalk() : CancelWalkResult {
        val walk = _activeWalk.value ?: return CancelWalkResult.NoActiveWalk
        publishJob?.cancel()
        publishJob = null
        _activeWalk.value = null

        return try {

            walks.document(walk.id).delete().await()
            CancelWalkResult.Success
        }
        catch (e: FirebaseFirestoreException){
            CancelWalkResult.Failed
        }
    }

    suspend fun sendSos() : SosResult {
        val walk = _activeWalk.value ?: return SosResult.NoActiveWalk

        return try {

            val walkRef = walks.document(walk.id)
                .get()
                .await()

            if (walkRef.getBoolean("sosActive") == true) {
                return SosResult.AlreadySent
            }

            walks.document(walk.id)
                .update("sosActive", true,
                "sosAt", FieldValue.serverTimestamp())
                .await()

            SosResult.Success
        }
        catch (e: FirebaseFirestoreException){
            Log.e("SOS", e.message, e)
            SosResult.Failed
        }
    }

    fun sosWalksUpdates(): Flow<SosWalksResult> = callbackFlow {
        val me = authRepository.currentUser.value

        if(me == null) {
            trySend(SosWalksResult.NotSignedIn)
            close()
            return@callbackFlow
        }

        val registration = walks
            .whereArrayContains("guardiansIds", me.documentId)
            .whereEqualTo("sosActive", true)
            .addSnapshotListener { snapshot, exception ->
                if(exception != null || snapshot == null) {
                    trySend(SosWalksResult.Failed)
                    return@addSnapshotListener
                }

                val sosWalks = snapshot.documents.map { doc ->
                    SosWalk(
                        doc.id,
                        doc.getString("walkerId").orEmpty()
                    )
                }

                trySend(SosWalksResult.Success(sosWalks))
            }

        awaitClose { registration.remove() }
    }
}