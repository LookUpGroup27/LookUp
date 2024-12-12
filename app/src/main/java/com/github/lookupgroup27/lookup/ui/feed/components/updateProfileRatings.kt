package com.github.lookupgroup27.lookup.ui.feed.components

import com.github.lookupgroup27.lookup.model.profile.UserProfile

fun updateProfileRatings(
    currentProfile: UserProfile?,
    postUid: String,
    starsCount: Int,
    username: String,
    bio: String,
    email: String
): UserProfile {
  val updatedRatings =
      currentProfile?.ratings?.toMutableMap()?.apply { this[postUid] = starsCount }
          ?: mutableMapOf(postUid to starsCount)

  return currentProfile?.copy(
      username = username, bio = bio, email = email, ratings = updatedRatings)
      ?: UserProfile(username = username, bio = bio, email = email, ratings = updatedRatings)
}
