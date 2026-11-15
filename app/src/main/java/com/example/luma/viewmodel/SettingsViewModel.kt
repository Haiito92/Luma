package com.example.luma.viewmodel

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class SettingsViewModel : ViewModel() {
    private val _shareLocation = MutableStateFlow(true)
    val shareLocation: StateFlow<Boolean> = _shareLocation.asStateFlow()

    private val _nightMode = MutableStateFlow(false)
    val nightMode: StateFlow<Boolean> = _nightMode.asStateFlow()

    val colorBlindnessOptions: List<String> = listOf(
        "None",
        "Protanopia",
        "Deuteranopia",
        "Tritanopia"
    )

    private val _selectedColorBlindness = MutableStateFlow(colorBlindnessOptions.first())
    val selectedColorBlindness: StateFlow<String> = _selectedColorBlindness.asStateFlow()

    fun onShareLocationChanged(enabled: Boolean) {
        _shareLocation.update { enabled }
        // TODO: Activer/désactiver le partage de localisation en arrière-plan
    }

    fun onNightModeChanged(enabled: Boolean) {
        _nightMode.update { enabled }
    }

    fun onColorBlindnessSelected(option: String) {
        _selectedColorBlindness.update { option }
    }

    fun onLogOut() {
        // TODO: Déconnecter l'utilisateur et rediriger vers l'écran de connexion
    }

    fun onDeleteAccount() {
        // TODO: Supprimer le compte utilisateur
    }
}
