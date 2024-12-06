package com.github.lookupgroup27.lookup.ui.image

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.BiasAlignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.rememberAsyncImagePainter
import com.github.lookupgroup27.lookup.R
import com.github.lookupgroup27.lookup.model.location.LocationProviderSingleton
import com.github.lookupgroup27.lookup.model.post.Post
import com.github.lookupgroup27.lookup.ui.image.components.ActionButton
import com.github.lookupgroup27.lookup.ui.navigation.NavigationActions
import com.github.lookupgroup27.lookup.ui.navigation.Screen
import com.github.lookupgroup27.lookup.ui.post.PostsViewModel
import com.github.lookupgroup27.lookup.ui.profile.CollectionViewModel
import com.google.firebase.auth.FirebaseAuth
import java.io.File

@Composable
fun ImageReviewScreen(
    navigationActions: NavigationActions,
    imageFile: File?,
    imageViewModel: ImageViewModel = viewModel(),
    postsViewModel: PostsViewModel = viewModel(),
    collectionViewModel: CollectionViewModel = viewModel(),
    timestamp: Long?
) {
  val context = LocalContext.current
  val locationProvider = LocationProviderSingleton.getInstance(context)
  val currentTimestamp = timestamp ?: System.currentTimeMillis() // Fallback to current time

  val uploadStatus by imageViewModel.uploadStatus.collectAsState()

  // Scroll state
  val scrollState = rememberScrollState()

  Box(
      modifier =
          Modifier.fillMaxSize()
              .background(MaterialTheme.colorScheme.background)
              .testTag("image_review"),
      contentAlignment = Alignment.TopStart) {
        // Background image
        Image(
            painter = painterResource(id = R.drawable.landing_screen_bckgrnd),
            contentDescription = "Background",
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize().blur(10.dp).testTag("background_image"))

        // Save and Discard buttons aligned to the bottom center
        Column(
            modifier =
                Modifier.fillMaxWidth()
                    .align(Alignment.Center)
                    .padding(16.dp)
                    .verticalScroll(scrollState) // Make the column scrollable
                    .testTag("edit_buttons_column"),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally) {

              // Display the image if available
              if (imageFile != null) {
                Image(
                    painter = rememberAsyncImagePainter(imageFile),
                    contentDescription = "Captured Image",
                    modifier =
                        Modifier.fillMaxWidth()
                            .padding(16.dp)
                            .aspectRatio(1f)
                            .testTag("display_image"),
                    contentScale = ContentScale.Crop)
              } else {
                Text(text = "No image available")
              }

              Spacer(modifier = Modifier.height(65.dp))

              // Save Image Button

              ActionButton(
                  text = "Save Image",
                  onClick = { imageFile?.let { imageViewModel.uploadImage(it) } },
                  modifier = Modifier.testTag("confirm_button"))

              ActionButton(
                  text = "Discard Image",
                  onClick = {
                    Toast.makeText(context, "Image discarded", Toast.LENGTH_SHORT).show()
                    navigationActions.navigateTo(Screen.TAKE_IMAGE)
                  },
                  modifier = Modifier.testTag("cancel_button"),
                  color = Color.Red)
            }

        // Display upload status messages and create a post upon successful upload
        when {
          uploadStatus.isLoading -> {
            CircularProgressIndicator(
                modifier =
                    Modifier.align(BiasAlignment(0f, 0.30f))
                        .padding(top = 16.dp)
                        .testTag("loading_indicator"))
          }
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
                      longitude = currentLocation.longitude,
                      timestamp = currentTimestamp)
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
            val errorMessage = uploadStatus.errorMessage
            LaunchedEffect(uploadStatus) {
              Toast.makeText(context, "Error: $errorMessage", Toast.LENGTH_SHORT).show()
            }
          }
        }
      }
}
