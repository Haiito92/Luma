package com.example.luma.ui.settings

import androidx.lifecycle.ViewModel
import com.example.luma.data.local.auth.AuthRepository
import com.example.luma.data.local.SettingsRepository
import kotlinx.coroutines.flow.StateFlow

class SettingsViewModel(
    private val settingsRepository: SettingsRepository,
    private val authRepository: AuthRepository
) : ViewModel() {
    val shareLocation: StateFlow<Boolean> = settingsRepository.shareLocation
    val nightMode: StateFlow<Boolean> = settingsRepository.nightMode
    val colorBlindnessOptions: List<String> = settingsRepository.colorBlindnessOptions
    val selectedColorBlindness: StateFlow<String> = settingsRepository.selectedColorBlindness

    fun onShareLocationChanged(enabled: Boolean) {
        settingsRepository.setShareLocation(enabled)
        // TODO: Activer/désactiver le partage de localisation en arrière-plan
    }

    fun onNightModeChanged(enabled: Boolean) {
        settingsRepository.setNightMode(enabled)
    }

    fun onColorBlindnessSelected(option: String) {
        settingsRepository.setColorBlindness(option)
    }

    fun onLogOut() {
        authRepository.logout()
    }

    fun onDeleteAccount() {
        authRepository.deleteCurrentAccount()
    }
}
