package com.github.lookupgroup27.lookup.ui.feed

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.github.lookupgroup27.lookup.R
import com.github.lookupgroup27.lookup.model.feed.ProximityAndTimePostFetcher
import com.github.lookupgroup27.lookup.model.location.LocationProviderSingleton
import com.github.lookupgroup27.lookup.model.post.Post
import com.github.lookupgroup27.lookup.model.profile.UserProfile
import com.github.lookupgroup27.lookup.ui.feed.components.PostItem
import com.github.lookupgroup27.lookup.ui.googlemap.components.SelectedPostMarker
import com.github.lookupgroup27.lookup.ui.navigation.BottomNavigationMenu
import com.github.lookupgroup27.lookup.ui.navigation.LIST_TOP_LEVEL_DESTINATION
import com.github.lookupgroup27.lookup.ui.navigation.NavigationActions
import com.github.lookupgroup27.lookup.ui.navigation.Route
import com.github.lookupgroup27.lookup.ui.post.PostsViewModel
import com.github.lookupgroup27.lookup.ui.profile.ProfileViewModel
import com.google.firebase.auth.FirebaseAuth

private const val LOCATION_PERMISSION_REQUEST_CODE = 1001
private const val NUMBER_OF_STARS = 3
/**
 * Main screen for displaying a feed of nearby posts. Each post includes the user's image and
 * username, and the list is updated dynamically based on the user's location.
 *
 * @param postsViewModel View Model for the posts
 * @param navigationActions Actions for navigation within the app
 */
@SuppressLint("StateFlowValueCalledInComposition")
@Composable
fun FeedScreen(
    postsViewModel: PostsViewModel,
    navigationActions: NavigationActions,
    profileViewModel: ProfileViewModel,
    initialNearbyPosts: List<Post>? = null // Optional parameter for testing
) {
  profileViewModel.fetchUserProfile()
  val profile = profileViewModel.userProfile.value
  val user = FirebaseAuth.getInstance().currentUser // Get the current signed-in user
  val isUserLoggedIn = user != null
  val userEmail = user?.email ?: ""
  val username by remember { mutableStateOf(profile?.username ?: "") }
  val bio by remember { mutableStateOf(profile?.bio ?: "") }
  val email by remember { mutableStateOf(userEmail) }

  val context = LocalContext.current
  val locationProvider = LocationProviderSingleton.getInstance(context)
  val proximityAndTimePostFetcher = remember {
    ProximityAndTimePostFetcher(postsViewModel, context)
  }

  var locationPermissionGranted by remember { mutableStateOf(false) }
  val unfilteredPosts by
      (initialNearbyPosts?.let { mutableStateOf(it) }
          ?: proximityAndTimePostFetcher.nearbyPosts.collectAsState())
  val nearbyPosts = unfilteredPosts.filter { it.username != userEmail }

  val postRatings = remember { mutableStateMapOf<String, List<Boolean>>() }

  LaunchedEffect(Unit) {
    locationPermissionGranted =
        ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) ==
            PackageManager.PERMISSION_GRANTED

    if (!locationPermissionGranted) {
      Toast.makeText(
              context, context.getString(R.string.location_permission_required), Toast.LENGTH_LONG)
          .show()
    } else {
      // Wait until location is available, then fetch posts
      while (locationProvider.currentLocation.value == null) {
        kotlinx.coroutines.delay(500)
      }
      proximityAndTimePostFetcher.fetchSortedPosts()
    }
  }

  LaunchedEffect(nearbyPosts, profile) {
    nearbyPosts.forEach { post ->
      if (!postRatings.containsKey(post.uid)) {
        // Get saved rating from profile, or initialize with all false
        val savedRating = profile?.ratings?.get(post.uid) ?: 0
        val initialRating = List(NUMBER_OF_STARS) { index -> index < savedRating }
        postRatings[post.uid] = initialRating.toMutableList()
      }
    }
  }

  Scaffold(
      bottomBar = {
        Box(Modifier.testTag(stringResource(R.string.bottom_navigation_menu))) {
          BottomNavigationMenu(
              onTabSelect = { destination -> navigationActions.navigateTo(destination) },
              tabList = LIST_TOP_LEVEL_DESTINATION,
              isUserLoggedIn = isUserLoggedIn,
              selectedItem = Route.FEED)
        }
      },
      modifier = Modifier.testTag(stringResource(R.string.feed_screen_test_tag))) { innerPadding ->
        if (nearbyPosts.isEmpty()) {
          // Show a message or loading indicator if nearbyPosts hasn't been populated yet
          Box(
              modifier =
                  Modifier.fillMaxSize()
                      .testTag(stringResource(R.string.loading_indicator_test_tag)),
              contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
              }
        } else {
          LazyColumn(
              contentPadding = innerPadding,
              modifier = Modifier.fillMaxSize().padding(horizontal = 8.dp),
              verticalArrangement = Arrangement.spacedBy(30.dp)) {
                items(nearbyPosts) { post ->
                  PostItem(
                      post = post,
                      starStates = postRatings[post.uid] ?: mutableListOf(false, false, false),
                      onRatingChanged = { newRating ->
                        val oldPostRatings =
                            postRatings[post.uid] ?: mutableListOf(false, false, false)
                        val oldStarCounts = oldPostRatings.count { it }
                        // Directly modify the existing starStates list to avoid creating a new list
                        postRatings[post.uid] = newRating.toList()
                        // Update the stars count based on the new rating
                        val starsCount = newRating.count { it }
                        // Update user profile with the new rating count
                        val updatedRatings = profile?.ratings?.toMutableMap()
                        updatedRatings?.set(post.uid, starsCount)
                        val newProfile: UserProfile =
                            profile?.copy(
                                username = username,
                                bio = bio,
                                email = email,
                                ratings = updatedRatings ?: emptyMap())
                                ?: UserProfile(
                                    username = username,
                                    bio = bio,
                                    email = email,
                                    ratings = updatedRatings ?: emptyMap())
                        profileViewModel.updateUserProfile(newProfile)

                        val isReturningUser = post.ratedBy.contains(userEmail)
                        val newStarsCount =
                            if (isReturningUser) post.starsCount - oldStarCounts + starsCount
                            else post.starsCount + starsCount
                        val newUsersNumber =
                            if (isReturningUser) post.usersNumber else post.usersNumber + 1
                        val newAvg = newStarsCount.toDouble() / newUsersNumber

                        postsViewModel.updatePost(
                            post.copy(
                                averageStars = newAvg,
                                starsCount = newStarsCount,
                                usersNumber = newUsersNumber,
                                ratedBy =
                                    if (!isReturningUser) {
                                      post.ratedBy + userEmail
                                    } else {
                                      post.ratedBy
                                    }))
                      },
                      onAddressClick = { clickedPost ->
                        val selectedMarker =
                            SelectedPostMarker(
                                postId = clickedPost.uid,
                                latitude = clickedPost.latitude,
                                longitude = clickedPost.longitude)
                        navigationActions.navigateToMapWithPost(
                            post.uid, post.latitude, post.longitude, false)
                      })
                  Spacer(modifier = Modifier.height(20.dp))
                }
              }
        }
      }
}
