package com.github.lookupgroup27.lookup.ui.image

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.rememberAsyncImagePainter
import com.github.lookupgroup27.lookup.model.image.FirebaseImageRepository
import com.github.lookupgroup27.lookup.model.image.ImageViewModel
import com.github.lookupgroup27.lookup.ui.navigation.NavigationActions
import com.github.lookupgroup27.lookup.ui.navigation.Screen
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.storage.FirebaseStorage
import java.io.File

@Composable
fun ImageReviewScreen(navigationActions: NavigationActions, imageUri: File?) {
  val context = LocalContext.current

  // Instantiate the repository
  val repository =
      FirebaseImageRepository(FirebaseStorage.getInstance(), FirebaseAuth.getInstance())

  // Use the companion object factory to create the ViewModel
  val imageViewModel: ImageViewModel =
      viewModel(factory = ImageViewModel.provideFactory(repository))

  val uploadStatus by imageViewModel.uploadStatus.collectAsState()

  Column(
      modifier = Modifier.fillMaxSize().padding(16.dp).testTag("image_review"),
      horizontalAlignment = Alignment.CenterHorizontally,
      verticalArrangement = Arrangement.Center) {
        // Display the image if available
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

        // Save Image Button
        Button(
            onClick = { imageUri?.let { imageViewModel.uploadImage(it) } },
            modifier = Modifier.fillMaxWidth().testTag("confirm_button")) {
              Text(text = "Save Image")
            }

        Spacer(modifier = Modifier.height(16.dp))

        // Discard Image Button
        Button(
            onClick = {
              Toast.makeText(context, "Image discarded", Toast.LENGTH_SHORT).show()
              navigationActions.navigateTo(Screen.TAKE_IMAGE) // Navigate back without saving
            },
            modifier = Modifier.fillMaxWidth().testTag("cancel_button")) {
              Text(text = "Discard Image")
            }

        // Display upload status messages
        when {
          uploadStatus.isLoading ->
              Text(text = "Uploading...", modifier = Modifier.padding(top = 16.dp))
          uploadStatus.downloadUrl != null -> {
            Toast.makeText(context, "Image uploaded successfully!", Toast.LENGTH_SHORT).show()
            navigationActions.navigateTo(Screen.TAKE_IMAGE) // Navigate after successful upload
            imageViewModel.resetUploadStatus() // Reset status after handling
          }
          uploadStatus.errorMessage != null -> {
            Text(
                text = "Error: ${uploadStatus.errorMessage}",
                modifier = Modifier.padding(top = 16.dp))
          }
        }
      }
}
