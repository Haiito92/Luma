package com.example.luma.data

import android.content.Context
import com.example.luma.data.local.AuthRepository
import com.example.luma.data.local.guardian.GuardiansRepository
import com.example.luma.data.local.LocationRepository
import com.example.luma.data.local.MapRepository
import com.example.luma.data.local.SettingsRepository
import com.example.luma.data.local.walk.WalkRepository
import com.example.luma.data.remote.OsmrApi
import com.example.luma.data.remote.PhotonApi
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.serialization.json.Json
import okhttp3.Dispatcher
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory

class AppContainer(context: Context) {

    // Scope to launch coroutine where there is no predifined scopes
    private val appScope = CoroutineScope(SupervisorJob() + Dispatchers.Main)

    private val db = FirebaseFirestore.getInstance()

    private val httpClient = OkHttpClient.Builder()
        .addInterceptor { chain ->
            chain.proceed(
                chain.request().newBuilder()
                    .header("User-Agent", "Luma/1.0 (student project; hannaantoine2002@gmail.com)")
                    .build()
            )
        }
        .build()

    private val photonApi: PhotonApi = Retrofit.Builder()
        .baseUrl("https://photon.komoot.io/")
        .client(httpClient)
        .addConverterFactory(
            Json { ignoreUnknownKeys = true }.asConverterFactory("application/json".toMediaType())
        )
        .build()
        .create(PhotonApi::class.java)

    private val osmrApi: OsmrApi = Retrofit.Builder()
        .baseUrl("https://routing.openstreetmap.de/")
        .client(httpClient)
        .addConverterFactory(
            Json {ignoreUnknownKeys = true}.asConverterFactory("application/json".toMediaType())
        )
        .build()
        .create(OsmrApi::class.java)


    val mapRepository = MapRepository(photonApi, osmrApi)

    val locationRepository = LocationRepository(context.applicationContext)

    val authRepository = AuthRepository(db)

    val guardiansRepository = GuardiansRepository(db, authRepository)

    val walkRepository = WalkRepository(authRepository, locationRepository, guardiansRepository, appScope, db)

    val settingsRepository = SettingsRepository()

}
