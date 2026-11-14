package com.example.luma.data

import com.example.luma.data.remote.PhotonApi

data class Place(
    val name: String,
    val latLng: LatLng
)

class MapRepository(val photonApi: PhotonApi) {

    suspend fun search(query: String): List<Place> {
        val response = photonApi.search(query)

        return response.features.mapNotNull { feature ->
            val name = feature.properties.name ?: return@mapNotNull null

            val label = listOfNotNull(name, feature.properties.city, feature.properties.country)
                .joinToString(", ")

            Place(
                name = label,
                latLng = LatLng(feature.geometry.coordinates[1], feature.geometry.coordinates[0])
            )
        }
    }
}