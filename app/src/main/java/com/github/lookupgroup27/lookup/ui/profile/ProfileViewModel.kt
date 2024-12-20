// File: ProfileViewModel.kt
package com.github.lookupgroup27.lookup.ui.profile

import androidx.lifecycle.*
import com.github.lookupgroup27.lookup.model.profile.ProfileRepository
import com.github.lookupgroup27.lookup.model.profile.ProfileRepositoryFirestore
import com.github.lookupgroup27.lookup.model.profile.UserProfile
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class ProfileViewModel(private val repository: ProfileRepository) : ViewModel() {

  private val _userProfile = MutableStateFlow<UserProfile?>(null)
  val userProfile: StateFlow<UserProfile?> = _userProfile.asStateFlow()

  private val _profileUpdateStatus = MutableStateFlow<Boolean?>(null)
  val profileUpdateStatus: StateFlow<Boolean?> = _profileUpdateStatus.asStateFlow()

  private val _error = MutableStateFlow<String?>(null)
  val error: StateFlow<String?> = _error.asStateFlow()

  // New state for username-specific errors
  private val _usernameError = MutableStateFlow<String?>(null)
  val usernameError: StateFlow<String?> = _usernameError.asStateFlow()

  init {
    repository.init { fetchUserProfile() }
  }

  fun fetchUserProfile() {
    viewModelScope.launch {
      repository.getUserProfile(
          onSuccess = { profile ->
            _userProfile.value = profile
            // Reset username error when fetching profile
            _usernameError.value = null
          },
          onFailure = { exception ->
            _error.value = "Failed to load profile: ${exception.message}"
          })
    }
  }

  /**
   * Updates the user profile after ensuring the username is unique.
   *
   * @param profile The new user profile with the updated username.
   */
  fun updateUserProfile(profile: UserProfile) {
    viewModelScope.launch {
      repository.isUsernameTaken(
          profile.username,
          onResult = { taken ->
            if (taken) {
              // Username is already taken
              _profileUpdateStatus.value = false
              _usernameError.value = "Username is already taken."
            } else {
              // Username is unique, proceed with update
              repository.updateUserProfile(
                  profile,
                  onSuccess = {
                    _profileUpdateStatus.value = true
                    _usernameError.value = null // Clear username error on successful update
                    _userProfile.value = profile // Update local userProfile state
                  },
                  onFailure = { exception ->
                    _profileUpdateStatus.value = false
                    _error.value = "Failed to update profile: ${exception.message}"
                  })
            }
          },
          onFailure = { exception ->
            _profileUpdateStatus.value = false
            _error.value = "Failed to check username: ${exception.message}"
          })
    }
  }

  /**
   * Deletes the user profile.
   *
   * @param profile The user profile to delete.
   */
  fun deleteUserProfile(profile: UserProfile) {
    viewModelScope.launch {
      repository.deleteUserProfile(
          profile,
          onSuccess = {
            _profileUpdateStatus.value = true
            _userProfile.value = null // Clear local userProfile state
          },
          onFailure = { exception ->
            _profileUpdateStatus.value = false
            _error.value = "Failed to delete profile: ${exception.message}"
          })
    }
  }

  /**
   * Resets the profile update status and error messages. This can be called after handling the
   * update result to clear previous states.
   */
  fun resetProfileUpdateStatus() {
    _profileUpdateStatus.value = null
    _error.value = null
    _usernameError.value = null
  }

  fun logoutUser() {
    repository.logoutUser()
    _userProfile.value = null
  }

  companion object {
    val Factory: ViewModelProvider.Factory =
        object : ViewModelProvider.Factory {
          @Suppress("UNCHECKED_CAST")
          override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return ProfileViewModel(ProfileRepositoryFirestore(Firebase.firestore, Firebase.auth))
                as T
          }
        }
  }
}
