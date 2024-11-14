package com.github.lookupgroup27.lookup.ui.image

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import coil.compose.rememberAsyncImagePainter
import com.github.lookupgroup27.lookup.ui.theme.*

/**
 * Display a dialog with an image preview.
 *
 * @param uri The URI of the image to display.
 * @param username The username of the user who posted the image.
 * @param onDismiss The callback to be invoked when the dialog is dismissed.
 */

// Display a dialog with an image preview
@Composable
fun ImagePreviewDialog(uri: String?, username: String?, onDismiss: () -> Unit) {
  if (uri != null) {
    Dialog(
        onDismissRequest =
            onDismiss) { // Dismiss the dialog when the user clicks outside the dialog
          Column(
              modifier = Modifier.padding(16.dp).background(DarkBlue).testTag("imagePreviewDialog"),
              horizontalAlignment = Alignment.CenterHorizontally) {
                if (username != null) {
                  Text(
                      text = "Posted by: $username",
                      modifier = Modifier.padding(bottom = 8.dp),
                      color = Color.White)
                }
                Image(
                    // Load the image from the provided URI
                    painter = rememberAsyncImagePainter(uri),
                    contentDescription = "Image Preview",
                    modifier = Modifier.fillMaxWidth().aspectRatio(1f),
                    contentScale = ContentScale.Crop)
                Spacer(modifier = Modifier.height(16.dp))
                Button(onClick = onDismiss) { Text(text = "Close") }
              }
        }
  }
}
