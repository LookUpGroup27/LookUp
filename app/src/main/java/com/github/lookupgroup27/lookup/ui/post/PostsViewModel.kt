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

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.github.lookupgroup27.lookup.model.location.LocationProviderSingleton
import com.github.lookupgroup27.lookup.model.post.Post
import com.github.lookupgroup27.lookup.model.post.PostsRepository
import com.github.lookupgroup27.lookup.model.post.PostsRepositoryFirestore
import com.github.lookupgroup27.lookup.util.LocationUtils
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class PostsViewModel(private val repository: PostsRepository) : ViewModel() {

  /** Holds the currently selected post. */
  val post = mutableStateOf<Post?>(null)

    init {
        repository.init {
            auth?.addAuthStateListener(authListener)
            getPosts() // to ensure posts are loaded initially
        }
    }

  @SuppressLint("StaticFieldLeak") private var context: Context? = null

  // Method to initialize context
  fun setContext(context: Context) {
    this.context = context
  }

  // LocationProvider instance to get user's current location
  private val locationProvider = context?.let { LocationProviderSingleton.getInstance(it) }

  // MutableStateFlow to hold the list of nearby posts
  private val _nearbyPosts = MutableStateFlow<List<Post>>(emptyList())
  val nearbyPosts: StateFlow<List<Post>> = _nearbyPosts

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

  /**
   * Fetches and updates the list of nearby posts based on the user's current location.
   *
   * This method:
   * - Retrieves the user's current location from the [LocationProvider].
   * - Collects the latest list of posts from the [allPosts] flow.
   * - Uses the helper function [getSortedNearbyPosts] to calculate distances, sort posts by
   *   proximity (ascending), and by timestamp (descending).
   * - Updates the [_nearbyPosts] flow with the 10 closest posts.
   *
   * If the user's location is null, the method logs an error and returns without updating posts. If
   * the list of posts is empty, the method logs a warning and does not update the flow.
   *
   * **Coroutines**:
   * - Runs on a background thread using `Dispatchers.IO` to ensure non-blocking operation.
   *
   * @see getSortedNearbyPosts for the sorting and filtering logic.
   */
  fun fetchSortedPosts() {
    val userLocation = locationProvider?.currentLocation?.value
    if (userLocation == null) {
      Log.e("ProximityPostFetcher", "User location is null; cannot fetch nearby posts.")
      return
    }

    // Launch a coroutine to collect and process posts
    CoroutineScope(Dispatchers.IO).launch {
      allPosts.collect { posts ->
        if (posts.isNotEmpty()) {
          // Map each post to a pair of (post, distance) from the user
          val sortedNearbyPosts =
              getSortedNearbyPosts(posts, userLocation.latitude, userLocation.longitude)

          // Update _nearbyPosts with the sorted list of nearby posts
          _nearbyPosts.update { sortedNearbyPosts }
        } else {
          Log.e("fetchPosts", "Posts are empty.")
        }
      }
    }
  }

  /**
   * Sorts and filters a list of posts based on their distance from the user's location and the time
   * they were posted.
   *
   * This function:
   * - Calculates the distance from the user's current location to each post's location.
   * - Sorts posts by distance in ascending order (closest posts first).
   * - If two posts have the same distance, sorts them by timestamp in descending order (most recent
   *   posts first).
   * - Limits the result to the 10 closest posts.
   *
   * @param posts The list of [Post] objects to sort and filter.
   * @param userLatitude The latitude of the user's current location.
   * @param userLongitude The longitude of the user's current location.
   * @return A sorted list of [Post] objects containing the 10 closest posts.
   */
  internal fun getSortedNearbyPosts(
      posts: List<Post>,
      userLatitude: Double,
      userLongitude: Double
  ): List<Post> {
    return posts
        .mapNotNull { post ->
          val distance =
              LocationUtils.calculateDistance(
                  userLatitude, userLongitude, post.latitude, post.longitude)
          post to distance // Pair each post with its calculated distance
        }
        .sortedWith(
            compareBy<Pair<Post, Double>> { it.second } // Sort by distance (ascending)
                .thenByDescending { it.first.timestamp } // Then sort by timestamp (descending)
            )
        .take(10) // Take the 10 closest posts
        .map { it.first } // Extract only the posts
  }


    private fun startLocationMonitoring() {
        viewModelScope.launch {
            while (true) {
                val location = locationProvider?.currentLocation?.value
                if (location != null) {
                    fetchSortedPosts()
                }
                delay(1000L)
            }
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
