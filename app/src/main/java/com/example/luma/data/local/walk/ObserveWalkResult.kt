package com.example.luma.data.local.walk

sealed interface ObserveWalkResult {
    data class Success(val walk: Walk) : ObserveWalkResult
    data object Failed : ObserveWalkResult
    data object WalkNotFound : ObserveWalkResult
}