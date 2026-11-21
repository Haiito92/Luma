package com.example.luma.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.luma.data.local.guardian.FollowedPerson
import com.example.luma.data.local.guardian.GuardianRequest
import com.example.luma.data.local.guardian.GuardiansRepository
import com.example.luma.data.local.guardian.PendingRequestsResult
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

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

    object ViewingPendingRequests : GuardiansUiState
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

    private val _pendingRequests = MutableStateFlow<List<GuardianRequest>>(emptyList())
    val pendingRequests: StateFlow<List<GuardianRequest>> = _pendingRequests.asStateFlow()

    private var addGuardianJob: Job? = null

    init {
        viewModelScope.launch {
            guardiansRepository.pendingRequestsUpdates().collect { result ->
                if (result is PendingRequestsResult.Success){
                    _pendingRequests.value = result.requests
                }
            }
        }
    }

    fun onTabSelected(tab: GuardiansTab) {
        _state.update { GuardiansUiState.ViewingContacts(selectedTab = tab) }
    }

    fun onOpenAddGuardian() {
        _state.update { GuardiansUiState.AddingGuardian }
    }

    fun onAddGuardian(guardianEmail: String) {

        addGuardianJob?.cancel()

        addGuardianJob = viewModelScope.launch {
            guardiansRepository.addGuardian(guardianEmail)
            _state.update { GuardiansUiState.ViewingContacts(selectedTab = GuardiansTab.Guardians) }
        }
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

    fun onOpenViewPendingRequests() {
        _state.update { GuardiansUiState.ViewingPendingRequests }
    }
}
