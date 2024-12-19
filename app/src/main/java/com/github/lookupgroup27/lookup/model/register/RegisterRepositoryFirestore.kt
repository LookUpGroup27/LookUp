package com.github.lookupgroup27.lookup.model.register

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

/**
 * Implementation of the [RegisterRepository] interface using Firebase Authentication and Firestore.
 *
 * This class handles the user registration process by:
 * 1. Checking username uniqueness against the Firestore 'users' collection.
 * 2. Creating the user with Firebase Authentication.
 * 3. Storing the user's email and username in the Firestore 'users' collection.
 *
 * By separating these responsibilities into a repository, we maintain a clean MVVM architecture.
 *
 * @property auth The [FirebaseAuth] instance used to perform authentication operations.
 * @property firestore The [FirebaseFirestore] instance used for username checks and data storage.
 */
class RegisterRepositoryFirestore(
    private val auth: FirebaseAuth = FirebaseAuth.getInstance(),
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
) : RegisterRepository {

  /**
   * Registers a new user with the provided email, password, and username.
   *
   * This function first checks whether the desired username is already taken. If not, it proceeds
   * to create the user with [FirebaseAuth]. Once the user is successfully created, it stores their
   * user data (email and username) in Firestore for future reference.
   *
   * @param email The email address of the new user.
   * @param password The password for the new user.
   * @param username The desired unique username for the new user.
   * @throws UsernameAlreadyExistsException If the chosen username is already taken.
   * @throws UserAlreadyExistsException If the email is already associated with an existing account.
   * @throws WeakPasswordException If the password does not meet security requirements.
   * @throws Exception If any other unexpected errors occur during registration.
   */
  override suspend fun registerUser(email: String, password: String, username: String) {
    try {
      // Check if username already exists in Firestore.
      val querySnapshot =
          firestore.collection("users").whereEqualTo("username", username).limit(1).get().await()

      if (!querySnapshot.isEmpty) {
        throw UsernameAlreadyExistsException("Username '$username' is already in use.")
      }

      // Attempt to create a new user in FirebaseAuth.
      val authResult = auth.createUserWithEmailAndPassword(email, password).await()
      val uid = authResult.user?.uid ?: throw Exception("Failed to retrieve user UID.")

      // Store user details in Firestore.
      val userData = mapOf("email" to email, "username" to username)
      firestore.collection("users").document(uid).set(userData).await()
    } catch (e: FirebaseAuthUserCollisionException) {
      throw UserAlreadyExistsException("An account with this email already exists.")
    } catch (e: FirebaseAuthWeakPasswordException) {
      throw WeakPasswordException("Your password is too weak.")
    } catch (e: UsernameAlreadyExistsException) {
      throw e
    } catch (e: Exception) {
      Log.e("RegisterRepository", "Error creating user", e)
      throw Exception("Registration failed due to an unexpected error.")
    }
  }
}
