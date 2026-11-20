package com.example.luma.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.luma.data.FollowedPerson
import com.example.luma.data.GuardiansRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update

enum class GuardiansTab {
    Guardians,
    Guarded
}

sealed interface GuardiansUiState {
    data class ViewingContacts(
        val selectedTab: GuardiansTab = GuardiansTab.Guardians
    ) : GuardiansUiState

    object AddingGuardian : GuardiansUiState

    data class ViewingCourse(
        val person: FollowedPerson
    ) : GuardiansUiState
}

class GuardiansViewModel(
    private val guardiansRepository: GuardiansRepository
) : ViewModel() {

    private val _state = MutableStateFlow<GuardiansUiState>(GuardiansUiState.ViewingContacts())
    val state: StateFlow<GuardiansUiState> = _state.asStateFlow()

    val guardians: StateFlow<List<String>> = guardiansRepository.guardians

    val guarded: StateFlow<List<String>> = guardiansRepository.guardedPeople
        .map { list -> list.map { it.name }.distinct() }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Eagerly,
            initialValue = guardiansRepository.guardedPeople.value.map { it.name }.distinct()
        )

    fun onTabSelected(tab: GuardiansTab) {
        _state.update { GuardiansUiState.ViewingContacts(selectedTab = tab) }
    }

    fun onOpenAddGuardian() {
        _state.update { GuardiansUiState.AddingGuardian }
    }

    fun onAddGuardian(guardianId: String) {
        guardiansRepository.addGuardian(guardianId)
        _state.update { GuardiansUiState.ViewingContacts(selectedTab = GuardiansTab.Guardians) }
    }

    fun onRemoveGuardian(name: String) {
        guardiansRepository.removeGuardian(name)
    }

    fun onGuardedContactClicked(name: String) {
        val person = guardiansRepository.findFollowedPersonByName(name)
            ?: FollowedPerson(id = name, name = name, remainingMinutes = 5)
        guardiansRepository.selectPerson(person)
        _state.update { GuardiansUiState.ViewingCourse(person) }
    }

    fun onBackToContacts() {
        _state.update { GuardiansUiState.ViewingContacts(selectedTab = GuardiansTab.Guardians) }
    }
}
