package com.example.luma.viewmodel

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

enum class GuardiansTab {
    Guardians,
    Guarded
}

class GuardiansViewModel : ViewModel() {
    private val _selectedTab = MutableStateFlow(GuardiansTab.Guardians)
    val selectedTab: StateFlow<GuardiansTab> = _selectedTab.asStateFlow()

    private val _guardians = MutableStateFlow(
        listOf("Jean-Baptiste Michelin", "Pierre Chabatte")
    )
    val guardians: StateFlow<List<String>> = _guardians.asStateFlow()

    private val _guarded = MutableStateFlow(
        listOf("Jean-Baptiste Michelin")
    )
    val guarded: StateFlow<List<String>> = _guarded.asStateFlow()

    fun onTabSelected(tab: GuardiansTab) {
        _selectedTab.update { tab }
    }

    fun onAddGuardian(guardianId: String) {
        val trimmed = guardianId.trim()
        if (trimmed.isNotEmpty() && !_guardians.value.contains(trimmed)) {
            _guardians.update { it + trimmed }
        }
    }

    fun onRemoveGuardian(name: String) {
        _guardians.update { it - name }
    }

    fun onContactSelected(name: String) {
        // TODO: Ouvrir le détail du gardien ou le suivi du trajet (FollowingCourse)
    }
}
