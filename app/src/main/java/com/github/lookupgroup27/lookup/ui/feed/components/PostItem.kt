package com.github.lookupgroup27.lookup.ui.feed.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.github.lookupgroup27.lookup.R
import com.github.lookupgroup27.lookup.model.post.Post
import kotlinx.coroutines.*
import okhttp3.*
import org.json.JSONObject

/**
 * Composable that displays an individual post, showing the user's image and username. The image is
 * loaded asynchronously using the post's URI.
 *
 * @param post The post data, including image URI and username
 * @param starStates The list of star states for the post
 * @param onRatingChanged The callback to be invoked when the rating is changed
 * @param color The color to use for the username and average rating
 * @param textForUsername The text to display for the username
 * @param showAverage Whether to display the average rating
 */
@Composable
fun PostItem(
    post: Post,
    starStates: List<Boolean>,
    onRatingChanged: (List<Boolean>) -> Unit,
    color: Color = Color.Black,
    textForUsername: String = post.username,
    showAverage: Boolean = true
) {

  val address = remember { mutableStateOf("Loading address...") }
  LaunchedEffect(post.latitude, post.longitude) {
    address.value = getAddressFromLatLngUsingNominatim(post.latitude, post.longitude)
  }

  Column(
      modifier =
          Modifier.fillMaxWidth()
              .padding(8.dp)
              .testTag("PostItem_${post.uid}") // Unique tag per post item
      ) {
        // Display the username at the top of each post item
        Text(
            text = textForUsername,
            style =
                MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = FontWeight.Bold, color = color),
            modifier =
                Modifier.padding(start = 4.dp)
                    .testTag("UsernameTag_${post.username}") // Tagging username for testing
            )
        // Display the address at the top of each post item
        Text(
            text = address.value,
            style = MaterialTheme.typography.bodySmall.copy(color = Color.Blue),
            modifier =
                Modifier.padding(start = 4.dp)
                    .testTag("AddressTag_${post.uid}"), // Tagging address for testing
        )

        // Display image using the dynamically fetched URI
        Image(
            painter = rememberAsyncImagePainter(post.uri), // Coil loads image from URI
            contentDescription = "Post Image for ${post.username}",
            modifier =
                Modifier.fillMaxWidth()
                    .height(300.dp)
                    .testTag("ImageTag_${post.uid}"), // Tagging image for testing
            contentScale = ContentScale.Crop)

        Text(
            text = post.description,
            style = MaterialTheme.typography.bodyMedium,
            modifier =
                Modifier.padding(start = 4.dp)
                    .testTag("DescriptionTag_${post.uid}") // Tagging description for testing
            )
        // Star rating row
        Row {
          // Loop through each star
          starStates.forEachIndexed { index, isFilled ->
            IconButton(
                onClick = {
                  // Toggle stars up to the clicked index
                  val newRating =
                      starStates.mapIndexed { i, _ -> if (isFilled) i < index else i <= index }
                  onRatingChanged(newRating)
                },
                modifier = Modifier.size(36.dp).testTag("Star_${index + 1}_${post.uid}")) {
                  Image(
                      painter =
                          painterResource(
                              id = if (isFilled) R.drawable.full_star else R.drawable.empty_star),
                      contentDescription = "Star")
                }
          }
          if (showAverage) {
            // Display the average rating at the end of the row
            Text(
                text = "Average rating: ${"%.1f".format(post.averageStars)}",
                modifier =
                    Modifier.fillMaxWidth()
                        .padding(start = 4.dp)
                        .testTag("AverageRatingTag_${post.uid}"),
                textAlign = TextAlign.End,
                style =
                    MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = FontWeight.Bold, color = color))
          }
        }
      }
}

suspend fun getAddressFromLatLngUsingNominatim(
    lat: Double,
    lon: Double,
    client: OkHttpClient = OkHttpClient()
): String {

  // Validate coordinates first
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
