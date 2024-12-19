package com.github.lookupgroup27.lookup.ui.profile

import androidx.lifecycle.*
import com.github.lookupgroup27.lookup.model.profile.ProfileRepository
import com.github.lookupgroup27.lookup.model.profile.ProfileRepositoryFirestore
import com.github.lookupgroup27.lookup.model.profile.UserProfile
import com.github.lookupgroup27.lookup.model.register.UsernameAlreadyExistsException
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class ProfileViewModel(private val repository: ProfileRepository) : ViewModel() {

    private val _userProfile = MutableStateFlow<UserProfile?>(null)
    val userProfile: StateFlow<UserProfile?> = _userProfile.asStateFlow()

    private val _profileUpdateStatus = MutableStateFlow<Boolean?>(null) // Nullable to represent no action yet
    val profileUpdateStatus: StateFlow<Boolean?> = _profileUpdateStatus.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

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

    fun updateUserProfile(profile: UserProfile) {
        viewModelScope.launch {
            repository.updateUserProfile(
                profile,
                onSuccess = {
                    // After successful update, fetch the latest profile
                    repository.getUserProfile(
                        onSuccess = { updatedProfile ->
                            _userProfile.value = updatedProfile
                            _profileUpdateStatus.value = true // Trigger navigation
                        },
                        onFailure = { exception ->
                            _error.value = "Failed to refresh profile: ${exception.message}"
                            _profileUpdateStatus.value = false // Indicate failure
                        }
                    )
                    _usernameError.value = null // Clear username error on success
                },
                onFailure = { exception ->
                    when (exception) {
                        is UsernameAlreadyExistsException -> {
                            _usernameError.value = exception.message
                            _profileUpdateStatus.value = false // Indicate failure
                        }
                        else -> {
                            _error.value = "Failed to update profile: ${exception.message}"
                            _profileUpdateStatus.value = false // Indicate failure
                        }
                    }
                })
        }
    }

    fun resetProfileUpdateStatus() {
        _profileUpdateStatus.value = null
    }

    fun deleteUserProfile(profile: UserProfile) {
        viewModelScope.launch {
            repository.deleteUserProfile(
                profile,
                onSuccess = {
                    _profileUpdateStatus.value = true // Optionally set a status for deletion
                },
                onFailure = { exception ->
                    _error.value = "Failed to delete profile: ${exception.message}"
                })
        }
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