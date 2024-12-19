package com.github.lookupgroup27.lookup.ui.image

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.github.lookupgroup27.lookup.model.post.Post
import com.github.lookupgroup27.lookup.ui.feed.components.PostItem

/**
 * Display a dialog with an image preview of a given post.
 *
 * This dialog shows a larger preview of a post, including the username and rating controls. It uses
 * [PostItem] to display the post details but doesn't allow navigating to the full screen image.
 * Instead, there's a Close button to dismiss the dialog.
 *
 * @param post The [Post] to display in the dialog. If null, the dialog is not shown.
 * @param username The username of the user who posted the image.
 * @param onDismiss The callback invoked when the dialog is dismissed.
 * @param starStates The current rating states (filled or empty) for each star.
 * @param onRatingChanged The callback invoked when the user changes the rating.
 */
@Composable
fun ImagePreviewDialog(
    post: Post?,
    username: String?,
    onDismiss: () -> Unit,
    starStates: List<Boolean>,
    onRatingChanged: (List<Boolean>) -> Unit
) {
  if (post != null) {
    Dialog(onDismissRequest = onDismiss) {
      Surface(
          modifier = Modifier.padding(16.dp).testTag("imagePreviewDialog"),
          color = Color.Gray,
          shape = MaterialTheme.shapes.medium) {
            Column(
                modifier = Modifier.padding(16.dp).verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally) {
                  PostItem(
                      post = post,
                      starStates = starStates,
                      onRatingChanged = onRatingChanged,
                      onImageClick = { _, _, _ -> }, // No-op for dialog preview
                      color = Color.White,
                      textForUsername = "Posted by: $username",
                      showAverage = false,
                      showAddress = false)
                  Spacer(modifier = Modifier.height(16.dp))
                  Button(onClick = onDismiss) { Text(text = "Close") }
                }
          }
    }
  }
}
