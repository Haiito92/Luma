package com.example.luma.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.luma.data.LocationRepository
import com.example.luma.data.MapRepository
import com.example.luma.data.Place
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

sealed interface MapUiState{
    data class SelectingDestination(
        val query: String = "",
        val matches: List<Place>,
        val selectedDestination: Place? = null,
    ) : MapUiState {
        val canConfirm: Boolean get() = selectedDestination != null
    }
    data class SelectingGuardians(
        val destination: Place
    ) : MapUiState
}

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
                current.copy(query = newQuery, selectedDestination = null)
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
                SearchResult.NoConnection -> { /* TODO SHOW ERROR MESSAGE */ }
                SearchResult.ServiceUnavailable -> { /* TODO SHOW ERROR MESSAGE */ }
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
        _state.update { current ->
            if (current is MapUiState.SelectingDestination) {
                current.copy(query = match.name, matches = listOf(), selectedDestination = match)
            } else {
                current
            }
        }
    }

    fun onDestinationConfirmed(){
        val currentState = _state.value as? MapUiState.SelectingDestination ?: return
        val destination = currentState.selectedDestination ?: return

        _state.update { MapUiState.SelectingGuardians(destination) }
    }
}