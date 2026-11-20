package com.example.luma.data.local

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class SettingsRepository {
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

    fun setShareLocation(enabled: Boolean) {
        _shareLocation.update { enabled }
    }

    fun setNightMode(enabled: Boolean) {
        _nightMode.update { enabled }
    }

    fun setColorBlindness(option: String) {
        _selectedColorBlindness.update { option }
    }
}
