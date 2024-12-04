package com.github.lookupgroup27.lookup.model.passwordreset

/**
 * Repository interface for handling password reset operations.
 *
 * This interface abstracts the implementation details of sending password reset emails, enabling
 * flexibility and easier testing.
 */
interface PasswordResetRepository {

  /**
   * Sends a password reset email to the specified email address.
   *
   * @param email The email address to send the reset instructions to.
   * @return A [Result] wrapping the outcome of the operation:
   *     - [Result.success]: If the email was sent successfully.
   *     - [Result.failure]: If there was an error during the process.
   */
  suspend fun sendPasswordResetEmail(email: String): Result<Unit>
}
