package com.github.lookupgroup27.lookup.model.passwordreset

import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.tasks.await

/**
 * Implementation of [PasswordResetRepository] that uses Firebase Authentication to handle password
 * reset operations.
 *
 * This class communicates with Firebase to send password reset emails.
 *
 * @property auth The [FirebaseAuth] instance used to interact with Firebase Authentication.
 */
class PasswordResetRepositoryFirestore(
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
) : PasswordResetRepository {

  /**
   * Sends a password reset email to the specified email address using Firebase Authentication.
   *
   * @param email The email address to send the password reset email to.
   * @return A [Result] wrapping the outcome of the operation:
   *     - [Result.success]: If the email was sent successfully.
   *     - [Result.failure]: If there was an error during the process.
   */
  override suspend fun sendPasswordResetEmail(email: String): Result<Unit> {
    return try {
      // Sends a password reset email asynchronously and waits for completion
      auth.sendPasswordResetEmail(email).await()
      Result.success(Unit) // Returns success if no exception is thrown
    } catch (e: Exception) {
      // Catches any exceptions and wraps them in a failure result
      Result.failure(e)
    }
  }
}
