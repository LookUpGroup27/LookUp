package com.github.lookupgroup27.lookup.ui.feed.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.github.lookupgroup27.lookup.R
import com.github.lookupgroup27.lookup.model.post.Post
import kotlinx.coroutines.*
import okhttp3.*
import org.json.JSONObject

/**
 * Composable for displaying an individual post in the feed. Each post includes:
 * - The username of the user who created the post.
 * - The location (address) where the post was created, resolved dynamically from coordinates.
 * - An image loaded from a URI.
 * - A description of the post.
 * - A star rating system where users can rate the post.
 *
 * The post is displayed in a card with rounded corners and appropriate spacing.
 *
 * @param post The post data, including image URI, username, and description.
 * @param starStates The list of star states for the post (filled or not).
 * @param onRatingChanged Callback to handle changes in the rating.
 * @param color The color to use for the username and average rating text.
 * @param textForUsername The text to display for the username.
 * @param showAverage Whether to display the average rating.
 */
@Composable
fun PostItem(
    post: Post,
    starStates: List<Boolean>,
    onRatingChanged: (List<Boolean>) -> Unit,
    color: Color = Color.White,
    textForUsername: String = post.username,
    showAverage: Boolean = true
) {
  val address = remember { mutableStateOf("Loading address...") }
  LaunchedEffect(post.latitude, post.longitude) {
    address.value = getAddressFromLatLngUsingNominatim(post.latitude, post.longitude)
  }

  Card(
      shape = RoundedCornerShape(16.dp),
      colors = CardDefaults.cardColors(containerColor = Color.Black.copy(alpha = 0.5f)),
      modifier =
          Modifier.fillMaxWidth().padding(horizontal = 4.dp).testTag("PostItem_${post.uid}")) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)) {
              // Username
              Text(
                  text = textForUsername,
                  style =
                      MaterialTheme.typography.titleSmall.copy(
                          fontWeight = FontWeight.Bold, color = color),
                  modifier = Modifier.testTag("UsernameTag_${post.username}"))

              // Address
              Text(
                  text = address.value,
                  style = MaterialTheme.typography.bodySmall.copy(color = Color.LightGray),
                  maxLines = 1,
                  overflow = TextOverflow.Ellipsis,
                  modifier = Modifier.testTag("AddressTag_${post.uid}"))

              // Image
              Image(
                  painter = rememberAsyncImagePainter(post.uri),
                  contentDescription = "Post Image for ${post.username}",
                  modifier = Modifier.fillMaxWidth().height(250.dp).testTag("ImageTag_${post.uid}"),
                  contentScale = ContentScale.Crop)

              // Description
              if (post.description.isNotEmpty()) {
                Text(
                    text = post.description,
                    style = MaterialTheme.typography.bodyMedium.copy(color = Color.White),
                    modifier = Modifier.testTag("DescriptionTag_${post.uid}"))
              }

              // Rating Row
              Row(
                  verticalAlignment = androidx.compose.ui.Alignment.CenterVertically,
                  horizontalArrangement = Arrangement.spacedBy(4.dp),
                  modifier = Modifier.fillMaxWidth()) {
                    starStates.forEachIndexed { index, isFilled ->
                      IconButton(
                          onClick = {
                            val newRating = starStates.mapIndexed { i, _ -> i <= index }
                            onRatingChanged(newRating)
                          },
                          modifier =
                              Modifier.size(36.dp).testTag("Star_${index + 1}_${post.uid}")) {
                            Image(
                                painter =
                                    painterResource(
                                        id =
                                            if (isFilled) R.drawable.full_star2
                                            else R.drawable.empty_star2),
                                contentDescription = "Star")
                          }
                    }
                    Spacer(modifier = Modifier.weight(1f))
                    if (showAverage) {
                      Text(
                          text = "Avg: ${"%.1f".format(post.averageStars)}",
                          style =
                              MaterialTheme.typography.bodyMedium.copy(
                                  fontWeight = FontWeight.Medium, color = color),
                          modifier = Modifier.testTag("AverageRatingTag_${post.uid}"))
                    }
                  }
            }
      }
}

/**
 * Fetches the address corresponding to latitude and longitude using the Nominatim API.
 *
 * @param lat Latitude of the location.
 * @param lon Longitude of the location.
 * @param client Optional OkHttpClient instance for making the API call.
 * @return The address as a string, or an error message if the address cannot be fetched.
 */
suspend fun getAddressFromLatLngUsingNominatim(
    lat: Double,
    lon: Double,
    client: OkHttpClient = OkHttpClient()
): String {
  if (lat < -90 || lat > 90) {
    return "Error fetching address: Invalid latitude: $lat"
  }
  if (lon < -180 || lon > 180) {
    return "Error fetching address: Invalid longitude: $lon"
  }

  val url = "https://nominatim.openstreetmap.org/reverse?lat=$lat&lon=$lon&format=json"

  return withContext(Dispatchers.IO) {
    try {
      val request = Request.Builder().url(url).addHeader("User-Agent", "lookup/1.0").build()
      val response = client.newCall(request).execute()
      if (response.isSuccessful) {
        val responseBody = response.body?.string()
        if (!responseBody.isNullOrEmpty()) {
          val json = JSONObject(responseBody)
          json.optString("display_name", "Address not found")
        } else {
          "Error: Empty response body"
        }
      } else {
        "Error fetching address: HTTP ${response.code} - ${response.message}"
      }
    } catch (e: Exception) {
      e.printStackTrace()
      "Error fetching address: ${e.message ?: "Unknown error"}"
    }
  }
}
