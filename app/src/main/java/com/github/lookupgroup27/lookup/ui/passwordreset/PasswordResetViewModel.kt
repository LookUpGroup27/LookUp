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

class PasswordResetViewModel(private val repository: PasswordResetRepository) : ViewModel() {

  private val _uiState = MutableStateFlow(PasswordResetState())
  val uiState: StateFlow<PasswordResetState> = _uiState

  fun onEmailChanged(email: String) {
    _uiState.update { it.copy(email = email) }
  }

  fun clearFields() {
    _uiState.update { it.copy(email = "") }
  }

  fun resetUiState() {
    _uiState.value = PasswordResetState()
  }

  fun resetPassword() {
    val email = _uiState.value.email

    if (email.isBlank()) {
      _uiState.update { it.copy(errorMessage = "Email cannot be empty") }
      return
    }

    viewModelScope.launch {
      _uiState.update { it.copy(isLoading = true, errorMessage = null) }
      val result = repository.sendPasswordResetEmail(email)
      result
          .onSuccess { _uiState.update { it.copy(isSuccess = true, isLoading = false) } }
          .onFailure { e ->
            _uiState.update {
              it.copy(isLoading = false, errorMessage = e.localizedMessage ?: "Unknown error")
            }
          }
    }
  }

  companion object {
    val Factory: ViewModelProvider.Factory =
        object : ViewModelProvider.Factory {
          @Suppress("UNCHECKED_CAST")
          override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return PasswordResetViewModel(PasswordResetRepositoryFirestore()) as T
          }
        }
  }
}
