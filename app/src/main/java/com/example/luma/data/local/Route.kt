package com.example.luma.data.local

data class Route(
    val points: List<LatLng>, // path
    val distanceMeters: Double,
    val durationSeconds: Double
)
