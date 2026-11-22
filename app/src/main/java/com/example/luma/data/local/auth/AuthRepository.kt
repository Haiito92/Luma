package com.example.luma.data.local.auth

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.GeoPoint
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class AuthRepository (
    val db: FirebaseFirestore,
    val auth: FirebaseAuth
){
    private val usersCollection by lazy {
        db.collection("user")
    }

    private val _currentUser = MutableStateFlow<LumaUser?>(null)
    val currentUser: StateFlow<LumaUser?> = _currentUser.asStateFlow()

    private val _authState = MutableStateFlow<AuthState>(AuthState.Loading)
    val authState: StateFlow<AuthState> = _authState.asStateFlow()

    private val authStateListener = FirebaseAuth.AuthStateListener { auth ->
        val firebaseUser = auth.currentUser

        if (firebaseUser == null) {
            _currentUser.value = null
            _authState.value = AuthState.Unauthenticated
        }
        else{
            loadUserProfile(firebaseUser.uid)
        }
    }

    init {
        auth.addAuthStateListener(authStateListener)
    }

    private fun loadUserProfile(authID: String){
        _authState.value = AuthState.Loading

        usersCollection.document(authID)
            .get()
            .addOnSuccessListener{ document ->

                val user = document.toLumaUser(document.id)

                _currentUser.value = user
                _authState.value = AuthState.Authenticated(user)
            }
            .addOnFailureListener {
                _authState.value = AuthState.Unauthenticated
            }
    }

    fun login(
        email: String,
        password: String,
        onResult: (Result<LumaUser>) -> Unit
    ) {
        auth.signInWithEmailAndPassword(
            email.trim(),
            password
        ).addOnSuccessListener { result ->
            val firebaseUser = result.user

            if (firebaseUser == null){
                onResult(Result.failure(Exception("Authentication failed.")))
                return@addOnSuccessListener
            }

            usersCollection.document(firebaseUser.uid)
                .get()
                .addOnSuccessListener { document ->
                    if(!document.exists()){
                        onResult(Result.failure(Exception("User profile not found.")))
                        return@addOnSuccessListener

                    }

                    val user = document.toLumaUser(firebaseUser.uid)
                    _currentUser.value = user
                    _authState.value = AuthState.Authenticated(user)

                    onResult(Result.success(user))
                }
                .addOnFailureListener { exception ->
                    onResult(Result.failure(exception))
                }




        }.addOnFailureListener { exception ->
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
        auth.createUserWithEmailAndPassword(
            email.trim(),
            password
        ).addOnSuccessListener { result ->
            val firebaseUser = result.user

            if (firebaseUser == null){
                onResult(Result.failure(Exception("Account creation failed.")))
                return@addOnSuccessListener
            }

            val user = LumaUser(
                documentId = firebaseUser.uid,
                id = 0L,
                name = name.trim(),
                lastName = lastName.trim(),
                email = email.trim()
            )

            val profile = hashMapOf(
                "id" to user.id,
                "name" to user.name,
                "last name" to user.lastName,
                "email" to user.email
            )

            usersCollection.document(firebaseUser.uid)
                .set(profile)
                .addOnSuccessListener {
                    _currentUser.value = user
                    _authState.value = AuthState.Authenticated(user)
                    onResult(Result.success(user))
                }
                .addOnFailureListener { exception ->
                    onResult(Result.failure(exception))
                }

        }.addOnFailureListener { exception ->
            onResult(Result.failure(exception))
        }
    }

    fun logout() {
        auth.signOut()
    }

    fun deleteCurrentAccount() {

    }

    private fun com.google.firebase.firestore.DocumentSnapshot.toLumaUser(
        uid: String
    ): LumaUser {
        return LumaUser(
            documentId = uid,
            id = getLong("id") ?: 0L,
            name = getString("name") ?: "",
            lastName = getString("last name") ?: "",
            email = getString("email") ?: ""
        )
    }
}
