package com.github.lookupgroup27.lookup.ui.passwordreset

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.github.lookupgroup27.lookup.model.passwordreset.PasswordResetRepository
import com.github.lookupgroup27.lookup.model.passwordreset.PasswordResetRepositoryFirestore
import com.github.lookupgroup27.lookup.model.passwordreset.PasswordResetState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * ViewModel responsible for managing the state and business logic for the Password Reset feature.
 *
 * This class communicates with the repository to handle password reset requests and maintains the
 * UI state to reflect user actions and responses from the backend.
 *
 * @param repository The [PasswordResetRepository] used to send password reset requests.
 */
class PasswordResetViewModel(private val repository: PasswordResetRepository) : ViewModel() {

  // Mutable state to hold the UI's current state
  private val _uiState = MutableStateFlow(PasswordResetState())
  val uiState: StateFlow<PasswordResetState> = _uiState

  /**
   * Updates the email in the UI state.
   *
   * @param email The email entered by the user.
   */
  fun onEmailChanged(email: String) {
    _uiState.update { it.copy(email = email) }
  }

  /** Clears all input fields in the UI state. */
  fun clearFields() {
    _uiState.update { it.copy(email = "") }
  }

  /** Resets the UI state to its initial default state. */
  fun resetUiState() {
    _uiState.value = PasswordResetState()
  }

  /**
   * Sends a password reset request for the entered email address.
   *
   * Validates the email before making the request and updates the UI state based on the outcome of
   * the repository call.
   */
  fun resetPassword() {
    val email = _uiState.value.email

    // Validate the email field
    if (email.isBlank()) {
      _uiState.update { it.copy(errorMessage = "Email cannot be empty") }
      return
    }

    // Launch a coroutine to perform the password reset operation
    viewModelScope.launch {
      // Set loading state
      _uiState.update { it.copy(isLoading = true, errorMessage = null) }

      // Call the repository to send a password reset email
      val result = repository.sendPasswordResetEmail(email)
      result
          .onSuccess {
            // Update the state to reflect a successful operation
            _uiState.update { it.copy(isSuccess = true, isLoading = false) }
          }
          .onFailure { e ->
            // Update the state to reflect an error during the operation
            _uiState.update {
              it.copy(isLoading = false, errorMessage = e.localizedMessage ?: "Unknown error")
            }
          }
    }
  }

  companion object {
    /**
     * Factory object to create an instance of [PasswordResetViewModel] with a default repository.
     *
     * This factory is used to inject the required dependencies into the ViewModel.
     */
    val Factory: ViewModelProvider.Factory =
        object : ViewModelProvider.Factory {
          @Suppress("UNCHECKED_CAST")
          override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return PasswordResetViewModel(PasswordResetRepositoryFirestore()) as T
          }
        }
  }
}
