package com.example.luma.data.local

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class WalkRepository {

    private val _activeWalk = MutableStateFlow<Walk?>(null)
    val activeWalk : StateFlow<Walk?> = _activeWalk.asStateFlow()

    fun startWalk(destination: Place, route: Route){

    }
}