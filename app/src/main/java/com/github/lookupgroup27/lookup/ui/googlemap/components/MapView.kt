package com.github.lookupgroup27.lookup.ui.googlemap.components

import android.location.Location
import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.github.lookupgroup27.lookup.model.post.Post
import com.github.lookupgroup27.lookup.model.profile.UserProfile
import com.github.lookupgroup27.lookup.ui.image.ImagePreviewDialog
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*

/**
 * Composable that displays a Google Map with markers for posts.
 *
 * @param padding Padding values for the map
 * @param hasLocationPermission Whether the app has location permission
 * @param location The user's location
 * @param autoCenteringEnabled Whether auto centering is enabled
 * @param posts The list of posts to display on the map
 * @param userEmail The email of the current user
 * @param updateProfile Function to update the user's profile
 * @param profile The user's profile
 * @param updatePost Function to update a post
 * @param postRatings The ratings for each post
 * @param highlightedPost The highlighted post
 */
@Composable
fun MapView(
    padding: PaddingValues,
    hasLocationPermission: Boolean,
    location: Location?,
    autoCenteringEnabled: Boolean,
    posts: List<Post>,
    userEmail: String,
    updateProfile: (UserProfile?, MutableMap<String, Int>?) -> Unit,
    profile: UserProfile?,
    updatePost: (Post, Double, Int, Int, List<String>) -> Unit,
    postRatings: MutableMap<String, List<Boolean>>,
    highlightedPost: SelectedPostMarker?
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

  LaunchedEffect(profile, posts) {
    posts.forEach { post ->
      val starsCount = postRatings[post.uid]?.count { it } ?: 0
      val avg = if (post.usersNumber == 0) 0.0 else starsCount.toDouble() / post.usersNumber
      updatePost(post, avg, starsCount, post.usersNumber, post.ratedBy)
    }
  }

  LaunchedEffect(highlightedPost) {
    highlightedPost?.let { post ->
      // Zoom to highlighted marker position with zoom level 15f
      val latLng = LatLng(post.latitude, post.longitude)
      val cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 15f)
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
          val isHighlighted = highlightedPost?.postId == post.uid
          Log.d(
              "MapView",
              "Adding marker at (${post.latitude}, ${post.longitude}) for URI: ${post.uri}")
          AddMapMarker(
              post,
              onMarkerClick = { clickedPost -> selectedPost = clickedPost },
              isHighlighted = isHighlighted)
        }

        // Display the ImagePreviewDialog when a post is selected
        selectedPost?.let {
          ImagePreviewDialog(
              post = it,
              username = it.username,
              onDismiss = { selectedPost = null },
              starStates = postRatings[it.uid] ?: mutableListOf(false, false, false),
              onRatingChanged = { newRating ->
                val oldPostRatings = postRatings[it.uid] ?: mutableListOf(false, false, false)
                val oldStarCounts = oldPostRatings.count { it }
                // Directly modify the existing starStates list to avoid creating a new list
                postRatings[it.uid] = newRating.toList()
                // Update the stars count based on the new rating
                val starsCount = newRating.count { it }
                // Update user profile with the new rating count
                val updatedRatings = profile?.ratings?.toMutableMap()
                updatedRatings?.set(it.uid, starsCount)
                updateProfile(profile, updatedRatings)

                val isReturningUser = it.ratedBy.contains(userEmail)
                val newStarsCount =
                    if (isReturningUser) it.starsCount - oldStarCounts + starsCount
                    else it.starsCount + starsCount
                val newUsersNumber = if (isReturningUser) it.usersNumber else it.usersNumber + 1
                val newAvg = newStarsCount.toDouble() / newUsersNumber
                val newRatedBy =
                    if (!isReturningUser) {
                      it.ratedBy + userEmail
                    } else {
                      it.ratedBy
                    }
                updatePost(it, newAvg, newStarsCount, newUsersNumber, newRatedBy)
              })
        }
      }
}
