package com.example.luma.repository

class MapRepository {
    val places = listOf("Home", "Pool", "Gym", "Graveyard", "School", "Shooting Range", "Apothecary", "Zoo")

    fun getMatches(query: String): List<String> {
        if (query.isBlank()) return listOf()
        return places.filter { it.contains(query, ignoreCase = true) }.sorted()
    }
}