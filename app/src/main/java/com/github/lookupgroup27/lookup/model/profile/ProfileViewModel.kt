package com.github.lookupgroup27.lookup.model.profile

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class ProfileViewModel(private val repository: ProfileRepositoryFirestore) : ViewModel() {

  private val _userProfile = MutableLiveData<UserProfile?>()
  val userProfile: LiveData<UserProfile?> = _userProfile

  private val _profileUpdateStatus = MutableLiveData<Boolean>()
  val profileUpdateStatus: LiveData<Boolean> = _profileUpdateStatus

  private val _error = MutableLiveData<String?>()
  val error: LiveData<String?> = _error

  init {
    fetchUserProfile()
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

  fun updateUserProfile(profile: UserProfile) {
    viewModelScope.launch {
      repository.updateUserProfile(
          profile,
          onSuccess = { _profileUpdateStatus.value = true },
          onFailure = { exception ->
            _profileUpdateStatus.value = false
            _error.value = "Failed to update profile: ${exception.message}"
          })
    }
  }

  fun logoutUser() {
    repository.logoutUser()
    _userProfile.value = null
  }
}
