package com.github.lookupgroup27.lookup.ui.profile.profilepic

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.github.lookupgroup27.lookup.model.profile.ProfileRepository
import com.github.lookupgroup27.lookup.model.profile.ProfileRepositoryFirestore
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel for the avatar selection screen.
 *
 * @param profileRepository the repository for profile data
 */

class AvatarViewModel(private val profileRepository: ProfileRepository) : ViewModel() {

  private val _selectedAvatar = MutableStateFlow<Int?>(null)
  val selectedAvatar: StateFlow<Int?> = _selectedAvatar.asStateFlow()

  private val _error = MutableStateFlow<String?>(null)
  val error: StateFlow<String?> = _error

  fun fetchSelectedAvatar(userId: String) {
    viewModelScope.launch {
      profileRepository.getSelectedAvatar(
          userId,
          onSuccess = { avatarId -> _selectedAvatar.value = avatarId },
          onFailure = { exception -> _error.value = exception.message })
    }
  }

  fun saveSelectedAvatar(userId: String, avatarId: Int) {
    viewModelScope.launch {
      profileRepository.saveSelectedAvatar(
          userId,
          avatarId,
          onSuccess = { _selectedAvatar.value = avatarId },
          onFailure = { exception -> _error.value = exception.message })
    }
  }

  companion object {
    val Factory: ViewModelProvider.Factory =
        object : ViewModelProvider.Factory {
          @Suppress("UNCHECKED_CAST")
          override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return AvatarViewModel(ProfileRepositoryFirestore(Firebase.firestore, Firebase.auth))
                as T
          }
        }
  }
}
