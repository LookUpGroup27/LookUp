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

  init {
    // Automatically fetch images when the ViewModel is created
    viewModelScope.launch { fetchImages() }
  }

  private suspend fun fetchImages() {
    val images = repository.getUserImageUrls()
    _imageUrls.value = images
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
