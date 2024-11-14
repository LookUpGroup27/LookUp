package com.github.lookupgroup27.lookup.ui.post

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.github.lookupgroup27.lookup.model.post.Post
import com.github.lookupgroup27.lookup.model.post.PostsRepository
import com.github.lookupgroup27.lookup.model.post.PostsRepositoryFirestore
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class PostsViewModel(private val repository: PostsRepository) : ViewModel() {
  val post = mutableStateOf<Post?>(null)

  // LiveData to hold the list of all posts
  private val _allPosts = MutableStateFlow<List<Post>>(emptyList())
  val allPosts: StateFlow<List<Post>> = _allPosts.asStateFlow()

  private val auth: FirebaseAuth? =
      try {
        FirebaseAuth.getInstance()
      } catch (e: IllegalStateException) {
        null
      }

  private val authListener =
      FirebaseAuth.AuthStateListener { firebaseAuth ->
        val user: FirebaseUser? = firebaseAuth.currentUser
        if (user != null) {
          getPosts()
        } else {
          Log.d("FeedViewModel", "User is not logged in")
        }
      }

  fun selectPost(post: Post) {
    this.post.value = post
  }

  init {
    repository.init { auth?.addAuthStateListener(authListener) }
  }

  fun generateNewUid(): String {
    return repository.generateNewUid()
  }

  fun getPosts(onSuccess: () -> Unit = {}, onFailure: (Exception) -> Unit = {}) {
    repository.getPosts(
        onSuccess = {
          if (it != null) {
            _allPosts.value = it
          }
        },
        onFailure = {})
  }

  fun addPost(post: Post, onSuccess: () -> Unit = {}, onFailure: (Exception) -> Unit = {}) {
    repository.addPost(post, onSuccess, onFailure)
  }

  fun deletePost(post: Post, onSuccess: () -> Unit = {}, onFailure: (Exception) -> Unit = {}) {
    repository.deletePost(post, onSuccess, onFailure)
  }

  fun updatePost(post: Post, onSuccess: () -> Unit = {}, onFailure: (Exception) -> Unit = {}) {
    repository.updatePost(post, onSuccess, onFailure)
  }

  companion object {
    val Factory: ViewModelProvider.Factory =
        object : ViewModelProvider.Factory {
          @Suppress("UNCHECKED_CAST")
          override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return PostsViewModel(PostsRepositoryFirestore(Firebase.firestore)) as T
          }
        }
  }
}
