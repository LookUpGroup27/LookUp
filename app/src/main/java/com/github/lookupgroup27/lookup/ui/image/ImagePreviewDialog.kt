package com.github.lookupgroup27.lookup.ui.image

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.*
import androidx.compose.material3.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.github.lookupgroup27.lookup.model.post.Post
import com.github.lookupgroup27.lookup.ui.feed.components.PostItem

/**
 * Display a dialog with an image preview.
 *
 * @param post The post to display in the dialog.
 * @param username The username of the user who posted the image.
 * @param onDismiss The callback to be invoked when the dialog is dismissed.
 * @param starStates The list of star states for the post.
 * @param onRatingChanged The callback to be invoked when the rating is changed.
 */

// Display a dialog with an image preview
@Composable
fun ImagePreviewDialog(
    post: Post?,
    username: String?,
    onDismiss: () -> Unit,
    starStates: List<Boolean>,
    onRatingChanged: (List<Boolean>) -> Unit
) {

  if (post != null) {
    Dialog(
        onDismissRequest =
            onDismiss) { // Dismiss the dialog when the user clicks outside the dialog
          Surface(
              modifier = Modifier.padding(16.dp).testTag("imagePreviewDialog"),
              color = Color.Gray, // Apply custom color here
              shape = MaterialTheme.shapes.medium // Optional: Add a shape for rounded corners
              ) {
                Column(
                    modifier = Modifier
                        .padding(16.dp)
                        .verticalScroll(rememberScrollState()),
                    horizontalAlignment = Alignment.CenterHorizontally) {
                      PostItem(
                          post = post,
                          starStates = starStates,
                          onRatingChanged = onRatingChanged,
                          color = Color.White,
                          textForUsername = "Posted by: $username",
                          showAverage = false)
                      Spacer(modifier = Modifier.height(16.dp))
                      Button(onClick = onDismiss) { Text(text = "Close") }
                    }
              }
        }
  }
}
