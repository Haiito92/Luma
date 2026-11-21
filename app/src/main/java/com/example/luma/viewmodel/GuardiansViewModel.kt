package com.example.luma.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.luma.data.local.guardian.FollowedPerson
import com.example.luma.data.local.guardian.Guarded
import com.example.luma.data.local.guardian.GuardedResult
import com.example.luma.data.local.guardian.Guardian
import com.example.luma.data.local.guardian.GuardianRequest
import com.example.luma.data.local.guardian.GuardiansRepository
import com.example.luma.data.local.guardian.GuardiansResult
import com.example.luma.data.local.guardian.PendingRequestsResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
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

    private val _guardians = MutableStateFlow<List<Guardian>>(emptyList())
    val guardians: StateFlow<List<Guardian>> = _guardians.asStateFlow()

    private val _guarded = MutableStateFlow<List<Guarded>>(emptyList())
    val guarded: StateFlow<List<Guarded>> = _guarded.asStateFlow()

    private val _pendingRequests = MutableStateFlow<List<GuardianRequest>>(emptyList())
    val pendingRequests: StateFlow<List<GuardianRequest>> = _pendingRequests.asStateFlow()

    init {
        viewModelScope.launch {
            guardiansRepository.guardiansUpdates().collect { result ->
                if (result is GuardiansResult.Success){
                    _guardians.value = result.guardians
                }
            }
        }

        viewModelScope.launch {
            guardiansRepository.guardedUpdates().collect { result ->
                if (result is GuardedResult.Success){
                    _guarded.value = result.guarded
                }
            }
        }

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
        viewModelScope.launch {
            guardiansRepository.addGuardianRequest(guardianEmail)
            _state.update { GuardiansUiState.ViewingContacts(selectedTab = GuardiansTab.Guardians) }
        }
    }

    fun onRemoveGuardian(linkId: String) {
        viewModelScope.launch {
            guardiansRepository.removeGuardianLink(linkId)
        }
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

    fun onAcceptGuardianRequest(linkId: String){
        viewModelScope.launch {
            guardiansRepository.acceptGuardianRequest(linkId)
        }
    }

    fun onDeclineGuardianRequest(linkId: String){
        viewModelScope.launch {
            guardiansRepository.declineGuardianRequest(linkId)
        }
    }
}
