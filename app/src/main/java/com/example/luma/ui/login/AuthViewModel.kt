package com.example.luma.ui.login

import androidx.lifecycle.ViewModel
import com.example.luma.data.local.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

sealed interface AuthUiState {
    object Idle : AuthUiState
    object Loading : AuthUiState
    object Authenticated : AuthUiState
    data class Error(val message: String) : AuthUiState
}

class AuthViewModel(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _state = MutableStateFlow<AuthUiState>(AuthUiState.Idle)
    val state: StateFlow<AuthUiState> = _state.asStateFlow()

    fun onLogin(email: String, password: String) {
        if (email.isBlank() || password.isBlank()) {
            _state.value = AuthUiState.Error("Veuillez remplir l'email et le mot de passe")
            return
        }

        _state.value = AuthUiState.Loading
        authRepository.login(email, password) { result ->
            result.onSuccess {
                _state.value = AuthUiState.Authenticated
            }.onFailure { exception ->
                _state.value = AuthUiState.Error(
                    exception.message ?: "Erreur lors de la connexion"
                )
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
            _state.value = AuthUiState.Error("Veuillez remplir tous les champs")
            return
        }

        _state.value = AuthUiState.Loading
        authRepository.signUp(name, lastName, email, password) { result ->
            result.onSuccess {
                _state.value = AuthUiState.Authenticated
            }.onFailure { exception ->
                _state.value = AuthUiState.Error(
                    exception.message ?: "Erreur lors de la création du compte"
                )
            }
        }
    }

    fun resetState() {
        _state.value = AuthUiState.Idle
    }
}
