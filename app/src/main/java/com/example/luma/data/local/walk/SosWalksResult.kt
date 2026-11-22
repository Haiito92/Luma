package com.example.luma.data.local.walk

sealed interface SosWalksUpdateResult {
    data class Success(val sosWalks: List<SosWalk>) : SosWalksUpdateResult
    data object NotSignedIn : SosWalksUpdateResult
    data object Failed : SosWalksUpdateResult
}