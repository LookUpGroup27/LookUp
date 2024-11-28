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

/**
 * ViewModel responsible for managing the registration process and maintaining the UI state.
 * Encapsulates the logic for validating user input and interacting with the `RegisterRepository`.
 *
 * @property repository The repository handling user registration operations.
 */
class RegisterViewModel(private val repository: RegisterRepository) : ViewModel() {

  // Holds the UI state for the registration screen.
  private val _uiState = MutableStateFlow(RegisterState())
  val uiState: StateFlow<RegisterState> = _uiState

  /**
   * Factory for creating instances of `RegisterViewModel`. This is used to provide the required
   * dependencies (e.g., `RegisterRepository`).
   */
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
   * Updates the email in the UI state when the user modifies the email field.
   *
   * @param email The new email input provided by the user.
   */
  fun onEmailChanged(email: String) {
    _uiState.update { it.copy(email = email) }
  }

  /**
   * Updates the password in the UI state when the user modifies the password field.
   *
   * @param password The new password input provided by the user.
   */
  fun onPasswordChanged(password: String) {
    _uiState.update { it.copy(password = password) }
  }

  /**
   * Updates the confirm password in the UI state when the user modifies the confirmation field.
   *
   * @param confirmPassword The new confirmation password input provided by the user.
   */
  fun onConfirmPasswordChanged(confirmPassword: String) {
    _uiState.update { it.copy(confirmPassword = confirmPassword) }
  }

  /** Resets all input fields in the registration form by clearing the UI state. */
  fun clearFields() {
    _uiState.value = RegisterState()
  }

  /**
   * Validates the user's input and attempts to register the user if all inputs are valid. This
   * method performs both synchronous (local) validation and asynchronous registration logic.
   *
   * @param onSuccess Callback invoked when registration succeeds.
   * @param onError Callback invoked with an error message when registration fails.
   */
  fun registerUser(onSuccess: () -> Unit, onError: (String) -> Unit) {
    val email = _uiState.value.email
    val password = _uiState.value.password
    val confirmPassword = _uiState.value.confirmPassword

    // Validate that no fields are left empty.
    if (email.isBlank() || password.isBlank() || confirmPassword.isBlank()) {
      onError("Email, password, and confirmation cannot be empty")
      return
    }

    // Validate the email format.
    if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
      onError("Please enter a valid email address")
      return
    }

    // Validate the password strength.
    if (password.length < 8) {
      onError("Password must be at least 8 characters long")
      return
    }
    if (!password.any { it.isDigit() }) {
      onError("Password must include at least one number")
      return
    }
    if (!password.any { it.isUpperCase() }) {
      onError("Password must include at least one uppercase letter")
      return
    }

    // Validate that the password and confirmation match.
    if (password != confirmPassword) {
      onError("Passwords do not match")
      return
    }

    // Perform asynchronous registration using the repository.
    viewModelScope.launch {
      try {
        repository.registerUser(email, password)
        onSuccess() // Notify the UI that registration was successful.
      } catch (e: Exception) {
        onError(e.localizedMessage ?: "Registration failed") // Handle errors.
      }
    }
  }
}
