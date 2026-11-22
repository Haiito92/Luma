package com.example.luma.ui.guardians

import androidx.lifecycle.ViewModel
import com.example.luma.data.local.guardian.GuardiansRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

typealias FollowedPerson = com.example.luma.data.local.guardian.FollowedPerson

class FollowingViewModel(
    private val guardiansRepository: GuardiansRepository
) : ViewModel() {

    private val _followedPeople = MutableStateFlow<List<FollowedPerson>>(emptyList())
    val followedPeople: StateFlow<List<FollowedPerson>> = _followedPeople.asStateFlow()

    val selectedPerson: StateFlow<FollowedPerson?> = guardiansRepository.selectedPerson

    fun onPersonSelected(person: FollowedPerson) {
        guardiansRepository.selectPerson(person)
    }

    fun onClearSelectedPerson() {
        guardiansRepository.clearSelectedPerson()
    }

    fun updateRemainingTime(personId: String, remainingMinutes: Int) {
        guardiansRepository.updateRemainingTime(personId, remainingMinutes)
    }

    fun onSosReceived(personId: String) {
        guardiansRepository.onSosReceived(personId)
    }
}
