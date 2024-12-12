package com.github.lookupgroup27.lookup.ui.feed.components

import com.github.lookupgroup27.lookup.model.post.Post

fun calculatePostUpdates(post: Post, userEmail: String, starsCount: Int, oldStarCounts: Int): Post {
  val isReturningUser = post.ratedBy.contains(userEmail)
  val newStarsCount =
      if (isReturningUser) {
        post.starsCount - oldStarCounts + starsCount
      } else {
        post.starsCount + starsCount
      }
  val newUsersNumber = if (isReturningUser) post.usersNumber else post.usersNumber + 1
  val newAvg = newStarsCount.toDouble() / newUsersNumber

  return post.copy(
      averageStars = newAvg,
      starsCount = newStarsCount,
      usersNumber = newUsersNumber,
      ratedBy = if (!isReturningUser) post.ratedBy + userEmail else post.ratedBy)
}
