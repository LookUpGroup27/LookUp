package com.github.lookupgroup27.lookup.model.collection

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class CollectionViewModel(private val repository: CollectionRepository) : ViewModel() {

  private val _imageUrls = MutableStateFlow<List<String>>(emptyList())
  val imageUrls: StateFlow<List<String>> = _imageUrls

  private val _error = MutableStateFlow<String?>(null)
  val error: StateFlow<String?> = _error

  init {
    repository.init { fetchImages() }
  }

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
