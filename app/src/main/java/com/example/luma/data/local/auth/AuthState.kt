package com.example.luma.data.local.auth

sealed interface AuthState {
    data object Loading : AuthState
    data object Unauthenticated : AuthState
    data class Authenticated(val user: LumaUser) : AuthState
    data class Error(val message: String) : AuthState
}