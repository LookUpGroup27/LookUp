package com.github.lookupgroup27.lookup.model.register

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import kotlinx.coroutines.tasks.await

/**
 * Implementation of the [RegisterRepository] interface using Firebase Authentication.
 *
 * This class handles the user registration process by interacting with FirebaseAuth to create new
 * users. It captures specific exceptions thrown by FirebaseAuth and translates them into custom
 * exceptions for better error handling in the ViewModel.
 *
 * @property auth The FirebaseAuth instance used to perform authentication operations.
 */
class RegisterRepositoryFirestore(private val auth: FirebaseAuth = FirebaseAuth.getInstance()) :
    RegisterRepository {

  /**
   * Registers a new user with the provided email and password.
   *
   * This function uses FirebaseAuth to create a new user account. It handles specific exceptions
   * thrown by FirebaseAuth and rethrows them as custom exceptions to be handled by the ViewModel.
   *
   * @param email The email address of the new user.
   * @param password The password for the new user.
   * @throws UserAlreadyExistsException If the email is already associated with an existing account.
   * @throws WeakPasswordException If the password does not meet Firebase's security requirements.
   * @throws Exception For any other errors during the registration process.
   */
  override suspend fun registerUser(email: String, password: String) {
    try {
      // Attempt to create a new user with the provided email and password.
      auth.createUserWithEmailAndPassword(email, password).await()
    } catch (e: FirebaseAuthUserCollisionException) {
      // Thrown if the email is already in use.
      throw UserAlreadyExistsException("An account with this email already exists.")
    } catch (e: FirebaseAuthWeakPasswordException) {
      // Thrown if the password is not strong enough.
      throw WeakPasswordException("Your password is too weak.")
    } catch (e: Exception) {
      // Log the error and rethrow a generic exception for any other errors.
      Log.e("RegisterRepository", "Error creating user", e)
      throw Exception("Registration failed due to an unexpected error.")
    }
  }
}
