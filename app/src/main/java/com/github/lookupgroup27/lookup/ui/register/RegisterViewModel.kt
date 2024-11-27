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

class RegisterViewModel(private val repository: RegisterRepository) : ViewModel() {

  private val _uiState = MutableStateFlow(RegisterState())
  val uiState: StateFlow<RegisterState> = _uiState

  companion object {
    val Factory: ViewModelProvider.Factory =
        object : ViewModelProvider.Factory {
          @Suppress("UNCHECKED_CAST")
          override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return RegisterViewModel(RegisterRepositoryFirestore()) as T
          }
        }
  }

  fun onEmailChanged(email: String) {
    _uiState.update { it.copy(email = email) }
  }

  fun onPasswordChanged(password: String) {
    _uiState.update { it.copy(password = password) }
  }

  fun onConfirmPasswordChanged(confirmPassword: String) {
    _uiState.update { it.copy(confirmPassword = confirmPassword) }
  }

  fun clearFields() {
    _uiState.value = RegisterState()
  }

  fun registerUser(onSuccess: () -> Unit, onError: (String) -> Unit) {
    val email = _uiState.value.email
    val password = _uiState.value.password
    val confirmPassword = _uiState.value.confirmPassword

    if (email.isBlank() || password.isBlank() || confirmPassword.isBlank()) {
      onError("Email, password, and confirmation cannot be empty")
      return
    }

    if (password.length < 8) {
      onError("Password must be at least 8 characters")
      return
    }

    if (password != confirmPassword) {
      onError("Passwords do not match")
      return
    }

    viewModelScope.launch {
      try {
        repository.registerUser(email, password)
        onSuccess()
      } catch (e: Exception) {
        onError(e.localizedMessage ?: "Registration failed")
      }
    }
  }
}
