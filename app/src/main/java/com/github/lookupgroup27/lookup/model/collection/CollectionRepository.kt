package com.github.lookupgroup27.lookup.model.collection

import com.github.lookupgroup27.lookup.model.post.Post

interface CollectionRepository {
  fun init(onSuccess: () -> Unit)

  suspend fun getUserPosts(onSuccess: (List<Post>?) -> Unit, onFailure: (Exception) -> Unit)
}
