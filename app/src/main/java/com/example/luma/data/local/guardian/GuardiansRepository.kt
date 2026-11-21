package com.example.luma.data.local.guardian

import android.util.Log
import com.example.luma.data.local.AuthRepository
import com.example.luma.data.local.walk.Walk
import com.google.firebase.FirebaseNetworkException
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.tasks.await

data class FollowedPerson(
    val id: String,
    val name: String,
    val remainingMinutes: Int,
    val isSosActive: Boolean = false
)

class GuardiansRepository(val db: FirebaseFirestore, val authRepository: AuthRepository) {

    private val users by lazy { db.collection("user") }
    private val links by lazy { db.collection("links") }

    private val _selectedPerson = MutableStateFlow<FollowedPerson?>(null)
    val selectedPerson: StateFlow<FollowedPerson?> = _selectedPerson.asStateFlow()

    suspend fun addGuardianRequest(guardianEmail: String) : AddGuardianResult {
        val me = authRepository.currentUser.value ?: return AddGuardianResult.NotSignedIn
        val email = guardianEmail.trim().lowercase()
        if (email == me.email.trim().lowercase()) return AddGuardianResult.CannotAddSelf

        return try {
            val guardianDoc = users.whereEqualTo("email", email).limit(1).get().await()
                .documents.firstOrNull() ?: return AddGuardianResult.UserNotFound

            val linkRef = links.document("${me.documentId}_${guardianDoc.id}")
            if (linkRef.get().await().exists()) return AddGuardianResult.AlreadyAdded

            linkRef.set(
                mapOf(
                    "walkerId" to me.documentId,
                    "guardianId" to guardianDoc.id,
                    "status" to "pending",
                    "createdAt" to FieldValue.serverTimestamp(),
                    "walkerFullName" to "${me.name} ${me.lastName}".trim() ,
                    "guardianFullName" to "${guardianDoc.getString("name").orEmpty()} ${guardianDoc.getString("last name").orEmpty()}".trim(),
                )
            ).await()

            AddGuardianResult.Sent
        } catch (e: FirebaseNetworkException) {
            Log.e("Add Guardian", "No Connection" ,e)
            AddGuardianResult.NoConnection
        } catch (e: FirebaseFirestoreException) {
            Log.e("Add Guardian", "Failed" ,e)
            AddGuardianResult.Failed
        }
    }

    fun pendingRequestsUpdates() : Flow<PendingRequestsResult> = callbackFlow {
        val me = authRepository.currentUser.value
        if(me == null) {
            trySend(PendingRequestsResult.NotSignIn)
            close()
            return@callbackFlow
        }

        val registration = links
            .whereEqualTo("guardianId", me.documentId)
            .whereEqualTo("status", "pending")
            .addSnapshotListener { snapshot, exception ->
                if(exception != null || snapshot == null) {
                    trySend(PendingRequestsResult.Failed)
                    return@addSnapshotListener
                }

                val requests = snapshot.documents.map { doc ->
                    GuardianRequest(
                        linkId = doc.id,
                        walkerFullName = doc.getString("walkerFullName") ?: "Unknown",
                        createdAtMillis = doc.getTimestamp("createdAt")?.toDate()?.time ?: Long.MAX_VALUE
                    )
                }.sortedByDescending { it.createdAtMillis }

                trySend(PendingRequestsResult.Success(requests))
            }

        awaitClose { registration.remove() }
    }

    suspend fun acceptGuardianRequest(linkId: String){
        val linkDoc = links.document(linkId)
        if (!linkDoc.get().await().exists()) return

        linkDoc.update("status", "accepted")
            .addOnSuccessListener {
                /* TODO handle success */
            }
            .addOnFailureListener {
                /* TODO handle failure */
            }
    }

    suspend fun declineGuardianRequest(linkId: String){
        val linkDoc = links.document(linkId)
        if (!linkDoc.get().await().exists()) return

        linkDoc.delete()
            .addOnSuccessListener {
                /* TODO handle success */
            }
            .addOnFailureListener {
                /* TODO handle failure */
            }
    }

    suspend fun removeGuardianLink(linkId: String){
        val linkDoc = links.document(linkId)
        if (!linkDoc.get().await().exists()) return

        linkDoc.delete()
            .addOnSuccessListener {
                /* TODO handle success */
            }
            .addOnFailureListener {
                /* TODO handle failure */
            }
    }

    fun guardiansUpdates() : Flow<GuardiansResult> = callbackFlow {
        val me = authRepository.currentUser.value
        if(me == null) {
            trySend(GuardiansResult.NotSignIn)
            close()
            return@callbackFlow
        }

        val registration = links
            .whereEqualTo("walkerId", me.documentId)
            .whereEqualTo("status", "accepted")
            .addSnapshotListener { snapshot, exception ->
                if(exception != null || snapshot == null) {
                    trySend(GuardiansResult.Failed)
                    return@addSnapshotListener
                }

                val guardians = snapshot.documents.map { doc ->
                    Guardian(
                        fullName = doc.getString("guardianFullName").orEmpty(),
                        linkId = doc.id

                    )
                }.sortedBy { it.fullName }

                trySend(GuardiansResult.Success(guardians))
            }

        awaitClose { registration.remove() }
    }

    fun guardedUpdates() : Flow<GuardedResult> = callbackFlow {
        val me = authRepository.currentUser.value
        if(me == null) {
            trySend(GuardedResult.NotSignIn)
            close()
            return@callbackFlow
        }

        val registration = links
            .whereEqualTo("guardianId", me.documentId)
            .whereEqualTo("status", "accepted")
            .addSnapshotListener { snapshot, exception ->
                if(exception != null || snapshot == null) {
                    trySend(GuardedResult.Failed)
                    return@addSnapshotListener
                }

                val guarded = snapshot.documents.map { doc ->
                    Guarded(
                        doc.getString("walkerFullName").orEmpty(),
                        doc.id
                    )
                }.sortedBy{ it.fullName }

                trySend(GuardedResult.Success(guarded))
            }

        awaitClose { registration.remove() }
    }

    fun selectPerson(person: FollowedPerson) {
        _selectedPerson.update { person }
    }

    fun clearSelectedPerson() {
        _selectedPerson.update { null }
    }

    fun updateRemainingTime(personId: String, remainingMinutes: Int) {

    }

    fun onSosReceived(personId: String) {

    }

    fun findFollowedPersonByName(name: String): FollowedPerson? {
        return null
    }
}
