package com.example.luma.viewmodel

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

sealed interface MapState{
    object SelectingDestination : MapState
    object SelectingGuardians : MapState
}

class MapViewModel : ViewModel() {
    val _destination = MutableStateFlow("")
    val destination: StateFlow<String> = _destination.asStateFlow()

    val _state = MutableStateFlow<MapState>(MapState.SelectingDestination)
    val state: StateFlow<MapState> = _state.asStateFlow()

    fun onDestinationConfirmed(){
        _state.update { MapState.SelectingGuardians }
    }
}