package com.github.lookupgroup27.lookup.ui.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.github.lookupgroup27.lookup.model.collection.CollectionRepository
import com.github.lookupgroup27.lookup.model.collection.CollectionRepositoryFirestore
import com.github.lookupgroup27.lookup.model.post.Post
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.firestore
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

  private val _myPosts = MutableStateFlow<List<Post>>(emptyList())
  val myPosts: StateFlow<List<Post>> = _myPosts

  private val _error = MutableStateFlow<String?>(null)
  val error: StateFlow<String?> = _error

  init {
    repository.init { fetchPosts() }
  }

  /**
   * Retrieves the list of image URLs associated with the user from the repository. Updates
   * [_imageUrls] with the fetched data on success, or sets an error message in [_error] on failure.
   */
  private fun fetchPosts() {
    viewModelScope.launch {
      try {
        repository.getUserPosts(
            onSuccess = {
              if (it != null) {
                _myPosts.value = it
              }
            },
            onFailure = { _error.value = "Failed to load images: ${it.localizedMessage}" })
        _error.value = null // Clear any previous error state if successful
      } catch (e: Exception) {
        _error.value = "Failed to load images: ${e.localizedMessage}"
      }
    }
  }

  fun updateImages() {
    fetchPosts()
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
                CollectionRepositoryFirestore(Firebase.firestore, FirebaseAuth.getInstance()))
                as T
          }
        }
  }
}
