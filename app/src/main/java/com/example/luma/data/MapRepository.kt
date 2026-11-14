package com.example.luma.data

data class Place(
    val name: String,
    val latLng: LatLng
)

class MapRepository {
    val places = listOf(Place("Home", LatLng(0.0,0.0)), Place("Gym", LatLng(100.0,100.0)))

    fun getMatches(query: String): List<Place> {
        if (query.isBlank()) return listOf()
        return places.filter { it.name.contains(query, ignoreCase = true) }.sortedBy { it.name }
    }
}