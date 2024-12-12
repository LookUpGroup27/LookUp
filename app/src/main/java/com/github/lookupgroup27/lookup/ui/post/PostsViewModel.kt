/**
 * ViewModel for managing posts in the application.
 *
 * The `PostsViewModel` acts as the intermediary between the UI and the [PostsRepository]. It
 * handles the retrieval, addition, updating, and deletion of posts, along with managing
 * authentication states. The ViewModel ensures a reactive flow of data to the UI using [StateFlow]
 * and [mutableStateOf].
 *
 * @property repository The repository responsible for performing operations on posts.
 */
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

  /** Holds the currently selected post. */
  val post = mutableStateOf<Post?>(null)

  /** Internal mutable state to hold all posts. */
  private val _allPosts = MutableStateFlow<List<Post>>(emptyList())

  /** Publicly exposed [StateFlow] to observe the list of all posts. */
  val allPosts: StateFlow<List<Post>> = _allPosts.asStateFlow()

  private val auth: FirebaseAuth? =
      try {
        FirebaseAuth.getInstance()
      } catch (e: IllegalStateException) {
        null
      }

  /** Auth listener to fetch posts when the user is authenticated. */
  private val authListener =
      FirebaseAuth.AuthStateListener { firebaseAuth ->
        val user: FirebaseUser? = firebaseAuth.currentUser
        if (user != null) {
          getPosts()
        } else {
          Log.d("FeedViewModel", "User is not logged in")
        }
      }

  /**
   * Selects a post and updates the `post` state.
   *
   * @param post The [Post] object to select.
   */
  fun selectPost(post: Post) {
    this.post.value = post
  }

  init {
    repository.init { auth?.addAuthStateListener(authListener) }
  }

  /**
   * Generates a new unique identifier (UID) for a post.
   *
   * @return A string representing the newly generated UID.
   */
  fun generateNewUid(): String {
    return repository.generateNewUid()
  }

  /**
   * Retrieves all posts from the repository.
   *
   * Updates the `_allPosts` state upon successful retrieval.
   *
   * @param onSuccess Callback executed on successful retrieval.
   * @param onFailure Callback executed on retrieval failure.
   */
  fun getPosts(onSuccess: () -> Unit = {}, onFailure: (Exception) -> Unit = {}) {
    repository.getPosts(
        onSuccess = {
          if (it != null) {
            _allPosts.value = it
          }
        },
        onFailure = {})
  }

  /**
   * Adds a new post to the repository.
   *
   * @param post The [Post] object to add.
   * @param onSuccess Callback executed on successful addition.
   * @param onFailure Callback executed on addition failure.
   */
  fun addPost(post: Post, onSuccess: () -> Unit = {}, onFailure: (Exception) -> Unit = {}) {
    repository.addPost(post, onSuccess, onFailure)
  }

  /**
   * Deletes a post from the repository.
   *
   * Identifies the post to delete by its `uid`.
   *
   * @param postUid The UID of the post to delete.
   * @param onSuccess Callback executed on successful deletion.
   * @param onFailure Callback executed on deletion failure.
   */
  fun deletePost(postUid: String, onSuccess: () -> Unit = {}, onFailure: (Exception) -> Unit = {}) {

    repository.deletePost(postUid, onSuccess, onFailure)
  }

  /**
   * Updates an existing post in the repository.
   *
   * @param post The [Post] object containing updated data.
   * @param onSuccess Callback executed on successful update.
   * @param onFailure Callback executed on update failure.
   */
  fun updatePost(post: Post, onSuccess: () -> Unit = {}, onFailure: (Exception) -> Unit = {}) {
    repository.updatePost(post, onSuccess, onFailure)
  }

  /**
   * Updates the description of an existing post in the repository.
   *
   * @param postUid The UID of the modified post.
   * @param onSuccess Callback executed on successful update.
   * @param onFailure Callback executed on update failure.
   */
  fun updateDescription(
      postUid: String,
      newDescription: String,
      onSuccess: () -> Unit = {},
      onFailure: (Exception) -> Unit = {}
  ) {
    try {
      repository.updateDescription(postUid, newDescription, onSuccess, onFailure)
    } catch (exception: Exception) {
      onFailure(exception)
    }
  }

  companion object {
    /**
     * Factory for creating instances of [PostsViewModel].
     *
     * Uses [PostsRepositoryFirestore] as the default repository implementation.
     */
    val Factory: ViewModelProvider.Factory =
        object : ViewModelProvider.Factory {
          @Suppress("UNCHECKED_CAST")
          override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return PostsViewModel(PostsRepositoryFirestore(Firebase.firestore)) as T
          }
        }
  }
}
