package com.example.luma.data.local.walk

sealed interface StartWalkResult {
    data class Started(val walk: Walk) : StartWalkResult
    object AlreadyInProgress : StartWalkResult
    object InvalidRoute : StartWalkResult
    object NotSignedIn : StartWalkResult
    object Failed : StartWalkResult
}