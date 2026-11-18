package com.example.luma.data.local

sealed interface RouteResult {
    data class Success(val route: Route) : RouteResult
    object NoRoute : RouteResult              // no walking path between the points
    object NoConnection : RouteResult
    object ServiceUnavailable : RouteResult
}