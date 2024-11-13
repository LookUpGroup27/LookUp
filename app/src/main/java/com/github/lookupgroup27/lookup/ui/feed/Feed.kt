package com.github.lookupgroup27.lookup.ui

import android.Manifest
import android.app.Activity
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.github.lookupgroup27.lookup.model.feed.ProximityPostFetcher
import com.github.lookupgroup27.lookup.model.location.LocationProviderSingleton
import com.github.lookupgroup27.lookup.model.post.Post
import com.github.lookupgroup27.lookup.ui.feed.components.PostItem
import com.github.lookupgroup27.lookup.ui.navigation.BottomNavigationMenu
import com.github.lookupgroup27.lookup.ui.navigation.LIST_TOP_LEVEL_DESTINATION
import com.github.lookupgroup27.lookup.ui.navigation.NavigationActions
import com.github.lookupgroup27.lookup.ui.navigation.Route
import com.github.lookupgroup27.lookup.ui.post.PostsViewModel

private const val LOCATION_PERMISSION_REQUEST_CODE = 1001
/**
 * Main screen for displaying a feed of nearby posts. Each post includes the user's image and
 * username, and the list is updated dynamically based on the user's location.
 *
 * @param postsViewModel View Model for the posts
 * @param navigationActions Actions for navigation within the app
 */
@Composable
fun FeedScreen(
    postsViewModel: PostsViewModel,
    navigationActions: NavigationActions,
    initialNearbyPosts: List<Post>? = null // Optional parameter for testing
) {
  val context = LocalContext.current
  val locationProvider = LocationProviderSingleton.getInstance(context)
  val proximityPostFetcher = remember { ProximityPostFetcher(postsViewModel, context) }

  var locationPermissionGranted by remember { mutableStateOf(false) }
  val nearbyPosts by
      (initialNearbyPosts?.let { mutableStateOf(it) }
          ?: proximityPostFetcher.nearbyPosts.collectAsState())

  LaunchedEffect(Unit) {
    locationPermissionGranted =
        ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) ==
            PackageManager.PERMISSION_GRANTED

    if (!locationPermissionGranted) {
      ActivityCompat.requestPermissions(
          context as Activity,
          arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
          LOCATION_PERMISSION_REQUEST_CODE)
      Toast.makeText(
              context, "Location permission is required to access the map.", Toast.LENGTH_LONG)
          .show()
    } else {
      // Wait until location is available, then fetch posts
      while (locationProvider.currentLocation.value == null) {
        kotlinx.coroutines.delay(500)
      }
      proximityPostFetcher.fetchNearbyPostsWithImages()
    }
  }

  Scaffold(
      bottomBar = {
        Box(Modifier.testTag("BottomNavigationMenu")) {
          BottomNavigationMenu(
              onTabSelect = { destination -> navigationActions.navigateTo(destination) },
              tabList = LIST_TOP_LEVEL_DESTINATION,
              selectedItem = Route.FEED)
        }
      }) { innerPadding ->
        if (nearbyPosts.isEmpty()) {
          // Show a message or loading indicator if nearbyPosts hasn't been populated yet
          Box(
              modifier = Modifier.fillMaxSize().testTag("LoadingIndicator"),
              contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
              }
        } else {
          LazyColumn(
              contentPadding = innerPadding,
              modifier = Modifier.fillMaxSize().padding(horizontal = 8.dp),
              verticalArrangement = Arrangement.spacedBy(30.dp)) {
                items(nearbyPosts) { post -> PostItem(post = post) }
              }
        }
      }
}
