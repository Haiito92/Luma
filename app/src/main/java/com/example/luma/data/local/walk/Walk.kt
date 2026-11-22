package com.example.luma.data.local.walk

import com.example.luma.data.local.LatLng
import com.example.luma.data.local.Place
import com.example.luma.data.local.Route
import com.example.luma.data.local.UserLocation

data class Walk(
    val id: String,
    val destination: Place,
    val route: Route,
    val walkerLocation: LatLng
)
