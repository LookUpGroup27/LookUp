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

  // TODO: Implement cropImage and resizeImage functions
  /**
   * fun cropImage(imageUrl: String) { viewModelScope.launch { _editImageState.value =
   * EditImageState.Loading try { val croppedImageBytes = performCropping(imageUrl) val result =
   * repository.uploadCroppedImage(croppedImageBytes) if (result.isSuccess) { _editImageState.value
   * = EditImageState.Success(result.getOrThrow()) } else { _editImageState.value =
   * EditImageState.Error(result.exceptionOrNull()?.message ?: "Error cropping image") } } catch (e:
   * Exception) { _editImageState.value = EditImageState.Error(e.message ?: "Error cropping image")
   * } } }
   *
   * fun resizeImage(imageUrl: String) { viewModelScope.launch { _editImageState.value =
   * EditImageState.Loading try { val resizedImageBytes = performResizing(imageUrl) val result =
   * repository.uploadResizedImage(resizedImageBytes) if (result.isSuccess) { _editImageState.value
   * = EditImageState.Success(result.getOrThrow()) } else { _editImageState.value =
   * EditImageState.Error(result.exceptionOrNull()?.message ?: "Error resizing image") } } catch (e:
   * Exception) { _editImageState.value = EditImageState.Error(e.message ?: "Error resizing image")
   * } } }
   *
   * private fun performCropping(imageUrl: String): ByteArray { // Placeholder for cropping logic
   * return ByteArray(0) }
   *
   * private fun performResizing(imageUrl: String): ByteArray { // Placeholder for resizing logic
   * return ByteArray(0) }
   */
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
