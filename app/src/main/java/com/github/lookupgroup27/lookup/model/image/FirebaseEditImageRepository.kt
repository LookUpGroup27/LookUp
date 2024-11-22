package com.github.lookupgroup27.lookup.model.image

import com.google.firebase.storage.FirebaseStorage
import java.net.URLDecoder
import java.nio.charset.StandardCharsets
import kotlinx.coroutines.tasks.await

class FirebaseEditImageRepository(private val firebaseStorage: FirebaseStorage) :
    EditImageRepository {
  override suspend fun deleteImage(imageUrl: String): Result<Unit> {
    return try {
      val path = getPathFromUrl(imageUrl)
      firebaseStorage.reference.child(path).delete().await()
      Result.success(Unit)
    } catch (e: Exception) {
      Result.failure(e)
    }
  }

  override suspend fun uploadCroppedImage(imageBytes: ByteArray): Result<String> {
    return try {
      val reference = firebaseStorage.reference.child("path_to_store_image_cropped")
      reference.putBytes(imageBytes).await()
      val downloadUrl = reference.downloadUrl.await()
      Result.success(downloadUrl.toString())
    } catch (e: Exception) {
      Result.failure(e)
    }
  }

  override suspend fun uploadResizedImage(imageBytes: ByteArray): Result<String> {
    return try {
      val reference = firebaseStorage.reference.child("path_to_store_image_resized")
      reference.putBytes(imageBytes).await()
      val downloadUrl = reference.downloadUrl.await()
      Result.success(downloadUrl.toString())
    } catch (e: Exception) {
      Result.failure(e)
    }
  }

  private fun getPathFromUrl(url: String): String {
    val decodedUrl = URLDecoder.decode(url, StandardCharsets.UTF_8.toString())
    return decodedUrl.substringAfter("o/").substringBefore("?alt=media")
  }
}
