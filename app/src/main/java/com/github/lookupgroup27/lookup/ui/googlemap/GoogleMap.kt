package com.github.lookupgroup27.lookup.ui.googlemap

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import com.github.lookupgroup27.lookup.model.location.LocationProviderSingleton
import com.github.lookupgroup27.lookup.ui.googlemap.components.MapControls
import com.github.lookupgroup27.lookup.ui.googlemap.components.MapView
import com.github.lookupgroup27.lookup.ui.navigation.BottomNavigationMenu
import com.github.lookupgroup27.lookup.ui.navigation.LIST_TOP_LEVEL_DESTINATION
import com.github.lookupgroup27.lookup.ui.navigation.NavigationActions
import com.github.lookupgroup27.lookup.ui.navigation.Screen
import com.github.lookupgroup27.lookup.ui.post.PostsViewModel
import com.google.firebase.auth.FirebaseAuth

/**
 * Screen that displays a Google Map with user's location and posts.
 *
 * @param navigationActions Actions to navigate to different screens.
 * @param postsViewModel ViewModel to fetch posts.
 */
private const val LOCATION_PERMISSION_REQUEST_CODE = 1001

@Composable
fun GoogleMapScreen(
    navigationActions: NavigationActions,
    postsViewModel: PostsViewModel = viewModel()
) {
  val context = LocalContext.current
  var hasLocationPermission by remember { mutableStateOf(false) }
  val locationProvider = LocationProviderSingleton.getInstance(context)
  var autoCenteringEnabled by remember { mutableStateOf(true) } // New state for auto-centering
  val auth = remember { FirebaseAuth.getInstance() }
  val isLoggedIn = auth.currentUser != null

  val allPosts by postsViewModel.allPosts.collectAsState()

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

  Scaffold(
      modifier = Modifier.testTag("googleMapScreen"),
      bottomBar = {
        BottomNavigationMenu(
            onTabSelect = { route -> navigationActions.navigateTo(route) },
            tabList = LIST_TOP_LEVEL_DESTINATION,
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
