package com.github.lookupgroup27.lookup.model.profile

interface ProfileRepository {
  fun init(onSuccess: () -> Unit)

  fun getUserProfile(onSuccess: (UserProfile?) -> Unit, onFailure: (Exception) -> Unit)

  fun updateUserProfile(profile: UserProfile, onSuccess: () -> Unit, onFailure: (Exception) -> Unit)

  fun deleteUserProfile(profile: UserProfile, onSuccess: () -> Unit, onFailure: (Exception) -> Unit)

  fun logoutUser()

  fun saveSelectedAvatar(
      userId: String,
      avatarId: Int?,
      onSuccess: () -> Unit,
      onFailure: (Exception) -> Unit
  )

  fun getSelectedAvatar(userId: String, onSuccess: (Int?) -> Unit, onFailure: (Exception) -> Unit)

  // ADDED
  fun isUsernameAvailable(
      username: String,
      currentUserId: String?,
      onSuccess: (Boolean) -> Unit,
      onFailure: (Exception) -> Unit
  )
}
