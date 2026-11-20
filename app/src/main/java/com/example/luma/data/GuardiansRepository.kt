package com.example.luma.data

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

class GuardiansRepository {
    private val _guardians = MutableStateFlow(
        listOf("Jean-Baptiste Michelin", "Pierre Chabatte")
    )
    val guardians: StateFlow<List<String>> = _guardians.asStateFlow()

    private val _guardedPeople = MutableStateFlow(
        listOf(
            FollowedPerson(id = "1", name = "Jean-Baptiste Michelin", remainingMinutes = 5),
            FollowedPerson(id = "2", name = "Jean-Baptiste Michelin", remainingMinutes = 12),
            FollowedPerson(id = "3", name = "Jean-Baptiste Michelin", remainingMinutes = 20)
        )
    )
    val guardedPeople: StateFlow<List<FollowedPerson>> = _guardedPeople.asStateFlow()

    private val _selectedPerson = MutableStateFlow<FollowedPerson?>(_guardedPeople.value.firstOrNull())
    val selectedPerson: StateFlow<FollowedPerson?> = _selectedPerson.asStateFlow()

    fun addGuardian(guardianId: String) {
        val trimmed = guardianId.trim()
        if (trimmed.isNotEmpty() && !_guardians.value.contains(trimmed)) {
            _guardians.update { it + trimmed }
        }
    }

    fun removeGuardian(name: String) {
        _guardians.update { it - name }
    }

    fun selectPerson(person: FollowedPerson) {
        _selectedPerson.update { person }
    }

    fun clearSelectedPerson() {
        _selectedPerson.update { null }
    }

    fun updateRemainingTime(personId: String, remainingMinutes: Int) {
        _guardedPeople.update { list ->
            list.map { person ->
                if (person.id == personId) person.copy(remainingMinutes = remainingMinutes) else person
            }
        }
        _selectedPerson.update { current ->
            if (current?.id == personId) current.copy(remainingMinutes = remainingMinutes) else current
        }
    }

    fun onSosReceived(personId: String) {
        _guardedPeople.update { list ->
            list.map { person ->
                if (person.id == personId) person.copy(isSosActive = true) else person
            }
        }
        _selectedPerson.update { current ->
            if (current?.id == personId) current.copy(isSosActive = true) else current
        }
    }

    fun findFollowedPersonByName(name: String): FollowedPerson? {
        return _guardedPeople.value.firstOrNull { it.name == name }
    }
}
