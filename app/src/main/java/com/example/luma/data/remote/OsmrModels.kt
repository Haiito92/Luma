package com.example.luma.data.remote
import kotlinx.serialization.Serializable

@Serializable
data class OsrmResponse(val code: String, val routes: List<OsrmRoute> = emptyList())

@Serializable
data class OsrmRoute(val distance: Double, val duration: Double, val geometry: OsrmGeometry)

@Serializable
data class OsrmGeometry(val coordinates: List<List<Double>>)   // each item: [longitude, latitude]