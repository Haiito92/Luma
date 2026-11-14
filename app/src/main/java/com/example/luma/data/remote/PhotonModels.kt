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

// Example of a JSON returned by Photon so we understand why the data classes are how they are
//{
//    "type": "FeatureCollection",
//    "features": [
//    {
//        "type": "Feature",
//        "geometry": {
//        "type": "Point",
//        "coordinates": [-73.5673, 45.5017]
//    },
//        "properties": {
//        "osm_id": 123456,
//        "osm_type": "N",
//        "osm_key": "amenity",
//        "osm_value": "library",
//        "name": "Central Library",
//        "housenumber": "350",
//        "street": "Main Street",
//        "city": "Montreal",
//        "state": "Quebec",
//        "country": "Canada"
//    }
//    },
//    {
//        "type": "Feature",
//        "geometry": {
//        "type": "Point",
//        "coordinates": [-73.6012, 45.5230]
//    },
//        "properties": {
//        "osm_id": 789012,
//        "osm_type": "W",
//        "osm_key": "amenity",
//        "osm_value": "library",
//        "name": "Neighborhood Library",
//        "country": "Canada"
//    }
//    }
//    ]
//}