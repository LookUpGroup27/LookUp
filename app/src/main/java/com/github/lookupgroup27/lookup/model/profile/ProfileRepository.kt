// File: ProfileRepository.kt
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

  /**
   * Checks if the given username is already taken by another user.
   *
   * @param username The username to check for uniqueness.
   * @param onResult Callback with true if the username is taken, false otherwise.
   * @param onFailure Callback with an exception if the operation fails.
   */
  fun isUsernameTaken(username: String, onResult: (Boolean) -> Unit, onFailure: (Exception) -> Unit)
}
