package com.github.lookupgroup27.lookup.ui.profile

import androidx.lifecycle.*
import com.github.lookupgroup27.lookup.model.profile.ProfileRepository
import com.github.lookupgroup27.lookup.model.profile.UserProfile
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class ProfileViewModel(private val repository: ProfileRepository) : ViewModel() {

  private val _userProfile = MutableStateFlow<UserProfile?>(null)
  val userProfile: StateFlow<UserProfile?> = _userProfile.asStateFlow()

  private val _profileUpdateStatus = MutableStateFlow<Boolean?>(null)
  val profileUpdateStatus: StateFlow<Boolean?> = _profileUpdateStatus.asStateFlow()

  private val _error = MutableStateFlow<String?>(null)
  val error: StateFlow<String?> = _error.asStateFlow()

  // ADDED: To indicate if username is taken
  private val _usernameError = MutableStateFlow<String?>(null)
  val usernameError: StateFlow<String?> = _usernameError.asStateFlow()

  init {
    repository.init { fetchUserProfile() }
  }

  fun fetchUserProfile() {
    viewModelScope.launch {
      repository.getUserProfile(
          onSuccess = { profile -> _userProfile.value = profile },
          onFailure = { exception ->
            _error.value = "Failed to load profile: ${exception.message}"
          })
    }
  }

  fun updateUserProfile(newProfile: UserProfile) {
    viewModelScope.launch {
      val currentUserId = Firebase.auth.currentUser?.uid
      val oldProfile = _userProfile.value

      val usernameChanged = oldProfile?.username != newProfile.username
      if (usernameChanged) {
        repository.isUsernameAvailable(
            username = newProfile.username,
            currentUserId = currentUserId,
            onSuccess = { available ->
              if (available) {
                performProfileUpdate(newProfile)
              } else {
                _usernameError.value = "Username is already taken. Please choose another."
              }
            },
            onFailure = { exception ->
              _error.value = "Failed to check username availability: ${exception.message}"
            })
      } else {
        performProfileUpdate(newProfile)
      }
    }
  }

  private fun performProfileUpdate(profile: UserProfile) {
    repository.updateUserProfile(
        profile,
        onSuccess = {
          _profileUpdateStatus.value = true
          _usernameError.value = null
          // ADDED: Update the local userProfile immediately so UI is in sync
          _userProfile.value = profile
        },
        onFailure = { exception ->
          _profileUpdateStatus.value = false
          _error.value = "Failed to update profile: ${exception.message}"
        })
  }

  fun deleteUserProfile(profile: UserProfile) {
    viewModelScope.launch {
      repository.deleteUserProfile(
          profile,
          onSuccess = { _profileUpdateStatus.value = true },
          onFailure = { exception ->
            _profileUpdateStatus.value = false
            _error.value = "Failed to delete profile: ${exception.message}"
          })
    }
  }

  fun logoutUser() {
    repository.logoutUser()
    _userProfile.value = null
  }

  fun clearUsernameError() {
    _usernameError.value = null
  }

  fun resetProfileUpdateStatus() {
    _profileUpdateStatus.value = null
  }

  companion object {
    val Factory: ViewModelProvider.Factory =
        object : ViewModelProvider.Factory {
          @Suppress("UNCHECKED_CAST")
          override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return ProfileViewModel(
                com.github.lookupgroup27.lookup.model.profile.ProfileRepositoryFirestore(
                    Firebase.firestore, Firebase.auth))
                as T
          }
        }
  }
}
