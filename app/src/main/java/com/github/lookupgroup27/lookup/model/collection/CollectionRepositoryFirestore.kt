package com.github.lookupgroup27.lookup.model.collection

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.tasks.await

class CollectionRepositoryFirestore(
    private val db: FirebaseStorage,
    private val auth: FirebaseAuth
) : CollectionRepository {

  override fun init(onSuccess: () -> Unit) {
    auth.addAuthStateListener {
      if (it.currentUser != null) {
        onSuccess()
      }
    }
  }

  override suspend fun getUserImageUrls(): List<String> {
    val userEmail = auth.currentUser?.email ?: ""
    if (userEmail.isEmpty()) {
      Log.e("FirebaseCollectionRepo", "User email is empty.")
      return emptyList()
    }

    val folderPath = "images/$userEmail/"
    val imagesRef = db.reference.child(folderPath)

    return try {
      val result = imagesRef.listAll().await()
      result.items.mapNotNull { it.downloadUrl.await().toString() }
    } catch (e: Exception) {
      Log.e("FirebaseCollectionRepo", "Error retrieving images from Firebase Storage", e)
      emptyList()
    }
  }
}
