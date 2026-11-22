package com.example.luma.data.local.walk

sealed interface StartWalkResult {
    data class Started(val walk: Walk) : StartWalkResult
    data object AlreadyInProgress : StartWalkResult
    data object InvalidRoute : StartWalkResult
    data object NotSignedIn : StartWalkResult
    data object Failed : StartWalkResult
}