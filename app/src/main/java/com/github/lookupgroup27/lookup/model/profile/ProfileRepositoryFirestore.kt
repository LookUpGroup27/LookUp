// File: ProfileRepositoryFirestore.kt
package com.github.lookupgroup27.lookup.model.profile

import android.util.Log
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

data class UserProfile(
    val username: String = " ",
    val email: String = " ",
    val bio: String = " ",
    val ratings: Map<String, Int> = emptyMap(),
    val selectedAvatar: Int? = null
)

class ProfileRepositoryFirestore(
    private val db: FirebaseFirestore,
    private val auth: FirebaseAuth
) : ProfileRepository {

  private val collectionPath = "users"
  private val usersCollection = db.collection(collectionPath)

  override fun init(onSuccess: () -> Unit) {
    auth.addAuthStateListener {
      if (it.currentUser != null) {
        onSuccess()
      }
    }
  }

  override fun getUserProfile(onSuccess: (UserProfile?) -> Unit, onFailure: (Exception) -> Unit) {
    val userId = auth.currentUser?.uid
    if (userId != null) {
      usersCollection.document(userId).get().addOnCompleteListener { task ->
        if (task.isSuccessful) {
          val profile = task.result?.toObject(UserProfile::class.java)
          onSuccess(profile)
        } else {
          task.exception?.let {
            Log.e("ProfileRepositoryFirestore", "Error getting user profile", it)
            onFailure(it)
          }
        }
      }
    } else {
      onSuccess(null) // No logged-in user
    }
  }

  override fun updateUserProfile(
      profile: UserProfile,
      onSuccess: () -> Unit,
      onFailure: (Exception) -> Unit
  ) {
    val userId = auth.currentUser?.uid
    if (userId != null) {
      performFirestoreOperation(usersCollection.document(userId).set(profile), onSuccess, onFailure)
    } else {
      onFailure(Exception("User not logged in"))
    }
  }

  override fun deleteUserProfile(
      profile: UserProfile,
      onSuccess: () -> Unit,
      onFailure: (Exception) -> Unit
  ) {
    val userId = auth.currentUser?.uid
    if (userId != null) {
      performFirestoreOperation(usersCollection.document(userId).delete(), onSuccess, onFailure)
    } else {
      onFailure(Exception("User not logged in"))
    }
  }

  override fun logoutUser() {
    auth.signOut()
  }

  override fun saveSelectedAvatar(
      userId: String,
      avatarId: Int?,
      onSuccess: () -> Unit,
      onFailure: (Exception) -> Unit
  ) {
    val userDocument = usersCollection.document(userId)
    userDocument
        .get()
        .addOnSuccessListener { document ->
          if (!document.exists()) {
            // Create a default profile with the selected avatar
            val defaultProfile = mapOf("selectedAvatar" to avatarId)
            userDocument
                .set(defaultProfile)
                .addOnSuccessListener { onSuccess() }
                .addOnFailureListener { exception -> onFailure(exception) }
          } else {
            // Update the existing profile with the selected avatar
            userDocument
                .update("selectedAvatar", avatarId)
                .addOnSuccessListener { onSuccess() }
                .addOnFailureListener { exception -> onFailure(exception) }
          }
        }
        .addOnFailureListener { exception -> onFailure(exception) }
  }

  override fun getSelectedAvatar(
      userId: String,
      onSuccess: (Int?) -> Unit,
      onFailure: (Exception) -> Unit
  ) {
    val userDocument = usersCollection.document(userId)
    userDocument
        .get()
        .addOnSuccessListener { document ->
          val avatarId = document.getLong("selectedAvatar")?.toInt()
          onSuccess(avatarId)
        }
        .addOnFailureListener { exception -> onFailure(exception) }
  }

  /**
   * Checks if the given username is already taken by another user.
   *
   * @param username The username to check for uniqueness.
   * @param onResult Callback with true if the username is taken, false otherwise.
   * @param onFailure Callback with an exception if the operation fails.
   */
  override fun isUsernameTaken(
      username: String,
      onResult: (Boolean) -> Unit,
      onFailure: (Exception) -> Unit
  ) {
    val userId = auth.currentUser?.uid
    if (userId != null) {
      usersCollection
          .whereEqualTo("username", username)
          .get()
          .addOnSuccessListener { querySnapshot ->
            // Check if any document has the username and is not the current user
            val taken = querySnapshot.documents.any { it.id != userId }
            onResult(taken)
          }
          .addOnFailureListener { exception ->
            Log.e("ProfileRepositoryFirestore", "Error checking username", exception)
            onFailure(exception)
          }
    } else {
      onFailure(Exception("User not logged in"))
    }
  }

  private fun performFirestoreOperation(
      task: Task<Void>,
      onSuccess: () -> Unit,
      onFailure: (Exception) -> Unit
  ) {
    task.addOnCompleteListener { result ->
      if (result.isSuccessful) {
        onSuccess()
      } else {
        result.exception?.let {
          Log.e("ProfileRepositoryFirestore", "Error performing Firestore operation", it)
          onFailure(it)
        }
      }
    }
  }
}
