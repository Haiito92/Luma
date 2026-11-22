package com.example.luma.ui.app

import androidx.lifecycle.ViewModel
import com.example.luma.data.local.auth.AuthRepository
import com.example.luma.data.local.auth.AuthState
import kotlinx.coroutines.flow.StateFlow

class AppViewModel (
    private val authRepository: AuthRepository
): ViewModel() {

    val authState: StateFlow<AuthState> = authRepository.authState
}