package com.github.lookupgroup27.lookup.data.repository

import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.tasks.await

class RegisterRepository {

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    suspend fun registerUser(email: String, password: String) {
        auth.createUserWithEmailAndPassword(email, password).await()
    }
}
