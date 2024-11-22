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

  fun clearFields() {
    _uiState.value = _uiState.value.copy(email = "", password = "")
  }

  fun registerUser(onSuccess: () -> Unit, onError: (String) -> Unit) {
    val email = _uiState.value.email
    val password = _uiState.value.password

    if (email.isBlank() || password.isBlank()) {
      onError("Email and password cannot be empty")
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
