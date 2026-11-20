package com.example.luma.data.local

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.GeoPoint
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class LumaUser(
    val documentId: String = "",
    val id: Long = 0L,
    val name: String = "",
    val lastName: String = "",
    val email: String = "",
)

class AuthRepository (val db: FirebaseFirestore){
    private val usersCollection by lazy {
        db.collection("user")
    }

    private val _currentUser = MutableStateFlow<LumaUser?>(null)
    val currentUser: StateFlow<LumaUser?> = _currentUser.asStateFlow()

    fun login(
        email: String,
        password: String,
        onResult: (Result<LumaUser>) -> Unit
    ) {
        val trimmedEmail = email.trim()
        usersCollection
            .whereEqualTo("email", trimmedEmail)
            .whereEqualTo("password", password)
            .get()
            .addOnSuccessListener { snapshot ->
                val document = snapshot.documents.firstOrNull()
                if (document != null) {
                    val user = LumaUser(
                        documentId = document.id,
                        id = document.getLong("id") ?: 0L,
                        name = document.getString("name") ?: "",
                        lastName = document.getString("last name") ?: "",
                        email = document.getString("email") ?: trimmedEmail,
                    )
                    _currentUser.value = user
                    onResult(Result.success(user))
                } else {
                    onResult(Result.failure(Exception("Email ou mot de passe incorrect")))
                }
            }
            .addOnFailureListener { exception ->
                onResult(Result.failure(exception))
            }
    }

    fun signUp(
        name: String,
        lastName: String,
        email: String,
        password: String,
        onResult: (Result<LumaUser>) -> Unit
    ) {
        val trimmedEmail = email.trim()
        val trimmedName = name.trim()
        val trimmedLastName = lastName.trim()

        usersCollection
            .whereEqualTo("email", trimmedEmail)
            .get()
            .addOnSuccessListener { existingSnapshot ->
                if (!existingSnapshot.isEmpty) {
                    onResult(Result.failure(Exception("Un compte existe déjà avec cet email")))
                    return@addOnSuccessListener
                }

                usersCollection
                    .get()
                    .addOnSuccessListener { allUsersSnapshot ->
                        val nextId = (allUsersSnapshot.documents
                            .mapNotNull { it.getLong("id") }
                            .maxOrNull() ?: -1L) + 1L
                        val initialPosition = GeoPoint(0.0, 0.0)

                        val userData = hashMapOf(
                            "email" to trimmedEmail,
                            "id" to nextId,
                            "last name" to trimmedLastName,
                            "name" to trimmedName,
                            "password" to password,
                            "position" to initialPosition
                        )

                        usersCollection
                            .add(userData)
                            .addOnSuccessListener { docRef ->
                                val user = LumaUser(
                                    documentId = docRef.id,
                                    id = nextId,
                                    name = trimmedName,
                                    lastName = trimmedLastName,
                                    email = trimmedEmail,
                                )
                                _currentUser.value = user
                                onResult(Result.success(user))
                            }
                            .addOnFailureListener { exception ->
                                onResult(Result.failure(exception))
                            }
                    }
                    .addOnFailureListener { exception ->
                        onResult(Result.failure(exception))
                    }
            }
            .addOnFailureListener { exception ->
                onResult(Result.failure(exception))
            }
    }

    fun logout() {
        _currentUser.value = null
    }

    fun deleteCurrentAccount(onComplete: () -> Unit) {
        val user = _currentUser.value
        if (user != null && user.documentId.isNotBlank()) {
            usersCollection
                .document(user.documentId)
                .delete()
                .addOnCompleteListener {
                    _currentUser.value = null
                    onComplete()
                }
        } else {
            _currentUser.value = null
            onComplete()
        }
    }
}
