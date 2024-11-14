package com.github.lookupgroup27.lookup.model.collection

interface CollectionRepository {
  fun init(onSuccess: () -> Unit)

  suspend fun getUserImageUrls(): List<String>
}
