package com.github.lookupgroup27.lookup.model.profile

interface ProfileRepository {
  fun init(onSuccess: () -> Unit)

  fun getUserProfile(onSuccess: (UserProfile?) -> Unit, onFailure: (Exception) -> Unit)

  fun updateUserProfile(profile: UserProfile, onSuccess: () -> Unit, onFailure: (Exception) -> Unit)

  fun logoutUser()
}