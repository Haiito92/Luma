package com.example.luma.data

data class Route(
    val points: List<LatLng>, // path
    val distanceMeters: Double,
    val durationSeconds: Double
)
