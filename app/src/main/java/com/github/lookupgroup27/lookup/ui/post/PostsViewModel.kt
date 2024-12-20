package com.github.lookupgroup27.lookup.ui.post

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.github.lookupgroup27.lookup.model.location.LocationProvider
import com.github.lookupgroup27.lookup.model.location.LocationProviderSingleton
import com.github.lookupgroup27.lookup.model.post.Post
import com.github.lookupgroup27.lookup.model.post.PostsRepository
import com.github.lookupgroup27.lookup.model.post.PostsRepositoryFirestore
import com.github.lookupgroup27.lookup.model.profile.UserProfile
import com.github.lookupgroup27.lookup.util.LocationUtils
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

private const val NUMBER_OF_STARS = 3

/**
 * ViewModel for managing posts and user interactions in the application.
 *
 * The `PostsViewModel` serves as the central hub for handling post-related data and logic, acting
 * as the intermediary between the user interface (UI) and the [PostsRepository]. It facilitates a
 * reactive flow of data to the UI using [StateFlow] and [mutableStateOf], ensuring that the
 * application remains responsive and up-to-date.
 *
 * @constructor Creates a new instance of the `PostsViewModel` with the given [PostsRepository].
 * @property repository The repository responsible for managing posts.
 */
class PostsViewModel(private val repository: PostsRepository) : ViewModel() {

  /** Holds the currently selected post. */
  val post = mutableStateOf<Post?>(null)

  @SuppressLint("StaticFieldLeak") private var context: Context? = null

  // LocationProvider instance to get user's current location
  private lateinit var locationProvider: LocationProvider

  /** Internal mutable state to hold all posts. */
  private val _allPosts = MutableStateFlow<List<Post>>(emptyList())
  /** Publicly exposed [StateFlow] to observe the list of all posts. */
  val allPosts: StateFlow<List<Post>> = _allPosts.asStateFlow()

  // MutableStateFlow to hold the list of nearby posts
  private val _nearbyPosts = MutableStateFlow<List<Post>>(emptyList())
  val nearbyPosts: StateFlow<List<Post>> = _nearbyPosts

  // Holds user ratings for posts: map of postUid to a list of booleans representing star states
  private val _postRatings = MutableStateFlow<Map<String, List<Boolean>>>(emptyMap())
  val postRatings: StateFlow<Map<String, List<Boolean>>> = _postRatings.asStateFlow()

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

  // User profile data, needed for rating initialization
  private var userProfile: UserProfile? = null
  private val userEmail: String?
    get() = auth?.currentUser?.email

  init {
    repository.init {
      auth?.addAuthStateListener(authListener)
      getPosts() // to ensure posts are loaded initially
    }
  }

  /**
   * Sets the application [Context] and initializes location monitoring and periodic post fetching.
   *
   * @param context The application [Context].
   */
  fun setContext(context: Context) {
    this.context = context
    locationProvider = LocationProviderSingleton.getInstance(context)
    startLocationMonitoring()
    startPeriodicPostFetching()
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
            // Initialize ratings if profile is already set
            if (userProfile != null) {
              initializeRatings(it, userProfile)
            }
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
   * Updates an existing post in the repository using concurrency-safe operations.
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
    val userLocation = locationProvider.currentLocation.value
    if (userLocation == null) {
      Log.e("ProximityPostFetcher", "User location is null; cannot fetch nearby posts.")
      return
    }

    viewModelScope.launch {
      allPosts.collect { posts ->
        if (posts.isNotEmpty()) {
          val sortedNearbyPosts =
              getSortedNearbyPosts(posts, userLocation.latitude, userLocation.longitude)
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
          post to distance
        }
        .sortedWith(
            compareBy<Pair<Post, Double>> { it.second } // Sort by distance (ascending)
                .thenByDescending { it.first.timestamp } // Then sort by timestamp (descending)
            )
        .take(10)
        .map { it.first }
  }

  /**
   * Starts monitoring location changes and triggers post updates accordingly. Uses viewModelScope
   * to ensure proper coroutine lifecycle management.
   */
  private fun startLocationMonitoring() {
    viewModelScope.launch {
      while (true) {
        val location = locationProvider.currentLocation.value
        if (location != null) {
          fetchSortedPosts()
        }
        delay(1000L)
      }
    }
  }

  /**
   * Starts periodic fetching of posts to ensure data stays fresh. Runs in viewModelScope to ensure
   * proper lifecycle management.
   */
  private fun startPeriodicPostFetching() {
    viewModelScope.launch {
      while (true) {
        fetchSortedPosts()
        delay(5000L)
      }
    }
  }

  /**
   * Call this when the user profile is available to initialize ratings. Ensure that posts are
   * already fetched before calling this.
   *
   * @param profile The [UserProfile] of the currently logged-in user.
   */
  fun setUserProfile(profile: UserProfile) {
    this.userProfile = profile
    initializeRatings(_allPosts.value, profile)
  }

  /**
   * Initializes post ratings for the current user once both posts and profile are available.
   *
   * @param posts The current list of posts.
   * @param profile The current user's profile with rating data.
   */
  private fun initializeRatings(posts: List<Post>, profile: UserProfile?) {
    if (profile == null || posts.isEmpty()) return

    val newRatings = mutableMapOf<String, List<Boolean>>()
    for (post in posts) {
      val savedRating = profile.ratings[post.uid] ?: 0
      val initialRating = List(NUMBER_OF_STARS) { index -> index < savedRating }
      newRatings[post.uid] = initialRating
    }
    _postRatings.value = newRatings
  }

  /**
   * Handles user rating updates. This function:
   * - Updates the local rating state.
   * - Updates the user profile ratings in memory (the calling code should persist profile changes).
   * - Executes a concurrency-safe transaction to update the post in the repository.
   *
   * @param postUid The unique ID of the post being rated.
   * @param newRating The new rating state as a list of booleans representing stars.
   */
  fun updateUserRatingForPost(postUid: String, newRating: List<Boolean>) {
    val oldMap = _postRatings.value.toMutableMap()
    val oldRating = oldMap[postUid] ?: List(NUMBER_OF_STARS) { false }
    val oldStarCount = oldRating.count { it }
    val starsCount = newRating.count { it }

    oldMap[postUid] = newRating
    _postRatings.value = oldMap

    val currentProfile = userProfile
    val currentPosts = _allPosts.value
    val postToUpdate = currentPosts.find { it.uid == postUid }

    if (postToUpdate != null && currentProfile != null && userEmail != null) {
      // Update user profile ratings in memory
      val updatedProfile =
          updateProfileRatings(
              currentProfile,
              postUid,
              starsCount,
              currentProfile.username,
              currentProfile.bio,
              currentProfile.email)
      userProfile = updatedProfile
      // Here you would typically call a profile repository or method to persist the updated profile

      // Calculate the new post state
      val updatedPost = calculatePostUpdates(postToUpdate, userEmail!!, starsCount, oldStarCount)
      updatePost(updatedPost)
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

/**
 * Updates the user's profile ratings.
 *
 * @param currentProfile The current user profile.
 * @param postUid The unique identifier of the post being rated.
 * @param starsCount The number of stars given to the post.
 * @param username The user's username.
 * @param bio The user's bio.
 * @param email The user's email.
 * @return An updated [UserProfile] with the new rating.
 */
fun updateProfileRatings(
    currentProfile: UserProfile,
    postUid: String,
    starsCount: Int,
    username: String,
    bio: String,
    email: String
): UserProfile {
  val updatedRatings = currentProfile.ratings.toMutableMap().apply { this[postUid] = starsCount }
  return currentProfile.copy(
      username = username, bio = bio, email = email, ratings = updatedRatings)
}

/**
 * Calculates the updated state of a post after a user rates it.
 *
 * @param post The original post.
 * @param userEmail The email of the user rating the post.
 * @param starsCount The number of stars the user has given.
 * @param oldStarCounts The previous number of stars the user had given.
 * @return An updated [Post] with recalculated ratings and user counts.
 */
fun calculatePostUpdates(post: Post, userEmail: String, starsCount: Int, oldStarCounts: Int): Post {
  val isReturningUser = post.ratedBy.contains(userEmail)
  val newStarsCount =
      if (isReturningUser) post.starsCount - oldStarCounts + starsCount
      else post.starsCount + starsCount
  val newRatedBy = if (!isReturningUser) post.ratedBy + userEmail else post.ratedBy
  val newUsersNumber = newRatedBy.size
  val newAvg = if (newUsersNumber != 0) newStarsCount.toDouble() / newUsersNumber else 0.0

  return post.copy(
      averageStars = newAvg,
      starsCount = newStarsCount,
      usersNumber = newUsersNumber,
      ratedBy = newRatedBy)
}
