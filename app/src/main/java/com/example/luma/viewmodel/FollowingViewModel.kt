package com.example.luma.viewmodel

import androidx.lifecycle.ViewModel
import com.example.luma.data.GuardiansRepository
import kotlinx.coroutines.flow.StateFlow

typealias FollowedPerson = com.example.luma.data.FollowedPerson

class FollowingViewModel(
    private val guardiansRepository: GuardiansRepository = GuardiansRepository()
) : ViewModel() {
    val followedPeople: StateFlow<List<FollowedPerson>> = guardiansRepository.guardedPeople

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
