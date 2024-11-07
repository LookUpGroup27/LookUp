package com.github.lookupgroup27.lookup.model.image

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.io.File

class ImageViewModel(private val repository: ImageRepository) : ViewModel() {

  data class UploadStatus(
    val isLoading: Boolean = false,
    val downloadUrl: String? = null,
    val errorMessage: String? = null
  )

  private val _uploadStatus = MutableStateFlow(UploadStatus())
  val uploadStatus = _uploadStatus.asStateFlow()

  fun uploadImage(imageFile: File?) {
    _uploadStatus.value = UploadStatus(isLoading = true)
    viewModelScope.launch {
      val result = repository.uploadImage(imageFile!!)
      _uploadStatus.value = if (result.isSuccess) {
        UploadStatus(isLoading = false, downloadUrl = result.getOrNull())
      } else {
        UploadStatus(isLoading = false, errorMessage = result.exceptionOrNull()?.message ?: "Unknown error")
      }
    }
  }

  fun resetUploadStatus() {
    _uploadStatus.value = UploadStatus()
  }

  companion object {
    // Factory method to create a ViewModelProvider.Factory
    fun provideFactory(repository: ImageRepository): ViewModelProvider.Factory {
      return object : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
          if (modelClass.isAssignableFrom(ImageViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ImageViewModel(repository) as T
          }
          throw IllegalArgumentException("Unknown ViewModel class")
        }
      }
    }
  }
}
