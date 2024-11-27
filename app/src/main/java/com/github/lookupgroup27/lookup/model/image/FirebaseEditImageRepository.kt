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

  private fun getPathFromUrl(url: String): String {
    val decodedUrl = URLDecoder.decode(url, StandardCharsets.UTF_8.toString())
    return decodedUrl.substringAfter("o/").substringBefore("?alt=media")
  }
}
