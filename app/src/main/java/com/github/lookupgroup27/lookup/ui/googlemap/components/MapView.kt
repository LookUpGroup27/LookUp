package com.github.lookupgroup27.lookup.ui.googlemap.components

import android.location.Location
import android.util.Log
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.github.lookupgroup27.lookup.model.post.Post
import com.github.lookupgroup27.lookup.ui.image.ImagePreviewDialog
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapType
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.rememberCameraPositionState

/**
 * Composable that displays a Google Map with markers for posts.
 *
 * @param padding Padding values for the map
 * @param hasLocationPermission Whether the app has location permission
 * @param location The user's location
 * @param autoCenteringEnabled Whether auto centering is enabled
 * @param posts The list of posts to display on the map
 */
@Composable
fun MapView(
    padding: PaddingValues,
    hasLocationPermission: Boolean,
    location: Location?,
    autoCenteringEnabled: Boolean,
    posts: List<Post>
) {

  var mapProperties by remember {
    mutableStateOf(
        MapProperties(
            mapType = MapType.NORMAL,
        ))
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
      cameraPositionState = cameraPositionState) {
        // Add markers for each post
        posts.forEach { post ->
          Log.d(
              "MapView",
              "Adding marker at (${post.latitude}, ${post.longitude}) for URI: ${post.uri}")
          AddMapMarker(post) { clickedPost -> selectedPost = clickedPost }
        }

        // Display the ImagePreviewDialog when a post is selected
        selectedPost?.let {
          ImagePreviewDialog(
              uri = it.uri, username = it.username, onDismiss = { selectedPost = null })
        }
      }
}
