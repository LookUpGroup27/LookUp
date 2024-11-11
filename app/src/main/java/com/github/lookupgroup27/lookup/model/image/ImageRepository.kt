package com.github.lookupgroup27.lookup.model.image

import java.io.File

interface ImageRepository {
  suspend fun uploadImage(imageUri: File): Result<String>
}
