package com.example.luma.ui.map

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.luma.data.LocationRepository
import com.example.luma.data.MapRepository
import com.example.luma.data.Place
import com.example.luma.data.Route
import com.example.luma.data.RouteResult
import com.example.luma.data.SearchResult
import com.example.luma.data.UserLocation
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.milliseconds

class MapViewModel(
    private val mapRepository: MapRepository,
    private val locationRepository: LocationRepository
) : ViewModel() {

    private val _userLocation = MutableStateFlow<UserLocation?>(null)
    val userLocation: StateFlow<UserLocation?> = _userLocation.asStateFlow()

    private val _state = MutableStateFlow<MapUiState>(MapUiState.SelectingDestination("", listOf()))
    val state: StateFlow<MapUiState> = _state.asStateFlow()


    private var locationJob: Job? = null
    private var searchJob: Job? = null

    private var routeJob: Job? = null

    fun onLocationPermissionResult(granted: Boolean) {
        if (granted && locationJob == null) {
            locationJob = viewModelScope.launch {
                locationRepository.locationUpdates()
                    .collect { position -> _userLocation.value = position }
            }
        }
    }

    fun onQueryChange(newQuery: String){

        // We do a first update for the text to not lag
        _state.update { current ->
            if (current is MapUiState.SelectingDestination) {
                current.copy(query = newQuery, selectedDestination = null, routeStatus = RouteStatus.Idle)
            } else {
                current
            }
        }

        searchJob?.cancel()

        // Start a new search, after a pause
        searchJob = viewModelScope.launch {
            delay(500.milliseconds)
            val result = mapRepository.search(newQuery)
            when (result) {
                is SearchResult.Success -> updateMatches(result.places)
                is SearchResult.NoConnection -> { /* TODO SHOW ERROR MESSAGE */ }
                is SearchResult.ServiceUnavailable -> { /* TODO SHOW ERROR MESSAGE */ }
            }
        }
    }

    private fun updateMatches(matches: List<Place>) {
        _state.update { current ->
            if (current is MapUiState.SelectingDestination) current.copy(matches = matches)
            else current
        }
    }

    fun onMatchSelected(match: Place){
        routeJob?.cancel()

        val currentUserLocation = _userLocation.value

        _state.update { current ->
            if (current is MapUiState.SelectingDestination) {
                current.copy(
                    query = match.name,
                    matches = emptyList(),
                    selectedDestination = match,
                    routeStatus = if(currentUserLocation == null) RouteStatus.NoLocation else RouteStatus.Loading)
            } else {
                current
            }
        }

        if (currentUserLocation == null) return

        routeJob = viewModelScope.launch {
            delay(500.milliseconds)
            val routeResult = mapRepository.route(currentUserLocation.latLng, match.latLng)

            val routeStatus = when (routeResult){
                is RouteResult.Success -> RouteStatus.Ready(routeResult.route)
                is RouteResult.NoRoute -> RouteStatus.NoRoute
                is RouteResult.NoConnection -> RouteStatus.NoConnection
                is RouteResult.ServiceUnavailable -> RouteStatus.Failed
            }

            _state.update { current ->
                // ignore a late answer for a place the user has since changed
                if (current is MapUiState.SelectingDestination && current.selectedDestination == match)
                    current.copy(routeStatus = routeStatus)
                else current
            }
        }
    }

    fun onDestinationConfirmed(){
        val currentState = _state.value as? MapUiState.SelectingDestination ?: return
        val destination = currentState.selectedDestination ?: return

        when (currentState.routeStatus){
            is RouteStatus.Ready -> {
                // TODO Go to guardian selection instead of going straight to walk
                // _state.update { MapUiState.SelectingGuardians(destination, Route(emptyList(), 0.0, 0.0)) }

                _state.update { MapUiState.WalkInProgress(destination, currentState.routeStatus.route) }
            }
            /* TODO handle other cases */
            is RouteStatus.Idle -> {}
            is RouteStatus.Loading -> {}
            is RouteStatus.Failed -> {}
            is RouteStatus.NoRoute -> {}
            is RouteStatus.NoLocation -> {}
            is RouteStatus.NoConnection -> {}
        }}
}