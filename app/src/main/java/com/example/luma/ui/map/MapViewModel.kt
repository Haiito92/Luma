package com.example.luma.ui.map

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.luma.data.local.LocationRepository
import com.example.luma.data.local.MapRepository
import com.example.luma.data.local.Place
import com.example.luma.data.local.RouteResult
import com.example.luma.data.local.SearchResult
import com.example.luma.data.local.walk.StartWalkResult
import com.example.luma.data.local.UserLocation
import com.example.luma.data.local.walk.WalkRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.milliseconds

class MapViewModel(
    private val mapRepository: MapRepository,
    private val locationRepository: LocationRepository,
    private val walkRepository: WalkRepository
) : ViewModel() {

    private val _userLocation = MutableStateFlow<UserLocation?>(null)
    val userLocation: StateFlow<UserLocation?> = _userLocation.asStateFlow()

    private val _selectionState = MutableStateFlow<MapUiState>(MapUiState.SelectingDestination("", listOf()))
    val state: StateFlow<MapUiState> = combine(
        _selectionState,
        walkRepository.activeWalk
    ) { selection, walk ->
        if (walk != null) MapUiState.WalkInProgress(walk) else selection
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = walkRepository.activeWalk.value
            ?.let { MapUiState.WalkInProgress(it) }
            ?: _selectionState.value
    )


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
        _selectionState.update { current ->
            if (current is MapUiState.SelectingDestination) {
                current.copy(query = newQuery, selectedDestination = null, routeStatus = RouteStatus.Idle)
            } else {
                current
            }
        }

        searchJob?.cancel()

        if (!mapRepository.isSearchable(newQuery)) {
            updateMatches(emptyList())     // clear the suggestions right away, no waiting
            return
        }

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
        _selectionState.update { current ->
            if (current is MapUiState.SelectingDestination) current.copy(matches = matches)
            else current
        }
    }

    fun onMatchSelected(match: Place){
        routeJob?.cancel()

        val currentUserLocation = _userLocation.value

        _selectionState.update { current ->
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

            _selectionState.update { current ->
                // ignore a late answer for a place the user has since changed
                if (current is MapUiState.SelectingDestination && current.selectedDestination == match)
                    current.copy(routeStatus = routeStatus)
                else current
            }
        }
    }

    fun onDestinationConfirmed() {
        val currentState = _selectionState.value as? MapUiState.SelectingDestination ?: return;
        val destination = currentState.selectedDestination ?: return
        val readyRoute = currentState.readyRoute ?: return

        viewModelScope.launch {
            when (val result = walkRepository.startWalk(destination, readyRoute)) {
                is StartWalkResult.Started -> { /* TODO Handle this case */ }
                is StartWalkResult.AlreadyInProgress -> { /* TODO Handle this case */ }
                is StartWalkResult.InvalidRoute -> { /* TODO Handle this case */ }
                is StartWalkResult.NotSignedIn -> { /* TODO Handle this case */ }
                is StartWalkResult.Failed -> { /* TODO Handle this case */ }
            }
        }
    }

    fun onWalkCanceled(){
        viewModelScope.launch {
            walkRepository.cancelWalk()
        }
    }

    fun onWalkFinished(){
        viewModelScope.launch {
            walkRepository.finishWalk()
        }
    }

    fun onSosSent(){
        walkRepository.sendSos()
    }
}