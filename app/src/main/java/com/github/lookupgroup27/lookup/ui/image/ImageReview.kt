package com.github.lookupgroup27.lookup.ui.image

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
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
import com.github.lookupgroup27.lookup.model.location.LocationProviderSingleton
import com.github.lookupgroup27.lookup.model.post.Post
import com.github.lookupgroup27.lookup.ui.navigation.NavigationActions
import com.github.lookupgroup27.lookup.ui.navigation.Screen
import com.github.lookupgroup27.lookup.ui.post.PostsViewModel
import com.github.lookupgroup27.lookup.ui.profile.CollectionViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.storage.FirebaseStorage
import java.io.File

@Composable
fun ImageReviewScreen(
    navigationActions: NavigationActions,
    imageFile: File?,
    postsViewModel: PostsViewModel = viewModel(),
    collectionViewModel: CollectionViewModel = viewModel()
) {
  val context = LocalContext.current
  val locationProvider = LocationProviderSingleton.getInstance(context)

  // Instantiate the repository
  val repository =
      FirebaseImageRepository(FirebaseStorage.getInstance(), FirebaseAuth.getInstance())

  // Use the companion object factory to create the ViewModel
  val imageViewModel: ImageViewModel =
      viewModel(factory = ImageViewModel.provideFactory(repository))

  val uploadStatus by imageViewModel.uploadStatus.collectAsState()

  // Scroll state
  val scrollState = rememberScrollState()

  Column(
      modifier =
          Modifier.fillMaxSize()
              .padding(16.dp)
              .verticalScroll(scrollState) // Make the column scrollable
              .testTag("image_review"),
      horizontalAlignment = Alignment.CenterHorizontally,
      verticalArrangement = Arrangement.Top // Adjust the arrangement to avoid centering all items
      ) {
        // Display the image if available
        if (imageFile != null) {
          Image(
              painter = rememberAsyncImagePainter(imageFile),
              contentDescription = "Captured Image",
              modifier = Modifier.fillMaxWidth().aspectRatio(1f),
              contentScale = ContentScale.Crop)
        } else {
          Text(text = "No image available")
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Save Image Button
        Button(
            onClick = { imageFile?.let { imageViewModel.uploadImage(it) } },
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

        // Display upload status messages and create a post upon successful upload
        when {
          uploadStatus.isLoading ->
              Text(text = "Uploading...", modifier = Modifier.padding(top = 16.dp))
          uploadStatus.downloadUrl != null -> {
            val downloadUrl = uploadStatus.downloadUrl // Get the download URL from upload status
            val currentLocation = locationProvider.currentLocation.value
            if (downloadUrl != null && currentLocation != null) {
              // Create a new post with the image download URL and current location
              val newPost =
                  Post(
                      uid = postsViewModel.generateNewUid(),
                      uri = downloadUrl,
                      username = FirebaseAuth.getInstance().currentUser?.displayName ?: "Anonymous",
                      latitude = currentLocation.latitude,
                      longitude = currentLocation.longitude)
              // Add the post to PostsViewModel
              postsViewModel.addPost(newPost)
              collectionViewModel.updateImages()
              Toast.makeText(context, "Image saved", Toast.LENGTH_SHORT).show()
              navigationActions.navigateTo(Screen.GOOGLE_MAP)
            } else {
              Toast.makeText(
                      context, "Failed to get current location or download URL", Toast.LENGTH_SHORT)
                  .show()
            }
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
