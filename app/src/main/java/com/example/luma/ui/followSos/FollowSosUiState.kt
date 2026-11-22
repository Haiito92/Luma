package com.example.luma.ui.followSos

import com.example.luma.data.local.Route
import com.example.luma.data.local.walk.Walk
import com.example.luma.ui.map.MapUiState
import com.example.luma.ui.map.RouteStatus

sealed interface FollowSosUiState {
    data object Loading : FollowSosUiState
    data class Success(val walk: Walk) : FollowSosUiState
    data object WalkNotFound : FollowSosUiState
    data object Error : FollowSosUiState
}

fun FollowSosUiState.walkOrNull(): Walk? {
    return when (this){
        is FollowSosUiState.Loading -> null
        is FollowSosUiState.Success -> walk
        is FollowSosUiState.WalkNotFound -> null
        is FollowSosUiState.Error -> null
    }
}