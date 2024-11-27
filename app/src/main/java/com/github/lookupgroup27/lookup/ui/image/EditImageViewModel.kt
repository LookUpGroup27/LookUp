package com.github.lookupgroup27.lookup.ui.image

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.github.lookupgroup27.lookup.model.image.EditImageRepository
import com.github.lookupgroup27.lookup.model.image.FirebaseEditImageRepository
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class EditImageViewModel(private val repository: EditImageRepository) : ViewModel() {

  private val _editImageState = MutableStateFlow<EditImageState>(EditImageState.Idle)
  val editImageState: StateFlow<EditImageState> = _editImageState

  fun deleteImage(imageUrl: String) {
    viewModelScope.launch {
      _editImageState.value = EditImageState.Loading
      val result = repository.deleteImage(imageUrl)
      if (result.isSuccess) {
        _editImageState.value = EditImageState.Deleted
      } else {
        _editImageState.value =
            EditImageState.Error(result.exceptionOrNull()?.message ?: "Error deleting image")
      }
    }
  }

  fun resetState() {
    _editImageState.value = EditImageState.Idle
  }

  fun setEditImageState(state: EditImageState) {
    (_editImageState as MutableStateFlow).value = state
  }

  companion object {
    val Factory: ViewModelProvider.Factory =
        object : ViewModelProvider.Factory {
          @Suppress("UNCHECKED_CAST")
          override fun <T : ViewModel> create(modelClass: Class<T>): T {
            val repository = FirebaseEditImageRepository(FirebaseStorage.getInstance())
            return EditImageViewModel(repository) as T
          }
        }
  }
}

sealed class EditImageState {
  object Idle : EditImageState()

  object Loading : EditImageState()

  data class Success(val imageUrl: String) : EditImageState()

  object Deleted : EditImageState()

  data class Error(val message: String) : EditImageState()
}
