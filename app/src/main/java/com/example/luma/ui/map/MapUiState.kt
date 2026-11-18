package com.example.luma.ui.map

import com.example.luma.data.local.Place
import com.example.luma.data.local.Route

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

fun MapUiState.routeOrNull(): Route? {
    return when (this){
        is MapUiState.SelectingDestination -> (routeStatus as? RouteStatus.Ready)?.route
        is MapUiState.SelectingGuardians -> route
        is MapUiState.WalkInProgress -> route
    }
}