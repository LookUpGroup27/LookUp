package com.github.lookupgroup27.lookup.model.image

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.storage.FirebaseStorage
import java.io.File
import kotlinx.coroutines.tasks.await

class FirebaseImageRepository : ImageRepository {
  private val storage = FirebaseStorage.getInstance()
  private val storageRef = storage.reference
  private val user = FirebaseAuth.getInstance().currentUser
  private val userMail = user?.email ?: "unknown"

  override suspend fun uploadImage(imageFile: File): Result<String> {
    return if (user == null) {
      Result.failure(Exception("Please sign in to upload images."))
    } else {
      try {
        val bytes = imageFile.readBytes()
        val fileRef = storageRef.child("/images/${userMail}/${imageFile.name}")
        fileRef.putBytes(bytes).await()
        val downloadUrl = fileRef.downloadUrl.await()
        Result.success(downloadUrl.toString())
      } catch (e: Exception) {
        Result.failure(e)
      }
    }
  }
}
