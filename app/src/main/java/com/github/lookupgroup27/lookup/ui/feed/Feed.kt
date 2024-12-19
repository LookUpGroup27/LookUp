package com.github.lookupgroup27.lookup.ui.feed

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
    initialNearbyPosts: List<Post>? = null,
    testNoLoca: Boolean = false
) {
  // Fetch user profile
  LaunchedEffect(Unit) {
    Log.d("FeedScreen", "Fetching user profile")
    profileViewModel.fetchUserProfile()
  }

  // User-related state
  val profile by profileViewModel.userProfile.collectAsState()
  val user = FirebaseAuth.getInstance().currentUser
  val isUserLoggedIn = user != null
  val userEmail = user?.email ?: ""
  val username by remember { mutableStateOf(profile?.username ?: "") }
  val bio by remember { mutableStateOf(profile?.bio ?: "") }
  val email by remember { mutableStateOf(userEmail) }

  // Location setup
  val context = LocalContext.current
  val locationProvider = LocationProviderSingleton.getInstance(context)
  var locationPermissionGranted by remember {
    mutableStateOf(
        ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) ==
            PackageManager.PERMISSION_GRANTED)
  }

  // Initialize PostsViewModel with context
  LaunchedEffect(Unit) {
    Log.d("FeedScreen", "Setting context in PostsViewModel")
    postsViewModel.setContext(context)
  }

  // Permission request launcher
  val permissionLauncher =
      rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
        locationPermissionGranted = isGranted
        if (isGranted && !testNoLoca) {
          locationProvider.requestLocationUpdates()
          postsViewModel.fetchSortedPosts()
        } else {
          Toast.makeText(
                  context,
                  "Location permission is required. Please enable it in the app settings.",
                  Toast.LENGTH_LONG)
              .show()
        }
      }

  // Trigger location updates when permission is granted
  LaunchedEffect(locationPermissionGranted) {
    if (locationPermissionGranted) {
      locationProvider.requestLocationUpdates()

      while (locationProvider.currentLocation.value == null) {
        delay(200) // Retry every 200ms
      }
      postsViewModel.fetchSortedPosts()
    }
  }

  val unfilteredPosts by
      (initialNearbyPosts?.let { mutableStateOf(it) }
          ?: postsViewModel.nearbyPosts.collectAsState())
  val nearbyPosts = unfilteredPosts.filter { it.userMail != userEmail }

  val postRatings = remember { mutableStateMapOf<String, List<Boolean>>() }

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

  // UI Structure
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
                      Box(
                          modifier = Modifier.fillMaxSize().testTag("loading_indicator_test_tag"),
                          contentAlignment = Alignment.Center) {
                            when {
                              (testNoLoca || !locationPermissionGranted) -> {
                                Log.d("FeedScreen", "Location permission not granted")

                                // Show permission request button
                                Button(
                                    onClick = {
                                      permissionLauncher.launch(
                                          Manifest.permission.ACCESS_FINE_LOCATION)
                                    },
                                    modifier = Modifier.testTag("enable_location_button"),
                                    colors =
                                        ButtonDefaults.buttonColors(
                                            MaterialTheme.colorScheme.primary)) {
                                      Text("Enable Location")
                                    }
                              }
                              locationProvider.currentLocation.value == null -> {
                                CircularProgressIndicator(color = Color.White)
                              }
                              else -> {
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.Center) {
                                      Image(
                                          painter =
                                              painterResource(R.drawable.no_images_placeholder),
                                          contentDescription =
                                              stringResource(R.string.feed_no_images_available),
                                          modifier =
                                              Modifier.size(180.dp)
                                                  .testTag("no_images_placeholder"))
                                      Spacer(modifier = Modifier.height(16.dp))
                                      Text(
                                          text = stringResource(R.string.feed_no_images_available),
                                          modifier = Modifier.testTag("feed_no_images_available"),
                                          style =
                                              MaterialTheme.typography.bodyLarge.copy(
                                                  color = Color.White))
                                    }
                              }
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

                                    // Update user profile ratings
                                    val newProfile =
                                        updateProfileRatings(
                                            currentProfile = profile,
                                            postUid = post.uid,
                                            starsCount = starsCount,
                                            username = username,
                                            bio = bio,
                                            email = email)
                                    profileViewModel.updateUserProfile(newProfile)

                                    // Update post details
                                    val updatedPost =
                                        calculatePostUpdates(
                                            post = post,
                                            userEmail = userEmail,
                                            starsCount = starsCount,
                                            oldStarCounts = oldStarCounts)
                                    postsViewModel.updatePost(updatedPost)
                                  },
                                  onAddressClick = { clickedPost ->
                                    val selectedMarker =
                                        SelectedPostMarker(
                                            postId = clickedPost.uid,
                                            latitude = clickedPost.latitude,
                                            longitude = clickedPost.longitude)
                                    navigationActions.navigateToMapWithPost(
                                        post.uid, post.latitude, post.longitude, false)
                                  },
                                  onImageClick = { imageUrl, username, description ->
                                    navigationActions.navigateToFullScreen(
                                        imageUrl, username, description)
                                  })
                            }
                          }
                    }
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
    currentProfile: UserProfile?,
    postUid: String,
    starsCount: Int,
    username: String,
    bio: String,
    email: String
): UserProfile {
  val updatedRatings =
      currentProfile?.ratings?.toMutableMap()?.apply { this[postUid] = starsCount }
          ?: mutableMapOf(postUid to starsCount)

  return currentProfile?.copy(
      username = username, bio = bio, email = email, ratings = updatedRatings)
      ?: UserProfile(username = username, bio = bio, email = email, ratings = updatedRatings)
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
      if (starsCount == 0) {
        post.starsCount - oldStarCounts
      } else if (isReturningUser) post.starsCount - oldStarCounts + starsCount
      else post.starsCount + starsCount
  val newRatedBy =
      if (starsCount == 0) {
        post.ratedBy.filter { x -> x != userEmail }
      } else if (!isReturningUser) {
        post.ratedBy + userEmail
      } else {
        post.ratedBy
      }
  val newUsersNumber = newRatedBy.size
  val newAvg = if (newUsersNumber != 0) newStarsCount.toDouble() / newUsersNumber else 0.0

  return post.copy(
      averageStars = newAvg,
      starsCount = newStarsCount,
      usersNumber = newUsersNumber,
      ratedBy = newRatedBy)
}
