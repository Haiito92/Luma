package com.example.luma.data.remote

import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface OsmrApi {
    @GET("routed-foot/route/v1/driving/{coordinates}")
    suspend fun route(
        @Path("coordinates", encoded = true) coordinates: String,
        @Query("overview") overview: String = "full",
        @Query("geometries") geometries: String = "geojson"
    ): OsrmResponse
}