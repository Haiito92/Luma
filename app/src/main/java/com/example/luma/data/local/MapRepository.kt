package com.example.luma.data.local

import android.util.Log
import com.example.luma.data.remote.OsmrApi
import com.example.luma.data.remote.PhotonApi
import kotlinx.io.IOException
import kotlinx.serialization.SerializationException
import retrofit2.HttpException


class MapRepository(val photonApi: PhotonApi, val osmrApi: OsmrApi) {

    fun isSearchable(query: String): Boolean = query.trim().length >= 3

    suspend fun search(query: String): SearchResult {
        if (!isSearchable(query)) return SearchResult.Success(emptyList())

        return try {
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

            SearchResult.Success(filtered)

        } catch (e: IOException) {
            Log.e("Search", "No connection", e)
            SearchResult.NoConnection
        } catch (e: HttpException) {
            val body = e.response()?.errorBody()?.string()
            Log.e("Search", "HTTP ${e.code()}: $body", e)
            SearchResult.ServiceUnavailable
        } catch (e: SerializationException) {
            Log.e("Search", "Bad JSON", e)
            SearchResult.ServiceUnavailable
        }
    }

    suspend fun route(from: LatLng, to: LatLng): RouteResult{
        val coordinates = "${from.longitude},${from.latitude};${to.longitude},${to.latitude}"

        return try {
            val response = osmrApi.route(coordinates)
            val best = response.routes.firstOrNull() ?: return RouteResult.NoRoute

            return RouteResult.Success(Route(
                points = best.geometry.coordinates.map { LatLng(latitude = it[1], longitude = it[0] )},
                distanceMeters = best.distance,
                durationSeconds = best.duration
            ))

        } catch (e: IOException) {
            RouteResult.NoConnection
        } catch (e: HttpException) {
            RouteResult.ServiceUnavailable
        } catch (e: SerializationException) {
            RouteResult.ServiceUnavailable
        }

    }
}