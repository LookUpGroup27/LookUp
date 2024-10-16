package com.github.lookupgroup27.lookup.model.profile

import android.util.Log
import com.google.android.gms.tasks.Task
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore

data class UserProfile(val username: String = " ", val email: String = " ", val bio: String = " ")

class ProfileRepositoryFirestore(private val db: FirebaseFirestore, private val auth: FirebaseAuth) {

  //private val auth = FirebaseAuth.getInstance()
  private val collectionPath = "users"
  private val usersCollection = db.collection(collectionPath)

  fun init(onSuccess: () -> Unit) {
    auth.addAuthStateListener {
      if (it.currentUser != null) {
        onSuccess()
      }
    }
  }

  fun getUserProfile(onSuccess: (UserProfile?) -> Unit, onFailure: (Exception) -> Unit) {
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

  fun updateUserProfile(
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

  fun logoutUser() {
    auth.signOut()
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
