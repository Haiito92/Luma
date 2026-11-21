package com.example.luma.data.local

import kotlin.math.asin
import kotlin.math.cos
import kotlin.math.pow
import kotlin.math.sin
import kotlin.math.sqrt

data class LatLng(
    val latitude: Double,
    val longitude: Double
)
private const val EarthRadiusMeters = 6_371_000.0

fun LatLng.distanceMeters(other: LatLng) : Double {
    val lat1 = Math.toRadians(this.latitude)
    val lat2 = Math.toRadians(other.latitude)
    val dLat = lat2 - lat1
    val dLng = Math.toRadians(other.longitude - this.longitude)

    val h = sin(dLat / 2).pow(2) + cos(lat1) * cos(lat2) * sin(dLng / 2).pow(2)
    return 2 * EarthRadiusMeters * asin(sqrt(h))
}
