package com.github.lookupgroup27.lookup.model.feed

interface FeedRepository {
  fun init(onSuccess: () -> Unit)

  fun generateNewUid(): String

  fun getPosts(onSuccess: (List<Post>?) -> Unit, onFailure: (Exception) -> Unit)

  fun addPost(post: Post, onSuccess: () -> Unit, onFailure: (Exception) -> Unit)

  fun deletePost(post: Post, onSuccess: () -> Unit, onFailure: (Exception) -> Unit)

  fun updatePost(post: Post, onSuccess: () -> Unit, onFailure: (Exception) -> Unit)
}
