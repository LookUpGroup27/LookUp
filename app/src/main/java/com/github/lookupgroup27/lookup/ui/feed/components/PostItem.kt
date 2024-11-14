package com.github.lookupgroup27.lookup.ui.feed.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.github.lookupgroup27.lookup.model.post.Post

/**
 * Composable that displays an individual post, showing the user's image and username. The image is
 * loaded asynchronously using the post's URI.
 *
 * @param post The post data, including image URI and username
 */
@Composable
fun PostItem(post: Post) {
  Column(
      modifier =
          Modifier.fillMaxWidth()
              .padding(8.dp)
              .testTag("PostItem_${post.uid}") // Unique tag per post item
      ) {
        // Display the username at the top of each post item
        Text(
            text = post.username,
            style =
                MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = FontWeight.Bold, color = Color.Black),
            modifier =
                Modifier.padding(start = 4.dp)
                    .testTag("UsernameTag_${post.username}") // Tagging username for testing
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
      }
}
