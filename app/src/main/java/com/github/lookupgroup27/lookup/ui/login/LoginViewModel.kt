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

class LoginViewModel(private val repository: LoginRepository) : ViewModel() {

  private val _uiState = MutableStateFlow(LoginState())
  val uiState: StateFlow<LoginState> = _uiState

  fun onEmailChanged(email: String) {
    _uiState.update { it.copy(email = email) }
  }

  fun onPasswordChanged(password: String) {
    _uiState.update { it.copy(password = password) }
  }

  fun clearFields() {
    _uiState.value = _uiState.value.copy(email = "", password = "")
  }

  fun loginUser(onSuccess: () -> Unit, onError: (String) -> Unit) {
    val email = _uiState.value.email
    val password = _uiState.value.password

    if (email.isBlank() || password.isBlank()) {
      onError("Email and password cannot be empty")
      return
    }

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
    val Factory: ViewModelProvider.Factory =
        object : ViewModelProvider.Factory {
          @Suppress("UNCHECKED_CAST")
          override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return LoginViewModel(LoginRepositoryFirestore()) as T
          }
        }
  }
}
