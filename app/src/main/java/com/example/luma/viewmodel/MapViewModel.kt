package com.example.luma.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.luma.data.LatLng
import com.example.luma.data.LocationRepository
import com.example.luma.data.MapRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

sealed interface MapUiState{
    data class SelectingDestination(
        val query: String = "",
        val matches: List<String>,
        val selectedDestination: String? = null,
    ) : MapUiState {
        val canConfirm: Boolean get() = selectedDestination != null
    }
    data class SelectingGuardians(
        val destination: String
    ) : MapUiState
}

class MapViewModel(
    private val mapRepository: MapRepository,
    private val locationRepository: LocationRepository
) : ViewModel() {

    // TODO Move construction of repository in the application container

    private val _userPosition = MutableStateFlow<LatLng?>(null)
    val userPosition: StateFlow<LatLng?> = _userPosition.asStateFlow()

    private var locationJob: Job? = null

    private val _state = MutableStateFlow<MapUiState>(MapUiState.SelectingDestination("", listOf()))
    val state: StateFlow<MapUiState> = _state.asStateFlow()

    fun onLocationPermissionResult(granted: Boolean) {
        if (granted && locationJob == null) {
            locationJob = viewModelScope.launch {
                locationRepository.locationUpdates()
                    .collect { position -> _userPosition.value = position }
            }
        }
    }

    fun onQueryChange(newQuery: String){
        val newMatches = mapRepository.getMatches(newQuery)

        _state.update { current ->
            if (current is MapUiState.SelectingDestination) {
                current.copy(query = newQuery, matches = newMatches, selectedDestination = null)
            } else {
                current
            }
        }
    }

    fun onMatchSelected(match: String){
        _state.update { current ->
            if (current is MapUiState.SelectingDestination) {
                current.copy(query = match, matches = listOf(), selectedDestination = match)
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