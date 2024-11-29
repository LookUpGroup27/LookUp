package com.github.lookupgroup27.lookup.model.register

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.tasks.await

// An implementation of RegisterRepository that uses Firebase Authentication.
// Handles creating a new user in the Firebase backend.
class RegisterRepositoryFirestore(private val auth: FirebaseAuth = FirebaseAuth.getInstance()) :
    RegisterRepository {

  /**
   * Registers a user in Firebase with the provided email and password.
   *
   * @param email The email of the user to register.
   * @param password The password of the user to register.
   * @throws Exception If the registration process fails, the error is logged and rethrown.
   */
  override suspend fun registerUser(email: String, password: String) {
    try {
      // Firebase's method to create a user with email and password is called here.
      // The 'await()' ensures the asynchronous operation completes before proceeding.
      auth.createUserWithEmailAndPassword(email, password).await()
    } catch (e: Exception) {
      // Logs the error to assist with debugging in case of failure.
      Log.e("RegisterRepository", "Error creating user", e)
      // Rethrows the exception to allow higher-level error handling.
      throw e
    }
  }
}
