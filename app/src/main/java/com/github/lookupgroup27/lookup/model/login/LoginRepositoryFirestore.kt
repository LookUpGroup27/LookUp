package com.github.lookupgroup27.lookup.model.login

import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.tasks.await

/**
 * Firebase Firestore implementation of the LoginRepository interface. Handles user authentication
 * using Firebase Authentication.
 *
 * @param auth FirebaseAuth instance for interacting with Firebase Authentication.
 */
class LoginRepositoryFirestore(private val auth: FirebaseAuth = FirebaseAuth.getInstance()) :
    LoginRepository {

  /**
   * Logs in a user using Firebase Authentication.
   *
   * @param email The user's email address.
   * @param password The user's password.
   */
  override suspend fun loginUser(email: String, password: String) {
    auth.signInWithEmailAndPassword(email, password).await()
  }
}
