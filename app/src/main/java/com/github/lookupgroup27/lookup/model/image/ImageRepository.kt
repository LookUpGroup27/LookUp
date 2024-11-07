package com.github.lookupgroup27.lookup.model.image

import android.net.Uri
import java.io.File

interface ImageRepository {
  suspend fun uploadImage(imageUri: File): Result<String>
}
