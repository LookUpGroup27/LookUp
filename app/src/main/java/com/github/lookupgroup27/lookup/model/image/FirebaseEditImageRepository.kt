/**
 * Implementation of the [EditImageRepository] interface that uses Firebase Storage for managing
 * images.
 *
 * This class provides the functionality to delete images stored in Firebase Storage by decoding
 * their URLs and interacting with the Firebase API.
 *
 * @property firebaseStorage An instance of [FirebaseStorage] used to interact with the Firebase
 *   Storage service.
 */
package com.github.lookupgroup27.lookup.model.image

import com.google.firebase.storage.FirebaseStorage
import java.net.URLDecoder
import java.nio.charset.StandardCharsets
import kotlinx.coroutines.tasks.await

class FirebaseEditImageRepository(private val firebaseStorage: FirebaseStorage) :
    EditImageRepository {

  /**
   * Deletes an image from Firebase Storage.
   *
   * This method takes the image URL, extracts its storage path, and attempts to delete it from
   * Firebase Storage.
   */
  override suspend fun deleteImage(imageUrl: String): Result<Unit> {
    return try {
      val path = getPathFromUrl(imageUrl)
      firebaseStorage.reference.child(path).delete().await()
      Result.success(Unit)
    } catch (e: Exception) {
      Result.failure(e)
    }
  }

  /**
   * Extracts the storage path from a Firebase Storage image URL.
   *
   * This method decodes the URL and extracts the path of the file stored in Firebase Storage by
   * identifying the substring after the `o/` segment and before the `?alt=media` segment.
   *
   * @param url The Firebase Storage image URL.
   * @return The storage path of the image in Firebase Storage.
   */
  private fun getPathFromUrl(url: String): String {
    val decodedUrl = URLDecoder.decode(url, StandardCharsets.UTF_8.toString())
    return decodedUrl.substringAfter("o/").substringBefore("?alt=media")
  }
}
