package com.github.lookupgroup27.lookup.ui.googlemap

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.pm.PackageManager
import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.*
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import com.github.lookupgroup27.lookup.model.location.LocationProviderSingleton
import com.github.lookupgroup27.lookup.ui.googlemap.components.*
import com.github.lookupgroup27.lookup.ui.navigation.*
import com.github.lookupgroup27.lookup.ui.post.PostsViewModel
import com.github.lookupgroup27.lookup.ui.profile.ProfileViewModel
import com.google.firebase.auth.FirebaseAuth

private const val LOCATION_PERMISSION_REQUEST_CODE: Int = 1001
private const val NUMBER_OF_STARS: Int = 3

/**
 * Screen that displays a Google Map with user's location and posts.
 *
 * @param navigationActions Actions to navigate to different screens.
 * @param postsViewModel ViewModel to fetch posts.
 * @param profileViewModel ViewModel to fetch user profile.
 */
@SuppressLint("StateFlowValueCalledInComposition")
@Composable
fun GoogleMapScreen(
    navigationActions: NavigationActions,
    postsViewModel: PostsViewModel = viewModel(),
    profileViewModel: ProfileViewModel = viewModel()
) {
  val context = LocalContext.current
  var hasLocationPermission by remember { mutableStateOf(false) }
  val locationProvider = LocationProviderSingleton.getInstance(context)
  var autoCenteringEnabled by remember { mutableStateOf(true) } // New state for auto-centering
  val auth = remember { FirebaseAuth.getInstance() }
  val isLoggedIn = auth.currentUser != null
  val allPosts by postsViewModel.allPosts.collectAsState()

  profileViewModel.fetchUserProfile()
  val profile = profileViewModel.userProfile.value
  val user = FirebaseAuth.getInstance().currentUser // Get the current signed-in user
  val userEmail = user?.email ?: ""
  val username by remember { mutableStateOf(profile?.username ?: "") }
  val bio by remember { mutableStateOf(profile?.bio ?: "") }
  val email by remember { mutableStateOf(userEmail) }
  val postRatings = remember { mutableStateMapOf<String, List<Boolean>>() }

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
  }

  LaunchedEffect(allPosts, profile) {
    if (userEmail.isEmpty()) {
      Toast.makeText(context, "Please sign in to rate photos on the feed.", Toast.LENGTH_LONG)
          .show()
    }
    allPosts.forEach { post ->
      if (!postRatings.containsKey(post.uid)) {
        // Get saved rating from profile, or initialize with all false
        val savedRating = profile?.ratings?.get(post.uid) ?: 0
        val initialRating = List(NUMBER_OF_STARS) { index -> index < savedRating }
        postRatings[post.uid] = initialRating.toMutableList()
      }
    }
  }

  Scaffold(
      modifier = Modifier.testTag("googleMapScreen"),
      bottomBar = {
        BottomNavigationMenu(
            onTabSelect = { route -> navigationActions.navigateTo(route) },
            tabList = LIST_TOP_LEVEL_DESTINATION,
            isUserLoggedIn = isLoggedIn,
            selectedItem = navigationActions.currentRoute())
      },
      // Floating action button to take a picture
      floatingActionButton = {
        FloatingActionButton(
            onClick = {
              if (isLoggedIn) {
                navigationActions.navigateTo(Screen.TAKE_IMAGE)
              } else {
                Toast.makeText(context, "Please log in to take a picture.", Toast.LENGTH_LONG)
                    .show()
                navigationActions.navigateTo(Screen.AUTH)
              }
            },
            modifier = Modifier.testTag("fab_take_picture")) {
              Icon(Icons.Default.Add, contentDescription = "Take Picture")
            }
      },
      content = { padding ->
        Column {
          // Buttons to toggle map modes

          MapControls(
              autoCenteringEnabled = autoCenteringEnabled,
              onCenteringToggle = { autoCenteringEnabled = it })

          // Map view below the buttons
          MapView(
              padding,
              hasLocationPermission,
              locationProvider.currentLocation.value,
              autoCenteringEnabled, // Pass the state
              allPosts)
        }
      })
}
