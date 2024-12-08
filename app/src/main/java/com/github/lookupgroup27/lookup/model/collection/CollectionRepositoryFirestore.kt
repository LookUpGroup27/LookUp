package com.github.lookupgroup27.lookup.model.collection

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.tasks.await

/**
 * CollectionRepositoryFirestore is responsible for fetching image URLs associated with a user's
 * email from Firebase Storage. This implementation utilizes Firebase Authentication and Storage
 * services to retrieve user-specific data.
 *
 * @param db The FirebaseStorage instance used to access storage data.
 * @param auth The FirebaseAuth instance used for user authentication.
 */
class CollectionRepositoryFirestore(
    private val db: FirebaseStorage,
    private val auth: FirebaseAuth
) : CollectionRepository {

  /**
   * Initializes the repository by checking the authentication state of the user. If the user is
   * authenticated, the provided [onSuccess] callback is called.
   *
   * @param onSuccess A callback to execute when the user is authenticated.
   */
  override fun init(onSuccess: () -> Unit) {
    auth.addAuthStateListener {
      if (it.currentUser != null) {
        onSuccess()
      }
    }
  }

  /**
   * Retrieves a list of image URLs associated with the authenticated user's email from Firebase
   * Storage. If the user is not authenticated or if an error occurs, an empty list is returned.
   *
   * @return A list of image URLs associated with the user's email.
   */
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
      result.items.mapNotNull {
        if (!it.name.endsWith(".tmp")) {
          it.downloadUrl.await().toString()
        } else {
          null
        }
      }
    } catch (e: Exception) {
      Log.e("FirebaseCollectionRepo", "Error retrieving images from Firebase Storage", e)
      emptyList()
    }
  }
}
