package com.github.lookupgroup27.lookup.model.image

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import java.io.File
import kotlinx.coroutines.tasks.await

class FirebaseImageRepository(
  private val storage: FirebaseStorage,
  private val auth: FirebaseAuth
) : ImageRepository {


  override suspend fun uploadImage(imageFile: File,): Result<String> {

    val storageRef = storage.reference
    val user = auth.currentUser
    val userMail = user?.email ?: "unknown"


    return if (user == null) {
      Result.failure(Exception("Please sign in to upload images."))
    } else {
      try {
        val bytes = imageFile.readBytes()
        val fileRef = storageRef.child("/images/${userMail}/${imageFile.name}")
        fileRef.putBytes(bytes)
        val downloadUrl = fileRef.downloadUrl.await()
        Result.success(downloadUrl.toString())
      } catch (e: Exception) {
        Result.failure(e)
      }
    }
  }
}
