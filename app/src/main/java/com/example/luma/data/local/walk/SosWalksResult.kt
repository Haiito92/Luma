package com.example.luma.data.local.walk

sealed interface SosWalksResult {
    data class Success(val sosWalks: List<SosWalk>) : SosWalksResult
    data object NotSignedIn : SosWalksResult
    data object Failed : SosWalksResult
}