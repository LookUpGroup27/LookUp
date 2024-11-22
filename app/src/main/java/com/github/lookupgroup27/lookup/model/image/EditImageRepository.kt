package com.github.lookupgroup27.lookup.model.image

interface EditImageRepository {
  suspend fun deleteImage(imageUrl: String): Result<Unit>

  suspend fun uploadCroppedImage(imageBytes: ByteArray): Result<String>

  suspend fun uploadResizedImage(imageBytes: ByteArray): Result<String>
}
