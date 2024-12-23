package com.github.lookupgroup27.lookup.ui.googlemap

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.ActivityInfo
import android.content.pm.PackageManager
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.*
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import com.github.lookupgroup27.lookup.R
import com.github.lookupgroup27.lookup.model.location.LocationProviderSingleton
import com.github.lookupgroup27.lookup.ui.googlemap.components.*
import com.github.lookupgroup27.lookup.ui.navigation.*
import com.github.lookupgroup27.lookup.ui.post.PostsViewModel
import com.github.lookupgroup27.lookup.ui.profile.ProfileViewModel
import com.github.lookupgroup27.lookup.ui.theme.DarkPurple
import com.google.firebase.auth.FirebaseAuth

private const val NUMBER_OF_STARS: Int = 3

/**
 * Screen that displays a Google Map with user's location and posts.
 *
 * @param navigationActions Actions to navigate to different screens.
 * @param postsViewModel ViewModel to fetch posts.
 * @param profileViewModel ViewModel to fetch user profile.
 * @param selectedPostMarker The selected post marker.
 * @param initialAutoCenterEnabled Whether auto-centering is enabled.
 * @param testNoLoc use for test purpose: it simulates the case before user grants location
 *   permission
 */
@SuppressLint("StateFlowValueCalledInComposition")
@Composable
fun GoogleMapScreen(
    navigationActions: NavigationActions,
    postsViewModel: PostsViewModel = viewModel(),
    profileViewModel: ProfileViewModel = viewModel(),
    selectedPostMarker: SelectedPostMarker? = null,
    initialAutoCenterEnabled: Boolean = true,
    testNoLoca: Boolean = false
) {
  val context = LocalContext.current

  // Lock the screen orientation to portrait mode.
  DisposableEffect(Unit) {
    val activity = context as? ComponentActivity
    activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
    onDispose { activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED }
  }

  val locationProvider = LocationProviderSingleton.getInstance(context)
  var hasLocationPermission by remember {
    mutableStateOf(
        ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) ==
            PackageManager.PERMISSION_GRANTED)
  }
  var autoCenteringEnabled by remember {
    mutableStateOf(initialAutoCenterEnabled)
  } // New state for auto-centering
  val auth = remember { FirebaseAuth.getInstance() }
  val isLoggedIn = auth.currentUser != null
  val allPosts by postsViewModel.allPosts.collectAsState()

  profileViewModel.fetchUserProfile()
  val profile = profileViewModel.userProfile.value
  val user = auth.currentUser
  val userEmail = user?.email ?: ""
  val username by remember { mutableStateOf(profile?.username ?: "") }
  val bio by remember { mutableStateOf(profile?.bio ?: "") }
  val email by remember { mutableStateOf(userEmail) }
  val postRatings = remember { mutableStateMapOf<String, List<Boolean>>() }
  var highlightedPost by remember(selectedPostMarker) { mutableStateOf(selectedPostMarker) }

  // Permission request launcher
  val permissionLauncher =
      rememberLauncherForActivityResult(contract = ActivityResultContracts.RequestPermission()) {
          isGranted: Boolean ->
        hasLocationPermission = isGranted
        if (isGranted) {
          locationProvider.requestLocationUpdates()
        } else {
          Toast.makeText(
                  context,
                  "Location permission is required. Please enable it in the app settings.",
                  Toast.LENGTH_LONG)
              .show()
        }
      }

  // Request location updates if permission is granted
  LaunchedEffect(hasLocationPermission) {
    if (hasLocationPermission) {
      locationProvider.requestLocationUpdates()
    }
  }

  // Sync posts and ratings
  LaunchedEffect(allPosts, profile) {
    if (userEmail.isEmpty()) {
      if (hasLocationPermission)
          Toast.makeText(context, "Please sign in to rate photos on the feed.", Toast.LENGTH_LONG)
              .show()
    }
    allPosts.forEach { post ->
      if (!postRatings.containsKey(post.uid)) {
        val savedRating = profile?.ratings?.get(post.uid) ?: 0
        val initialRating = List(NUMBER_OF_STARS) { index -> index < savedRating }
        postRatings[post.uid] = initialRating.toMutableList()
      }
    }
  }

  LaunchedEffect(selectedPostMarker) { highlightedPost = selectedPostMarker }

  Scaffold(
      modifier = Modifier.testTag("googleMapScreen"),
      bottomBar = {
        BottomNavigationMenu(
            onTabSelect = { route -> navigationActions.navigateTo(route) },
            tabList = LIST_TOP_LEVEL_DESTINATION,
            isUserLoggedIn = isLoggedIn,
            selectedItem = navigationActions.currentRoute())
      },
      floatingActionButton = {
        if (hasLocationPermission)
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
          // Check and request permission
          if (!hasLocationPermission || testNoLoca) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
              Image(
                  painter = painterResource(id = R.drawable.landing_screen_bckgrnd),
                  contentDescription = "Background",
                  modifier = Modifier.fillMaxSize().testTag("background_image").blur(20.dp),
                  contentScale = ContentScale.Crop)

              Button(
                  onClick = { permissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION) },
                  modifier = Modifier.testTag("enable_location_button"),
                  colors = ButtonDefaults.buttonColors(DarkPurple),
              ) {
                Text("Enable Location")
              }
            }
          } else {
            // Buttons to toggle map modes
            MapControls(
                autoCenteringEnabled = autoCenteringEnabled,
                onCenteringToggle = { autoCenteringEnabled = it })

            // Map view below the buttons
            MapView(
                padding,
                hasLocationPermission,
                locationProvider.currentLocation.value,
                autoCenteringEnabled,
                allPosts,
                highlightedPost = highlightedPost)
          }
        }
      })
}
