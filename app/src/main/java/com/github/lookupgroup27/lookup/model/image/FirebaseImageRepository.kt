package com.github.lookupgroup27.lookup.model.image

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.storage.FirebaseStorage
import java.io.File
import kotlinx.coroutines.tasks.await

/**
 * A repository implementation for handling image uploads to Firebase Storage.
 *
 * @property storage The [FirebaseStorage] instance used for uploading files.
 * @property auth The [FirebaseAuth] instance used to get the current authenticated user.
 */
class FirebaseImageRepository(
    private val storage: FirebaseStorage,
    private val auth: FirebaseAuth
) : ImageRepository {

  /**
   * Uploads an image file to Firebase Storage.
   *
   * The image is uploaded under the path: `/images/{userEmail}/{fileName}`. If the user is not
   * signed in, the function returns a failure result.
   *
   * @param imageFile The [File] object representing the image to be uploaded.
   * @return A [Result] containing the download URL of the uploaded image if successful, or an error
   *   message if the upload fails.
   * @throws Exception if the upload fails or the download URL cannot be retrieved.
   */
  override suspend fun uploadImage(imageFile: File): Result<String> {
    val storageRef = storage.reference
    val user = auth.currentUser
    val userMail = user?.email?.lowercase() ?: "unknown"

    // Check if the user is signed in; if not, return a failure result
    return if (user == null) {
      Result.failure(Exception("Failed to upload image: User not signed in."))
    } else {
      try {
        // Read the image file into a byte array
        val bytes = imageFile.readBytes()

        // Create a reference to the file in Firebase Storage
        val fileRef = storageRef.child("/images/$userMail/${imageFile.name}")

        // Upload the byte array to Firebase Storage
        fileRef.putBytes(bytes).await()

        // Retrieve the download URL of the uploaded image
        val downloadUrl = fileRef.downloadUrl.await()

        // Return the download URL as a success result
        Result.success(downloadUrl.toString())
      } catch (e: Exception) {
        // Log the error and return a failure result
        Log.e("FirebaseImageRepository", "Image upload failed", e)
        Result.failure(Exception("Failed to upload image: ${e.localizedMessage}"))
      }
    }
  }
}
