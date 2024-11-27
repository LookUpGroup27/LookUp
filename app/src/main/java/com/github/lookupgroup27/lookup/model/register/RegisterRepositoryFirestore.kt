package com.github.lookupgroup27.lookup.model.register

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.tasks.await

class RegisterRepositoryFirestore(private val auth: FirebaseAuth = FirebaseAuth.getInstance()) :
    RegisterRepository {

  override suspend fun registerUser(email: String, password: String) {
    try {
      auth.createUserWithEmailAndPassword(email, password).await()
    } catch (e: Exception) {
      Log.e("RegisterRepository", "Error creating user", e)
      throw e
    }
  }
}
