package com.github.lookupgroup27.lookup.ui.googlemap

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import android.location.Location
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import com.github.lookupgroup27.lookup.model.location.LocationProviderSingleton
import com.github.lookupgroup27.lookup.model.post.Post
import com.github.lookupgroup27.lookup.model.post.PostsViewModel
import com.github.lookupgroup27.lookup.ui.image.ImagePreviewDialog
import com.github.lookupgroup27.lookup.ui.navigation.BottomNavigationMenu
import com.github.lookupgroup27.lookup.ui.navigation.LIST_TOP_LEVEL_DESTINATION
import com.github.lookupgroup27.lookup.ui.navigation.NavigationActions
import com.github.lookupgroup27.lookup.ui.navigation.Screen
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.auth.FirebaseAuth
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapType
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState

private const val LOCATION_PERMISSION_REQUEST_CODE = 1001

@Composable
fun GoogleMapScreen(navigationActions: NavigationActions, postsViewModel: PostsViewModel = viewModel()) {
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
      floatingActionButton = {
          FloatingActionButton(
              onClick = {
                  if (isLoggedIn) {
                      navigationActions.navigateTo(Screen.TAKE_IMAGE)
                  } else {
                      navigationActions.navigateTo(Screen.AUTH)
                  }
              },
              modifier = Modifier.testTag("fab_take_picture")
          ) {
              Icon(Icons.Default.Add, contentDescription = "Take Picture")
          }
      },
      content = { padding ->
        Column {
          // Add buttons to toggle map modes
          Box(
              modifier =
                  Modifier.fillMaxWidth()
                      .background(Color(0xFF0D1023)) // Dark blue
                      .padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween) {
                      Button(onClick = { autoCenteringEnabled = true }) {
                        Text(text = "Auto Center On")
                      }
                      Button(onClick = { autoCenteringEnabled = false }) {
                        Text(text = "Auto Center Off")
                      }
                    }
              }

          // Map view below the buttons
          MapView(
              padding,
              hasLocationPermission,
              locationProvider.currentLocation.value,
              autoCenteringEnabled, // Pass the state
              allPosts
              )
        }
      }

  )
}

@Composable
fun MapView(
    padding: PaddingValues,
    hasLocationPermission: Boolean,
    location: Location?,
    autoCenteringEnabled: Boolean,
    posts: List<Post>
) {
    //var mapProperties by remember { mutableStateOf(MapProperties(mapType = MapType.NORMAL)) }
    var mapProperties by remember {
        mutableStateOf(
            MapProperties(
                mapType = MapType.NORMAL,
                //isMyLocationEnabled = false // Enable the my location layer
            )
        )
    }
    var mapUiSettings by remember { mutableStateOf(MapUiSettings(zoomControlsEnabled = false)) }
    val cameraPositionState = rememberCameraPositionState()

    // Enable location layer only when permissions are granted
    LaunchedEffect(hasLocationPermission) {
        if (hasLocationPermission) {
            mapProperties = mapProperties.copy(isMyLocationEnabled = true)
        }
    }

    // State for showing image preview dialog
    var selectedPost by remember { mutableStateOf<Post?>(null) }

    // Update the camera position whenever location changes
    LaunchedEffect(location, autoCenteringEnabled) {
        if (hasLocationPermission && location != null && autoCenteringEnabled) {
            val latLng = LatLng(location.latitude, location.longitude)
            val cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 5f)
            cameraPositionState.animate(cameraUpdate)
        }
    }

    GoogleMap(
        modifier = Modifier.fillMaxSize().padding(padding),
        properties = mapProperties,
        uiSettings = mapUiSettings,
        cameraPositionState = cameraPositionState
    ) {
        // Add markers for each post
        posts.forEach { post ->
            val latLng = LatLng(post.latitude, post.longitude)
            Log.d("MapView", "Adding marker at (${post.latitude}, ${post.longitude}) for URI: ${post.uri}")
            Marker(
                state = MarkerState(position = latLng),
                title = post.username,
                icon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE), // Change color
                onClick = {
                    // When the marker is clicked, open the image preview dialog
                    selectedPost = post
                    true
                }
            )
        }

        // Display the ImagePreviewDialog when a post is selected
        selectedPost?.let {
            ImagePreviewDialog(
                uri = it.uri,
                username = it.username,
                onDismiss = { selectedPost = null }
            )
        }

        // Add a marker for the user's current location
        /*if (hasLocationPermission && location != null) {
            val latLng = LatLng(location.latitude, location.longitude)
            Marker(state = MarkerState(position = latLng), title = "You are here")
       }*/
    }
}