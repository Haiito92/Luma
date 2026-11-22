package com.example.luma.ui.auth

import androidx.lifecycle.ViewModel
import com.example.luma.data.local.auth.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow


class AuthViewModel(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _authUiState = MutableStateFlow<AuthUiState>(AuthUiState.Idle)
    val authUiState: StateFlow<AuthUiState> = _authUiState.asStateFlow()

    fun onLogin(email: String, password: String) {
        if (email.isBlank() || password.isBlank()) {
            _authUiState.value = AuthUiState.Error("Please fill in all fields.")
            return
        }

        _authUiState.value = AuthUiState.Loading

        authRepository.login(email, password) { result ->
            result.onSuccess {
            }.onFailure { exception ->
                _authUiState.value = AuthUiState.Error( exception.message ?: "Login failed.")
            }
        }
    }

    fun onSignUp(
        name: String,
        lastName: String,
        email: String,
        password: String
    ) {
        if (name.isBlank() || lastName.isBlank() || email.isBlank() || password.isBlank()) {
            _authUiState.value = AuthUiState.Error("Please fill in all fields.")
            return
        }

        _authUiState.value = AuthUiState.Loading

        authRepository.signUp(name, lastName, email, password) { result ->
            result.onSuccess {
            }.onFailure { exception ->
                _authUiState.value = AuthUiState.Error(exception.message ?: "Account creation failed.")
            }
        }
    }
}
