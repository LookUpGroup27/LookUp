package com.github.lookupgroup27.lookup.ui

import ProximityPostFetcher
import android.Manifest
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import coil.compose.rememberAsyncImagePainter
import com.github.lookupgroup27.lookup.model.location.LocationProviderSingleton
import com.github.lookupgroup27.lookup.model.post.Post
import com.github.lookupgroup27.lookup.model.post.PostsViewModel
import com.github.lookupgroup27.lookup.ui.navigation.BottomNavigationMenu
import com.github.lookupgroup27.lookup.ui.navigation.LIST_TOP_LEVEL_DESTINATION
import com.github.lookupgroup27.lookup.ui.navigation.NavigationActions
import com.github.lookupgroup27.lookup.ui.navigation.Route

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
) {
  val context = LocalContext.current
  val locationProvider = LocationProviderSingleton.getInstance(context)
  val proximityPostFetcher = remember { ProximityPostFetcher(postsViewModel, context) }
  var hasLocationPermission by remember { mutableStateOf(false) }

  // Collect nearby posts from the fetcher as a state
  val nearbyPosts by proximityPostFetcher.nearbyPosts.collectAsState()

  // Trigger fetching nearby posts when the composable is launched
  LaunchedEffect(Unit) {
    hasLocationPermission =
        ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) ==
            PackageManager.PERMISSION_GRANTED
    if (hasLocationPermission) {
      locationProvider.requestLocationUpdates()
    } else {
      // Request permission
      ActivityCompat.requestPermissions(
          context as Activity,
          arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
          LOCATION_PERMISSION_REQUEST_CODE)
      Toast.makeText(
              context, "Location permission is required to access the map.", Toast.LENGTH_LONG)
          .show()
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
              items(nearbyPosts) { post -> PostItem(post = post) }
            }
      }
}

/**
 * Composable that displays an individual post, showing the user's image and username. The image is
 * loaded asynchronously using the post's URI.
 *
 * @param post The post data, including image URI and username
 */
@Composable
fun PostItem(post: Post) {
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
  }
}
