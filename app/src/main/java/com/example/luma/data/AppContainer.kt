package com.example.luma.data

import android.content.Context
import com.example.luma.data.local.LocationRepository
import com.example.luma.data.local.MapRepository
import com.example.luma.data.local.WalkRepository
import com.example.luma.data.remote.OsmrApi
import com.example.luma.data.remote.PhotonApi
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory

class AppContainer(context: Context) {
    private val photonApi: PhotonApi = Retrofit.Builder()
        .baseUrl("https://photon.komoot.io/")
        .addConverterFactory(
            Json { ignoreUnknownKeys = true }.asConverterFactory("application/json".toMediaType())
        )
        .build()
        .create(PhotonApi::class.java)

    private val osmrApi: OsmrApi = Retrofit.Builder()
        .baseUrl("https://routing.openstreetmap.de/")
        .addConverterFactory(
            Json {ignoreUnknownKeys = true}.asConverterFactory("application/json".toMediaType())
        )
        .build()
        .create(OsmrApi::class.java)


    val mapRepository = MapRepository(photonApi, osmrApi)

    val locationRepository = LocationRepository(context.applicationContext)

    val walkRepository = WalkRepository()
}