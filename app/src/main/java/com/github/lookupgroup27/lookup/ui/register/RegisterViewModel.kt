package com.github.lookupgroup27.lookup.ui.register

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.github.lookupgroup27.lookup.model.register.RegisterRepository
import com.github.lookupgroup27.lookup.model.register.RegisterRepositoryFirestore
import com.github.lookupgroup27.lookup.model.register.RegisterState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

// ViewModel to manage the registration process and maintain UI state.
// Encapsulates the logic for user input validation and interaction with the RegisterRepository.
class RegisterViewModel(private val repository: RegisterRepository) : ViewModel() {

  private val _uiState = MutableStateFlow(RegisterState())
  val uiState: StateFlow<RegisterState> = _uiState

  // Factory for creating instances of RegisterViewModel.
  companion object {
    val Factory: ViewModelProvider.Factory =
        object : ViewModelProvider.Factory {
          @Suppress("UNCHECKED_CAST")
          override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return RegisterViewModel(RegisterRepositoryFirestore()) as T
          }
        }
  }

  /**
   * Updates the email in the state when the user changes the email field.
   *
   * @param email The updated email entered by the user.
   */
  fun onEmailChanged(email: String) {
    // Updates the state with the new email while keeping other fields unchanged.
    _uiState.update { it.copy(email = email) }
  }

  /**
   * Updates the password in the state when the user changes the password field.
   *
   * @param password The updated password entered by the user.
   */
  fun onPasswordChanged(password: String) {
    // Updates the state with the new password.
    _uiState.update { it.copy(password = password) }
  }

  /**
   * Updates the confirm password in the state when the user changes the confirmation field.
   *
   * @param confirmPassword The updated confirmation password entered by the user.
   */
  fun onConfirmPasswordChanged(confirmPassword: String) {
    // Updates the state with the new confirmation password.
    _uiState.update { it.copy(confirmPassword = confirmPassword) }
  }

  /** Clears all input fields in the registration form by resetting the state to its default. */
  fun clearFields() {
    _uiState.value = RegisterState() // Resets the state to its initial values.
  }

  /**
   * Validates the user input and attempts to register the user if valid.
   *
   * @param onSuccess Callback invoked when registration succeeds.
   * @param onError Callback invoked with an error message when registration fails.
   */
  fun registerUser(onSuccess: () -> Unit, onError: (String) -> Unit) {
    val email = _uiState.value.email
    val password = _uiState.value.password
    val confirmPassword = _uiState.value.confirmPassword

    // Input validation: Checks for empty fields.
    if (email.isBlank() || password.isBlank() || confirmPassword.isBlank()) {
      onError("Email, password, and confirmation cannot be empty")
      return
    }

    // Input validation: Checks if the password meets the minimum length requirement.
    if (password.length < 8) {
      onError("Password must be at least 8 characters")
      return
    }

    // Input validation: Ensures the password and confirmation match.
    if (password != confirmPassword) {
      onError("Passwords do not match")
      return
    }

    // If inputs are valid, attempt to register the user asynchronously.
    viewModelScope.launch {
      try {
        // Calls the repository to register the user.
        repository.registerUser(email, password)
        onSuccess() // Invokes the success callback when registration is complete.
      } catch (e: Exception) {
        // If registration fails, passes an error message to the UI.
        onError(e.localizedMessage ?: "Registration failed")
      }
    }
  }
}
