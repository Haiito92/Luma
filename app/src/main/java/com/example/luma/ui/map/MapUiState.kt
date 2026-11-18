package com.example.luma.ui.map

import com.example.luma.data.Place
import com.example.luma.data.Route

sealed interface RouteStatus{
    object Idle : RouteStatus
    object Loading : RouteStatus
    object NoLocation : RouteStatus
    object NoRoute : RouteStatus
    object NoConnection : RouteStatus
    object Failed : RouteStatus
    data class Ready(val route: Route) : RouteStatus
}

sealed interface MapUiState{
    data class SelectingDestination(
        val query: String = "",
        val matches: List<Place>,
        val selectedDestination: Place? = null,
        val routeStatus: RouteStatus = RouteStatus.Idle
    ) : MapUiState {
        val canConfirm: Boolean get() = selectedDestination != null
    }
    data class SelectingGuardians(
        val destination: Place,
        val route: Route
    ) : MapUiState

    data class WalkInProgress(
        val destination: Place,
        val route: Route
    ) : MapUiState
}