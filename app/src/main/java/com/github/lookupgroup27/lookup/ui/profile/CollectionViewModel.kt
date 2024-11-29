package com.github.lookupgroup27.lookup.ui.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.github.lookupgroup27.lookup.model.collection.CollectionRepository
import com.github.lookupgroup27.lookup.model.collection.CollectionRepositoryFirestore
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel for managing a user's image collection data from a remote source. Handles image data
 * retrieval and error management, exposing states for UI observation.
 *
 * @param repository The repository that provides methods for accessing user-specific image data.
 */
class CollectionViewModel(private val repository: CollectionRepository) : ViewModel() {

  private val _imageUrls = MutableStateFlow<List<String>>(emptyList())
  val imageUrls: StateFlow<List<String>> = _imageUrls

  private val _error = MutableStateFlow<String?>(null)
  val error: StateFlow<String?> = _error

  init {
    repository.init { fetchImages() }
  }

  /**
   * Retrieves the list of image URLs associated with the user from the repository. Updates
   * [_imageUrls] with the fetched data on success, or sets an error message in [_error] on failure.
   */
  private fun fetchImages() {
    viewModelScope.launch {
      try {
        val images = repository.getUserImageUrls()
        _imageUrls.value = images
        _error.value = null // Clear any previous error state if successful
      } catch (e: Exception) {
        _error.value = "Failed to load images: ${e.localizedMessage}"
      }
    }
  }

  fun updateImages() {
    fetchImages()
  }

  /**
   * Factory object to create instances of [CollectionViewModel] with required dependencies. Uses
   * [CollectionRepositoryFirestore] to interact with Firebase for authenticated user image data.
   */
  companion object {
    val Factory: ViewModelProvider.Factory =
        object : ViewModelProvider.Factory {
          @Suppress("UNCHECKED_CAST")
          override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return CollectionViewModel(
                CollectionRepositoryFirestore(
                    FirebaseStorage.getInstance(), FirebaseAuth.getInstance()))
                as T
          }
        }
  }
}
