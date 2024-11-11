package com.github.lookupgroup27.lookup.model.post

data class Post(
    val uid: String = "",
    val uri: String = "",
    val username: String = "",
    val likes: Int = 0,
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,
)