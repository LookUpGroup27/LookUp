package com.github.lookupgroup27.lookup.model.feed

import android.content.Context
import android.util.Log
import com.github.lookupgroup27.lookup.model.location.LocationProviderSingleton
import com.github.lookupgroup27.lookup.model.post.Post
import com.github.lookupgroup27.lookup.ui.post.PostsViewModel
import com.github.lookupgroup27.lookup.util.LocationUtils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * ProximityPostFetcher is responsible for fetching posts and filtering them based on their
 * proximity to the user's current location and also on the time the posts were taken. It updates a
 * list of the most recent and nearby posts based on distance from the user.
 *
 * @param postsViewModel ViewModel to access the list of all posts
 * @param context Application context to access the singleton LocationProvider
 */
class ProximityAndTimePostFetcher(private val postsViewModel: PostsViewModel, context: Context) {
  // LocationProvider instance to get user's current location
  private val locationProvider = LocationProviderSingleton.getInstance(context)

  // MutableStateFlow to hold the list of nearby posts
  private val _nearbyPosts = MutableStateFlow<List<Post>>(emptyList())
  val nearbyPosts: StateFlow<List<Post>> = _nearbyPosts

  /**
   * Fetches nearby posts with images based on the user's current location and the time they were
   * posted. Filters and sorts posts by distance and time, limiting results to the 3 closest posts.
   */
  fun fetchSortedPosts() {
    val userLocation = locationProvider.currentLocation.value
    if (userLocation == null) {
      Log.e("ProximityPostFetcher", "User location is null; cannot fetch nearby posts.")
      return
    }

    // Launch a coroutine to collect and process posts
    CoroutineScope(Dispatchers.IO).launch {
      postsViewModel.allPosts.collect { posts ->
        if (posts.isNotEmpty()) {
          // Map each post to a pair of (post, distance) from the user
          val sortedNearbyPosts =
              posts
                  .mapNotNull { post ->
                    // Calculate the distance from the user's location to each post's location
                    val distance =
                        LocationUtils.calculateDistance(
                            userLocation.latitude,
                            userLocation.longitude,
                            post.latitude,
                            post.longitude)

                    // Pair each post with its distance from the user
                    post to distance
                  }
                  // Sort first by distance (ascending), then by timestamp (descending)
                  .sortedWith(
                      compareBy<Pair<Post, Double>> { it.second } // Sort by distance
                          .thenByDescending { it.first.timestamp } // Sort by timestamp
                      )
                  // Take the 3 closest posts for now
                  .take(3)
                  // Extract only the post objects from the pairs
                  .map { it.first }

          // Update _nearbyPosts with the sorted list of nearby posts
          _nearbyPosts.update { sortedNearbyPosts }
        } else {
          Log.e("fetchPosts", "Posts are empty.")
        }
      }
    }
  }
}
