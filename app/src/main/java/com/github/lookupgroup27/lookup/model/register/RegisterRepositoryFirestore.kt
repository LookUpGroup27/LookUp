package com.github.lookupgroup27.lookup.model.register

import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.tasks.await

class RegisterRepositoryFirestore(private val auth: FirebaseAuth = FirebaseAuth.getInstance()) :
    RegisterRepository {

  override suspend fun registerUser(email: String, password: String) {
    auth.createUserWithEmailAndPassword(email, password).await()
  }
}
