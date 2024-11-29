/**
 * ViewModel for managing the state and operations of the Edit Image feature.
 *
 * The `EditImageViewModel` interacts with the repository to perform actions on images such as
 * deleting them. It maintains the UI state using a `StateFlow` to reflect changes in the status of
 * these operations.
 *
 * @property repository The repository instance responsible for handling image-related operations.
 */
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

  /** Internal mutable state for tracking the status of edit image operations. */
  private val _editImageState = MutableStateFlow<EditImageState>(EditImageState.Idle)

  /** Exposed state to be observed by the UI for reflecting the current status. */
  val editImageState: StateFlow<EditImageState> = _editImageState

  /**
   * Deletes an image using the provided URL.
   *
   * Updates the state to `Loading` while the operation is in progress. If successful, the state
   * changes to `Deleted`. If it fails, the state transitions to `Error` with the relevant error
   * message.
   *
   * @param imageUrl The URL of the image to be deleted.
   */
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

  /**
   * Resets the state to `Idle`.
   *
   * This is typically used after the completion of an operation to prepare the state for a new
   * action.
   */
  fun resetState() {
    _editImageState.value = EditImageState.Idle
  }

  /**
   * Manually sets the state to a specific value.
   *
   * This method can be useful for testing or custom state management scenarios.
   *
   * @param state The new state to set.
   */
  fun setEditImageState(state: EditImageState) {
    (_editImageState as MutableStateFlow).value = state
  }

  /**
   * Factory for creating instances of `EditImageViewModel`.
   *
   * Uses the [FirebaseEditImageRepository] as the default implementation for interacting with
   * Firebase Storage.
   */
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

/** Represents the possible states of the Edit Image feature. */
sealed class EditImageState {
  /** Initial idle state where no operation is in progress. */
  object Idle : EditImageState()

  /** Indicates that an operation is currently in progress. */
  object Loading : EditImageState()

  /**
   * Indicates a successful operation with the updated image URL.
   *
   * @property imageUrl The URL of the updated image.
   */
  data class Success(val imageUrl: String) : EditImageState()

  /** Indicates that an image was successfully deleted. */
  object Deleted : EditImageState()

  /**
   * Indicates an error occurred during an operation.
   *
   * @property message A description of the error.
   */
  data class Error(val message: String) : EditImageState()
}
