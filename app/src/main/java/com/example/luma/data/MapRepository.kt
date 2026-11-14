package com.example.luma.data

import com.example.luma.data.remote.PhotonApi
import kotlinx.io.IOException
import kotlinx.serialization.SerializationException
import retrofit2.HttpException

data class Place(
    val name: String,
    val latLng: LatLng
)

sealed interface SearchResult {
    data class Success(val places: List<Place>) : SearchResult
    object NoConnection : SearchResult
    object ServiceUnavailable : SearchResult      // server error, throttled, bad data
}

class MapRepository(val photonApi: PhotonApi) {

    suspend fun search(query: String): SearchResult {
        try {
            val trimmed = query.trim()
            val response = photonApi.search(trimmed)

            val filtered = response.features.mapNotNull { feature ->
                val name = feature.properties.name ?: return@mapNotNull null

                val label = listOfNotNull(name, feature.properties.city, feature.properties.country)
                    .joinToString(", ")

                Place(
                    name = label,
                    latLng = LatLng(feature.geometry.coordinates[1], feature.geometry.coordinates[0])
                )
            }

            return SearchResult.Success(filtered)

        }   catch (e: IOException) {
            return SearchResult.NoConnection
        } catch (e: HttpException) {
            return SearchResult.ServiceUnavailable
        } catch (e: SerializationException) {
            return SearchResult.ServiceUnavailable
        }
    }
}