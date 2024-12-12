package com.github.lookupgroup27.lookup.ui.googlemap.components

import androidx.compose.runtime.Composable
import com.github.lookupgroup27.lookup.model.post.Post
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState

/**
 * Add a marker to the map
 *
 * @param post The post to add a marker for
 * @param onMarkerClick The callback to be called when the marker is clicked
 */
@Composable
fun AddMapMarker(post: Post, onMarkerClick: (Post) -> Unit, isHighlighted: Boolean = false) {
  val latLng = LatLng(post.latitude, post.longitude)
  Marker(
      state = MarkerState(position = latLng),
      title = post.username,
      icon =
          BitmapDescriptorFactory.defaultMarker(
              if (isHighlighted) BitmapDescriptorFactory.HUE_RED
              else BitmapDescriptorFactory.HUE_AZURE),
      onClick = {
        onMarkerClick(post)
        true
      })
}
