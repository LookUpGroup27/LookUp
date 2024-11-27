package com.github.lookupgroup27.lookup.model.image

interface EditImageRepository {
  suspend fun deleteImage(imageUrl: String): Result<Unit>
}
