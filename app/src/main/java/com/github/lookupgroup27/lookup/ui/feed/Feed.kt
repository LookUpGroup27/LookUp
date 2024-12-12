// File: com/github/lookupgroup27/lookup/ui/feed/FeedScreen.kt
package com.github.lookupgroup27.lookup.ui.feed

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.github.lookupgroup27.lookup.R
import com.github.lookupgroup27.lookup.model.feed.ProximityAndTimePostFetcher
import com.github.lookupgroup27.lookup.model.location.LocationProviderSingleton
import com.github.lookupgroup27.lookup.model.post.Post
import com.github.lookupgroup27.lookup.ui.feed.components.PostItem
import com.github.lookupgroup27.lookup.ui.feed.components.calculatePostUpdates
import com.github.lookupgroup27.lookup.ui.feed.components.updateProfileRatings
import com.github.lookupgroup27.lookup.ui.navigation.BottomNavigationMenu
import com.github.lookupgroup27.lookup.ui.navigation.LIST_TOP_LEVEL_DESTINATION
import com.github.lookupgroup27.lookup.ui.navigation.NavigationActions
import com.github.lookupgroup27.lookup.ui.navigation.Route
import com.github.lookupgroup27.lookup.ui.post.PostsViewModel
import com.github.lookupgroup27.lookup.ui.profile.ProfileViewModel
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.delay

private const val LOCATION_PERMISSION_REQUEST_CODE = 1001
private const val NUMBER_OF_STARS = 3

/**
 * Main screen for displaying a feed of nearby posts. Each post includes the user's image, username,
 * location, and an average rating. The feed updates dynamically based on the user's location.
 *
 * The screen includes a background image, a top app bar, and a bottom navigation menu. If the user
 * hasn't granted location permissions or there are no posts, appropriate messages are shown.
 *
 * @param postsViewModel ViewModel for managing posts.
 * @param navigationActions Actions for navigating within the app.
 * @param profileViewModel ViewModel for managing user profiles.
 * @param initialNearbyPosts Optional parameter for testing, allows pre-loading posts.
 */
@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("StateFlowValueCalledInComposition")
@Composable
fun FeedScreen(
    postsViewModel: PostsViewModel,
    navigationActions: NavigationActions,
    profileViewModel: ProfileViewModel,
    initialNearbyPosts: List<Post>? = null
) {
  // Fetch user profile
  LaunchedEffect(Unit) { profileViewModel.fetchUserProfile() }

  val profile by profileViewModel.userProfile.collectAsState()
  val user = FirebaseAuth.getInstance().currentUser
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

  // Check for location permissions and fetch posts when granted.
  LaunchedEffect(Unit) {
    locationPermissionGranted =
        ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) ==
            PackageManager.PERMISSION_GRANTED

    if (!locationPermissionGranted) {
      Toast.makeText(
              context, context.getString(R.string.location_permission_required), Toast.LENGTH_LONG)
          .show()
    } else {
      while (locationProvider.currentLocation.value == null) {
        delay(500)
      }
      proximityAndTimePostFetcher.fetchSortedPosts()
    }
  }

  // Initialize post ratings based on the user profile.
  LaunchedEffect(nearbyPosts, profile) {
    nearbyPosts.forEach { post ->
      if (!postRatings.containsKey(post.uid)) {
        val savedRating = profile?.ratings?.get(post.uid) ?: 0
        val initialRating = List(NUMBER_OF_STARS) { index -> index < savedRating }
        postRatings[post.uid] = initialRating.toMutableList()
      }
    }
  }

  // Background Box with gradient overlay using drawBehind for efficiency.
  Box(
      modifier =
          Modifier.fillMaxSize().drawBehind {
            drawRect(
                brush =
                    Brush.verticalGradient(
                        colors =
                            listOf(
                                Color.Black.copy(alpha = 0.6f),
                                Color.Transparent,
                                Color.Black.copy(alpha = 0.6f))))
          }) {
        Image(
            painter = painterResource(R.drawable.background_blurred),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize())

        Scaffold(
            containerColor = Color.Transparent,
            topBar = {
              TopAppBar(
                  title = {
                    Text(
                        text = stringResource(R.string.nearby_posts_title),
                        style = MaterialTheme.typography.titleMedium.copy(color = Color.White))
                  },
                  colors =
                      TopAppBarDefaults.smallTopAppBarColors(
                          containerColor = Color.Black.copy(alpha = 0.5f)))
            },
            bottomBar = {
              Box(Modifier.testTag(stringResource(R.string.bottom_navigation_menu))) {
                BottomNavigationMenu(
                    onTabSelect = { destination -> navigationActions.navigateTo(destination) },
                    tabList = LIST_TOP_LEVEL_DESTINATION,
                    isUserLoggedIn = isUserLoggedIn,
                    selectedItem = Route.FEED)
              }
            },
            modifier = Modifier.testTag(stringResource(R.string.feed_screen_test_tag))) {
                innerPadding ->
              Column(
                  modifier =
                      Modifier.fillMaxSize().padding(innerPadding).padding(horizontal = 8.dp)) {
                    if (nearbyPosts.isEmpty()) {
                      // Loading or empty state
                      Box(
                          modifier =
                              Modifier.fillMaxSize()
                                  .testTag(stringResource(R.string.loading_indicator_test_tag)),
                          contentAlignment = Alignment.Center) {
                            if (!locationPermissionGranted) {
                              Text(
                                  text = stringResource(R.string.location_permission_required),
                                  style =
                                      MaterialTheme.typography.bodyLarge.copy(color = Color.White))
                            } else {
                              CircularProgressIndicator(color = Color.White)
                            }
                          }
                    } else {
                      LazyColumn(
                          modifier = Modifier.fillMaxSize(),
                          contentPadding = PaddingValues(vertical = 16.dp),
                          verticalArrangement = Arrangement.spacedBy(30.dp)) {
                            items(nearbyPosts) { post ->
                              PostItem(
                                  post = post,
                                  starStates =
                                      postRatings[post.uid] ?: List(NUMBER_OF_STARS) { false },
                                  onRatingChanged = { newRating ->
                                    val oldPostRatings =
                                        postRatings[post.uid] ?: List(NUMBER_OF_STARS) { false }
                                    val oldStarCounts = oldPostRatings.count { it }
                                    postRatings[post.uid] = newRating.toList()
                                    val starsCount = newRating.count { it }

                                    // Update user profile ratings using the utility function
                                    val newProfile =
                                        updateProfileRatings(
                                            currentProfile = profile,
                                            postUid = post.uid,
                                            starsCount = starsCount,
                                            username = username,
                                            bio = bio,
                                            email = email)
                                    profileViewModel.updateUserProfile(newProfile)

                                    // Update post details using the utility function
                                    val updatedPost =
                                        calculatePostUpdates(
                                            post = post,
                                            userEmail = userEmail,
                                            starsCount = starsCount,
                                            oldStarCounts = oldStarCounts)
                                    postsViewModel.updatePost(updatedPost)
                                  })
                            }
                          }
                    }
                  }
            }
      }
}
