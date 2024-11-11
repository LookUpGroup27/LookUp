package com.github.lookupgroup27.lookup.model.image

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import java.io.File
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel for handling image upload functionality using the provided [ImageRepository].
 *
 * @param repository The repository responsible for handling image uploads.
 */
class ImageViewModel(private val repository: ImageRepository) : ViewModel() {

  /**
   * Data class representing the status of an image upload.
   *
   * @property isLoading Indicates whether the upload is currently in progress.
   * @property downloadUrl The URL of the uploaded image if successful.
   * @property errorMessage The error message if the upload fails.
   */
  data class UploadStatus(
      val isLoading: Boolean = false,
      val downloadUrl: String? = null,
      val errorMessage: String? = null
  )

  // Mutable state flow to hold the current upload status.
  private val _uploadStatus = MutableStateFlow(UploadStatus())

  /** Publicly exposed state flow representing the current status of the image upload. */
  val uploadStatus = _uploadStatus.asStateFlow()

  /**
   * Initiates the upload of the given image file.
   *
   * @param imageFile The [File] object representing the image to be uploaded.
   *
   * If the provided [imageFile] is null, the upload process is aborted, and an error message is set
   * in the upload status.
   */
  fun uploadImage(imageFile: File?) {
    if (imageFile == null) {
      _uploadStatus.value = UploadStatus(isLoading = false, errorMessage = "Image file is null")
      return
    }
    _uploadStatus.value = UploadStatus(isLoading = true)
    viewModelScope.launch {
      val result = repository.uploadImage(imageFile)
      _uploadStatus.value =
          if (result.isSuccess) {
            UploadStatus(isLoading = false, downloadUrl = result.getOrNull())
          } else {
            UploadStatus(
                isLoading = false,
                errorMessage = result.exceptionOrNull()?.message ?: "Unknown error")
          }
    }
  }

  /** Resets the upload status to its default state. */
  fun resetUploadStatus() {
    _uploadStatus.value = UploadStatus()
  }

  companion object {
    /**
     * Provides a factory method to create an instance of [ImageViewModel] using the given
     * [ImageRepository].
     *
     * @param repository The [ImageRepository] instance used for uploading images.
     * @return A [ViewModelProvider.Factory] that creates [ImageViewModel] instances.
     */
    fun provideFactory(repository: ImageRepository): ViewModelProvider.Factory {
      return object : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
          if (modelClass.isAssignableFrom(ImageViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST") return ImageViewModel(repository) as T
          }
          throw IllegalArgumentException("Unknown ViewModel class")
        }
      }
    }
  }
}
