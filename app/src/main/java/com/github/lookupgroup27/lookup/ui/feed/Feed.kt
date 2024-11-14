package com.github.lookupgroup27.lookup.ui.feed

import ProximityPostFetcher
import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.pm.PackageManager
import android.widget.Toast
import androidx.compose.foundation.Image
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import coil.compose.rememberAsyncImagePainter
import com.github.lookupgroup27.lookup.R
import com.github.lookupgroup27.lookup.model.location.LocationProviderSingleton
import com.github.lookupgroup27.lookup.model.post.Post
import com.github.lookupgroup27.lookup.model.profile.UserProfile
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
    profileViewModel: ProfileViewModel
) {
  profileViewModel.fetchUserProfile()
  val profile = profileViewModel.userProfile.value
  val user = FirebaseAuth.getInstance().currentUser // Get the current signed-in user
  val userEmail = user?.email ?: ""
  val username by remember { mutableStateOf(profile?.username ?: "") }
  val bio by remember { mutableStateOf(profile?.bio ?: "") }
  val email by remember { mutableStateOf(userEmail) }
  val context = LocalContext.current
  val locationProvider = LocationProviderSingleton.getInstance(context)
  val proximityPostFetcher = remember { ProximityPostFetcher(postsViewModel, context) }
  var hasLocationPermission by remember { mutableStateOf(false) }

  // Collect nearby posts from the fetcher as a state
  val nearbyPosts by proximityPostFetcher.nearbyPosts.collectAsState()

  val ratings by remember { profile?.ratings ?: emptyMap() }
  val postRatings = remember { mutableStateMapOf<String, List<Boolean>>() }

  LaunchedEffect(Unit) {
    hasLocationPermission =
        ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) ==
            PackageManager.PERMISSION_GRANTED
    if (hasLocationPermission) {
      locationProvider.requestLocationUpdates()
    } else {
      ActivityCompat.requestPermissions(
          context as Activity,
          arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
          LOCATION_PERMISSION_REQUEST_CODE)
      Toast.makeText(
              context, "Location permission is required to access the map.", Toast.LENGTH_LONG)
          .show()
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
    proximityPostFetcher.fetchNearbyPostsWithImages()
  }

  // Main layout of the feed screen
  Scaffold(
      bottomBar = {
        BottomNavigationMenu(
            onTabSelect = { destination -> navigationActions.navigateTo(destination) },
            tabList = LIST_TOP_LEVEL_DESTINATION,
            selectedItem = Route.FEED)
      }) { innerPadding ->
        LazyColumn(
            contentPadding = innerPadding,
            modifier = Modifier.fillMaxSize().padding(horizontal = 8.dp),
            verticalArrangement = Arrangement.spacedBy(30.dp)) {
              items(nearbyPosts) { post ->
                // Pass the rating state for each post and update function
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
                    })
                Spacer(modifier = Modifier.height(20.dp))
              }
            }
      }
}

/**
 * Composable that displays an individual post, showing the user's image and username, and allows
 * the user to rate the post.
 *
 * @param post The post data, including image URI and username
 * @param starStates List representing filled/unfilled states for each star
 * @param onRatingChanged Callback to update the star rating in the parent composable
 */
@Composable
fun PostItem(post: Post, starStates: List<Boolean>, onRatingChanged: (List<Boolean>) -> Unit) {
  Column(modifier = Modifier.fillMaxWidth().padding(8.dp)) {
    // Display the username at the top of each post item
    Text(
        text = post.username,
        style =
            MaterialTheme.typography.bodyMedium.copy(
                fontWeight = FontWeight.Bold, color = Color.Black),
        modifier = Modifier.padding(start = 4.dp).testTag("UsernameTag_${post.username}"))

    // Display image using the dynamically fetched URI
    Image(
        painter = rememberAsyncImagePainter(post.uri), // Coil loads image from URI
        contentDescription = "Post Image for ${post.username}",
        modifier = Modifier.fillMaxWidth().height(300.dp),
        contentScale = ContentScale.Crop)

    // Star rating row
    Row {
      // Loop through each star
      starStates.forEachIndexed { index, isFilled ->
        IconButton(
            onClick = {
              // Toggle stars up to the clicked index
              val newRating =
                  if (isFilled) {
                    // If the clicked star is filled, unfill it and any stars to the right
                    starStates.mapIndexed { i, _ -> i < index }
                  } else {
                    // Otherwise, fill all stars up to and including the clicked one
                    starStates.mapIndexed { i, _ -> i <= index }
                  }
              onRatingChanged(newRating)
            },
            modifier = Modifier.size(36.dp).testTag("Star_${index + 1}_${post.uid}")) {
              Image(
                  painter =
                      painterResource(
                          id = if (isFilled) R.drawable.full_star else R.drawable.empty_star),
                  contentDescription = "Star")
            }
      }
      Text(
          text = "Average rating: ${"%.1f".format(post.averageStars)}",
          modifier =
              Modifier.fillMaxWidth().padding(start = 4.dp).testTag("AverageRatingTag_${post.uid}"),
          textAlign = TextAlign.End,
          style =
              MaterialTheme.typography.bodyMedium.copy(
                  fontWeight = FontWeight.Bold, color = Color.Black))
    }
  }
}
