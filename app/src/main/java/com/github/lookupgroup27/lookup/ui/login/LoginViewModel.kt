package com.github.lookupgroup27.lookup.ui.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.github.lookupgroup27.lookup.model.login.LoginRepository
import com.github.lookupgroup27.lookup.model.login.LoginRepositoryFirestore
import com.github.lookupgroup27.lookup.model.login.LoginState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * ViewModel for managing the state and logic of the login screen.
 *
 * @param repository Repository for handling login operations.
 */
class LoginViewModel(private val repository: LoginRepository) : ViewModel() {

  // Holds the current state of the login screen.
  private val _uiState = MutableStateFlow(LoginState())
  val uiState: StateFlow<LoginState> = _uiState

  /**
   * Updates the email in the login state.
   *
   * @param email The updated email entered by the user.
   */
  fun onEmailChanged(email: String) {
    _uiState.update { it.copy(email = email) }
  }

  /**
   * Updates the password in the login state.
   *
   * @param password The updated password entered by the user.
   */
  fun onPasswordChanged(password: String) {
    _uiState.update { it.copy(password = password) }
  }

  /** Resets the login state to its default values (clears the email and password fields). */
  fun clearFields() {
    _uiState.value = LoginState()
  }

  /**
   * Attempts to log in the user with the provided credentials.
   *
   * @param onSuccess Callback executed on successful login.
   * @param onError Callback executed if login fails, with an error message.
   */
  fun loginUser(onSuccess: () -> Unit, onError: (String) -> Unit) {
    val email = _uiState.value.email
    val password = _uiState.value.password

    // Validate email format.
    if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
      onError("Please enter a valid email address")
      return
    }

    // Launch the login operation in a coroutine.
    viewModelScope.launch {
      try {
        repository.loginUser(email, password)
        onSuccess()
      } catch (e: Exception) {
        onError(e.localizedMessage ?: "Login failed")
      }
    }
  }

  companion object {
    /** Factory for creating instances of LoginViewModel. */
    val Factory: ViewModelProvider.Factory =
        object : ViewModelProvider.Factory {
          @Suppress("UNCHECKED_CAST")
          override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return LoginViewModel(LoginRepositoryFirestore()) as T
          }
        }
  }
}
