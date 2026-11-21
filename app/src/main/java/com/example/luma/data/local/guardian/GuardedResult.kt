package com.example.luma.data.local.guardian

sealed interface GuardedResult {
    data class Success(val guarded: List<Guarded>) : GuardedResult
    object Failed : GuardedResult
    object NotSignIn : GuardedResult
}