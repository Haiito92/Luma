package com.example.luma.data.local.walk

import com.example.luma.data.local.Place
import com.example.luma.data.local.Route

data class Walk(
    val id: String,
    val destination: Place,
    val route: Route
)
