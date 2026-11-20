package com.example.luma.data.local

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class WalkRepository {

    private val _activeWalk = MutableStateFlow<Walk?>(null)
    val activeWalk : StateFlow<Walk?> = _activeWalk.asStateFlow()

    fun startWalk(destination: Place, route: Route): StartWalkResult {

        if (_activeWalk.value != null) return StartWalkResult.AlreadyInProgress
        if (route.points.size < 2 ) return StartWalkResult.InvalidRoute

        val walk = Walk(destination, route)
        _activeWalk.value = walk
        return StartWalkResult.Started(walk)
    }

    fun finishWalk() : FinishWalkResult {
        _activeWalk.value = null
        return FinishWalkResult.Success
    }

    fun cancelWalk() : CancelWalkResult {
        _activeWalk.value = null
        return CancelWalkResult.Success
    }

    fun sendSos() : SosResult {
        return SosResult.Success
    }
}