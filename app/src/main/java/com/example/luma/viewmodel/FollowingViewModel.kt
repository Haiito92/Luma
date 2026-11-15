package com.example.luma.viewmodel

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class FollowedPerson(
    val id: String,
    val name: String,
    val remainingMinutes: Int,
    val isSosActive: Boolean = false
)

class FollowingViewModel : ViewModel() {
    private val _followedPeople = MutableStateFlow(
        listOf(
            FollowedPerson(id = "1", name = "Jean-Baptiste Michelin", remainingMinutes = 5),
            FollowedPerson(id = "2", name = "Pierre Chabatte", remainingMinutes = 12),
            FollowedPerson(id = "3", name = "Jack Johnson", remainingMinutes = 20)
        )
    )
    val followedPeople: StateFlow<List<FollowedPerson>> = _followedPeople.asStateFlow()

    private val _selectedPerson = MutableStateFlow<FollowedPerson?>(_followedPeople.value.firstOrNull())
    val selectedPerson: StateFlow<FollowedPerson?> = _selectedPerson.asStateFlow()

    fun onPersonSelected(person: FollowedPerson) {
        _selectedPerson.update { person }
    }

    fun onClearSelectedPerson() {
        _selectedPerson.update { null }
    }

    fun updateRemainingTime(personId: String, remainingMinutes: Int) {
        _followedPeople.update { list ->
            list.map { person ->
                if (person.id == personId) person.copy(remainingMinutes = remainingMinutes) else person
            }
        }
        _selectedPerson.update { current ->
            if (current?.id == personId) current.copy(remainingMinutes = remainingMinutes) else current
        }
    }

    fun onSosReceived(personId: String) {
        _followedPeople.update { list ->
            list.map { person ->
                if (person.id == personId) person.copy(isSosActive = true) else person
            }
        }
        _selectedPerson.update { current ->
            if (current?.id == personId) current.copy(isSosActive = true) else current
        }
    }
}
