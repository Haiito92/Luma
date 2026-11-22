package com.example.luma.utils

import com.example.luma.data.local.LatLng
import com.example.luma.data.local.Route
import com.example.luma.data.local.UserLocation
import org.maplibre.spatialk.geojson.BoundingBox

fun UserLocation?.toGeoJson(): String =
    this?.latLng?.toPointGeoJson() ?: """{"type":"FeatureCollection","features":[]}"""

fun LatLng?.toPointGeoJson(): String =
    if (this == null) {
        """{"type":"FeatureCollection","features":[]}"""
    } else {
        """{"type":"Feature","geometry":{"type":"Point","coordinates":[$longitude,$latitude]},"properties":{}}"""
    }

fun Route?.toGeoJson(): String =
    if (this == null || points.size < 2) {
        """{"type":"FeatureCollection","features":[]}"""
    } else {
        val coordinates = points.joinToString(",") { "[${it.longitude},${it.latitude}]" }
        """{"type":"Feature","geometry":{"type":"LineString","coordinates":[$coordinates]},"properties":{}}"""
    }

fun Route.toBoundingBox(): BoundingBox {
    val lats = points.map { it.latitude }
    val lngs = points.map { it.longitude }
    return BoundingBox(
        west = lngs.min(),
        south = lats.min(),
        east = lngs.max(),
        north = lats.max()
    )
}