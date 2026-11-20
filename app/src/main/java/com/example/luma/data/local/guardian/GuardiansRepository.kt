package com.example.luma.data.local.guardian

import android.util.Log
import com.example.luma.data.local.AuthRepository
import com.google.firebase.FirebaseNetworkException
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.tasks.await

data class FollowedPerson(
    val id: String,
    val name: String,
    val remainingMinutes: Int,
    val isSosActive: Boolean = false
)

class GuardiansRepository(val db: FirebaseFirestore, val authRepository: AuthRepository) {

    private val users by lazy { db.collection("user") }
    private val links by lazy { db.collection("links") }

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

    suspend fun addGuardian(guardianEmail: String) : AddGuardianResult {
        val me = authRepository.currentUser.value ?: return AddGuardianResult.NotSignedIn
        val email = guardianEmail.trim().lowercase()
        if (email == me.email.trim().lowercase()) return AddGuardianResult.CannotAddSelf

        return try {
            val guardianDoc = users.whereEqualTo("email", email).limit(1).get().await()
                .documents.firstOrNull() ?: return AddGuardianResult.UserNotFound

            val linkRef = links.document("${me.documentId}_${guardianDoc.id}")
            if (linkRef.get().await().exists()) return AddGuardianResult.AlreadyAdded

            linkRef.set(
                mapOf(
                    "walkerId" to me.documentId,
                    "guardianId" to guardianDoc.id,
                    "status" to "pending",
                    "createdAt" to FieldValue.serverTimestamp(),
                    "guardianName" to guardianDoc.getString("name").orEmpty()
                )
            ).await()

            AddGuardianResult.Sent
        } catch (e: FirebaseNetworkException) {
            Log.e("Add Guardian", "No Connection" ,e)
            AddGuardianResult.NoConnection
        } catch (e: FirebaseFirestoreException) {
            Log.e("Add Guardian", "Failed" ,e)
            AddGuardianResult.Failed
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
