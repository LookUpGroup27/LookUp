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

  // private val auth = FirebaseAuth.getInstance()
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
      avatarId: Int,
      onSuccess: () -> Unit,
      onFailure: (Exception) -> Unit
  ) {
    val userDocument = usersCollection.document(userId)
    userDocument
        .get()
        .addOnSuccessListener { document ->
          if (document.exists()) {
            // Update the existing document
            userDocument
                .update("selectedAvatar", avatarId)
                .addOnSuccessListener { onSuccess() }
                .addOnFailureListener { exception -> onFailure(exception) }
          } else {
            // Create the document if it doesn't exist
            val newUserData = mapOf("selectedAvatar" to avatarId)
            userDocument
                .set(newUserData)
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
