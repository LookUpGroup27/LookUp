package com.github.lookupgroup27.lookup.ui.register

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.github.lookupgroup27.lookup.model.register.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * ViewModel responsible for handling the registration logic and UI state.
 *
 * This ViewModel interacts with the [RegisterRepository] to perform registration operations. It
 * validates user inputs, updates the UI state accordingly, and handles success and error cases.
 *
 * @property repository The repository handling user registration operations.
 */
class RegisterViewModel(private val repository: RegisterRepository) : ViewModel() {

  // MutableStateFlow to hold and update the UI state.
  private val _uiState = MutableStateFlow(RegisterState())

  // Exposed immutable StateFlow for observing UI state changes.
  val uiState: StateFlow<RegisterState> = _uiState

  private val EMAIL_REGEX = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$".toRegex()

  /**
   * Companion object providing a custom [ViewModelProvider.Factory] for creating
   * [RegisterViewModel] instances.
   *
   * This factory allows the ViewModel to be created with the necessary repository dependency.
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
   * Updates the email in the UI state and clears any related error messages.
   *
   * @param email The new email input by the user.
   */
  fun onEmailChanged(email: String) {
    _uiState.update { it.copy(email = email, emailError = null, generalError = null) }
  }

  /**
   * Updates the password in the UI state and clears any related error messages.
   *
   * @param password The new password input by the user.
   */
  fun onPasswordChanged(password: String) {
    _uiState.update { it.copy(password = password, passwordError = null, generalError = null) }
  }

  /**
   * Updates the confirm password in the UI state and clears any related error messages.
   *
   * @param confirmPassword The new confirm password input by the user.
   */
  fun onConfirmPasswordChanged(confirmPassword: String) {
    _uiState.update {
      it.copy(confirmPassword = confirmPassword, confirmPasswordError = null, generalError = null)
    }
  }

  /** Resets the UI state to its initial values, clearing all input fields and error messages. */
  fun clearFields() {
    _uiState.value = RegisterState()
  }

  /**
   * Validates the email input.
   *
   * @param email The email to validate.
   * @return An error message if validation fails; null otherwise.
   */
  private fun validateEmail(email: String): String? {
    return when {
      email.isBlank() -> "Email cannot be empty."
      !EMAIL_REGEX.matches(email) -> "Invalid email address."
      else -> null
    }
  }

  /**
   * Validates the password input.
   *
   * The password must be at least 8 characters long, contain at least one digit, and one uppercase
   * letter.
   *
   * @param password The password to validate.
   * @return An error message if validation fails; null otherwise.
   */
  private fun validatePassword(password: String): String? {
    return when {
      password.isBlank() -> "Password cannot be empty."
      password.length < 8 -> "Password must be at least 8 characters."
      !password.any { it.isDigit() } -> "Password must include at least one number."
      !password.any { it.isUpperCase() } -> "Password must include at least one uppercase letter."
      else -> null
    }
  }

  /**
   * Validates that the confirm password matches the original password.
   *
   * @param password The original password.
   * @param confirmPassword The confirm password to validate.
   * @return An error message if validation fails; null otherwise.
   */
  private fun validateConfirmPassword(password: String, confirmPassword: String): String? {
    return if (password != confirmPassword) "Passwords do not match." else null
  }

  /**
   * Initiates the user registration process.
   *
   * This function performs input validation, updates the UI state with any errors, and proceeds to
   * register the user if validation passes. It handles success and error cases, updating the UI
   * state accordingly.
   *
   * @param onSuccess Callback invoked when registration is successful.
   */
  fun registerUser(onSuccess: () -> Unit) {
    val email = _uiState.value.email.trim()
    val password = _uiState.value.password
    val confirmPassword = _uiState.value.confirmPassword

    // Perform input validations and collect error messages.
    val emailError = validateEmail(email)
    val passwordError = validatePassword(password)
    val confirmPasswordError = validateConfirmPassword(password, confirmPassword)

    // Update the UI state with validation errors.
    _uiState.update {
      it.copy(
          emailError = emailError,
          passwordError = passwordError,
          confirmPasswordError = confirmPasswordError,
          generalError = null)
    }

    // If any validation errors exist, abort the registration process.
    if (emailError != null || passwordError != null || confirmPasswordError != null) {
      return
    }

    // Indicate that a registration operation is in progress.
    _uiState.update { it.copy(isLoading = true) }

    // Launch a coroutine to perform the registration asynchronously.
    viewModelScope.launch {
      try {
        // Attempt to register the user using the repository.
        repository.registerUser(email, password)

        // Registration successful; update the UI state and invoke the success callback.
        _uiState.update { it.copy(isLoading = false) }
        onSuccess()
      } catch (e: UserAlreadyExistsException) {
        // Specific error when the email is already in use.
        _uiState.update { it.copy(isLoading = false, generalError = e.message) }
      } catch (e: WeakPasswordException) {
        // Specific error when the password is too weak.
        _uiState.update { it.copy(isLoading = false, passwordError = e.message) }
      } catch (e: Exception) {
        // General error handling for any other exceptions.
        _uiState.update {
          it.copy(isLoading = false, generalError = e.message ?: "An unexpected error occurred.")
        }
      }
    }
  }
}
