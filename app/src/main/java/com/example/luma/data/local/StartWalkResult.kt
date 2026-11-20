package com.example.luma.data.local

sealed interface StartWalkResult {
    data class Started(val walk: Walk) : StartWalkResult
    object AlreadyInProgress : StartWalkResult
    object InvalidRoute : StartWalkResult
}