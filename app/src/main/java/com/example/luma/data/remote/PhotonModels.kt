package com.example.luma.data.remote

import kotlinx.serialization.Serializable

@Serializable
data class PhotonResponse(val features: List<PhotonFeature>)

@Serializable
data class PhotonFeature(val geometry: PhotonGeometry, val properties: PhotonProperties)

@Serializable
data class PhotonGeometry(val coordinates: List<Double>)     // [longitude, latitude]

@Serializable
data class PhotonProperties(
    val name: String? = null,
    val street: String? = null,
    val housenumber: String? = null,
    val city: String? = null,
    val country: String? = null
)
