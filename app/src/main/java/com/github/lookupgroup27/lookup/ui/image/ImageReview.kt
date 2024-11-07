package com.github.lookupgroup27.lookup.ui.image

import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.github.lookupgroup27.lookup.ui.navigation.NavigationActions
import com.github.lookupgroup27.lookup.ui.navigation.Screen

@Composable
fun ImageReviewScreen(navigationActions: NavigationActions, imageUri: Uri?) {
  val context = LocalContext.current

  Column(
      modifier = Modifier.fillMaxSize().padding(16.dp).testTag("image_review"),
      horizontalAlignment = Alignment.CenterHorizontally,
      verticalArrangement = Arrangement.Center) {
        if (imageUri != null) {
          Image(
              painter = rememberAsyncImagePainter(imageUri),
              contentDescription = "Captured Image",
              modifier = Modifier.fillMaxWidth().aspectRatio(1f),
              contentScale = ContentScale.Crop)
        } else {
          Text(text = "No image available")
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Save button
        Button(
            onClick = {
              Toast.makeText(context, "Image saved", Toast.LENGTH_SHORT).show()
              // TODO: navigationActions.navigateTo(Screen.GoogleMAp)
            },
            modifier = Modifier.fillMaxWidth().testTag("confirm_button")) {
              Text(text = "Save Image")
            }

        Spacer(modifier = Modifier.height(16.dp))

        // Discard button
        Button(
            onClick = {
              // Logic to discard the image
              Toast.makeText(context, "Image discarded", Toast.LENGTH_SHORT).show()
              navigationActions.navigateTo(Screen.TAKE_IMAGE) // Navigate back without saving
            },
            modifier = Modifier.fillMaxWidth().testTag("cancel_button")) {
              Text(text = "Discard Image")
            }
      }
}