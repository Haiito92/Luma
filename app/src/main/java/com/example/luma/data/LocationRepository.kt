package com.example.luma.data

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Looper
import androidx.core.content.ContextCompat
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

data class UserLocation(
    val latLng: LatLng,
    val accuracy: Float
)

data class LatLng(
    val latitude: Double,
    val longitude: Double
)


class LocationRepository(val context: Context) {

    private val client = LocationServices.getFusedLocationProviderClient(context)

    fun locationUpdates(): Flow<UserLocation> = callbackFlow {

        val granted = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) ==
                PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) ==
                PackageManager.PERMISSION_GRANTED

        if(!granted) { close(); return@callbackFlow }

        val request = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 5_000).build()
        val callback = object : LocationCallback(){
            override fun onLocationResult(result: LocationResult) {
                result.lastLocation?.let { trySend(UserLocation(LatLng(it.latitude, it.longitude), it.accuracy)) }
            }
        }

        client.requestLocationUpdates(request, callback, Looper.getMainLooper())
        awaitClose { client.removeLocationUpdates(callback) }
    }
}