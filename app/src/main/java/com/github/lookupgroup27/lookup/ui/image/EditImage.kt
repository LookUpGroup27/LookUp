package com.github.lookupgroup27.lookup.ui.image

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
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
import coil.compose.rememberAsyncImagePainter
import com.github.lookupgroup27.lookup.R
import com.github.lookupgroup27.lookup.ui.navigation.NavigationActions
import com.github.lookupgroup27.lookup.ui.navigation.Screen
import com.github.lookupgroup27.lookup.ui.post.PostsViewModel
import com.github.lookupgroup27.lookup.ui.profile.CollectionViewModel

/**
 * Composable function to display the Edit Image Screen.
 *
 * This screen allows users to edit an image by cropping, resizing, or deleting it. It also provides
 * navigation options to go back to the collection screen. The UI dynamically updates based on the
 * state of the edit operation.
 *
 * @param imageUrl The URL of the image to be displayed and edited.
 * @param editImageViewModel The [EditImageViewModel] handling the logic for image editing.
 * @param collectionViewModel The [CollectionViewModel] managing the state of the user's image
 *   collection.
 * @param postsViewModel The [PostsViewModel] managing post data associated with the image.
 * @param navigationActions An instance of [NavigationActions] for handling navigation between
 *   screens.
 */
@Composable
fun EditImageScreen(
    imageUrl: String,
    editImageViewModel: EditImageViewModel,
    collectionViewModel: CollectionViewModel,
    postsViewModel: PostsViewModel,
    navigationActions: NavigationActions
) {
  val editImageState by editImageViewModel.editImageState.collectAsState()
  val context = LocalContext.current

  Box(
      modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background),
      contentAlignment = Alignment.TopStart) {
        // Background image
        Image(
            painter = painterResource(id = R.drawable.landing_screen_bckgrnd),
            contentDescription = "Background",
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize().blur(10.dp).testTag("background_image"))

        // Back button aligned to top start
        IconButton(
            onClick = { navigationActions.navigateTo(Screen.COLLECTION) },
            modifier =
                Modifier.align(Alignment.TopStart)
                    .padding(16.dp)
                    .testTag("go_back_button_collection")) {
              Icon(
                  imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                  contentDescription = "Back",
                  tint = Color.White,
                  modifier = Modifier.testTag("back_icon"))
            }

        // Display the image
        Image(
            painter = rememberAsyncImagePainter(imageUrl),
            contentDescription = "Edit Image",
            modifier =
                Modifier.fillMaxWidth()
                    .align(BiasAlignment(0f, -0.5f)) // Center the image
                    .padding(16.dp)
                    .testTag("display_image"))

        // Edit buttons aligned to the bottom center
        Column(
            modifier =
                Modifier.fillMaxWidth()
                    .align(BiasAlignment(0f, 0.85f)) // Align to the bottom center of the Box
                    .padding(16.dp)
                    .testTag("edit_buttons_column"),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally) {
              Button(
                  onClick = { /*editImageViewModel.cropImage(imageUrl)*/},
                  modifier = Modifier.testTag("crop_button")) {
                    Text("Crop Image")
                  }
              Button(
                  onClick = { /*editImageViewModel.resizeImage(imageUrl)*/},
                  modifier = Modifier.testTag("resize_button")) {
                    Text("Resize Image")
                  }
              Button(
                  onClick = {
                    editImageViewModel.deleteImage(imageUrl)
                    collectionViewModel.updateImages()
                    postsViewModel.deletePost(imageUrl)
                    navigationActions.navigateTo(Screen.COLLECTION)
                  },
                  colors = ButtonDefaults.buttonColors(containerColor = Color.Red),
                  modifier = Modifier.testTag("delete_button")) {
                    Text("Delete Image")
                  }
            }

        // Loading indicator and state handling
        when (editImageState) {
          is EditImageState.Loading -> {
            CircularProgressIndicator(
                modifier =
                    Modifier.align(Alignment.Center)
                        .padding(top = 16.dp)
                        .testTag("loading_indicator"))
          }
          is EditImageState.Success -> {
            val newImageUrl = (editImageState as EditImageState.Success).imageUrl
            LaunchedEffect(editImageState) {
              Toast.makeText(context, "Success! Image updated.", Toast.LENGTH_SHORT).show()
            }
          }
          is EditImageState.Error -> {
            val errorMessage = (editImageState as EditImageState.Error).message
            LaunchedEffect(editImageState) {
              Toast.makeText(context, "Error: $errorMessage", Toast.LENGTH_SHORT).show()
            }
          }
          is EditImageState.Deleted -> {
            LaunchedEffect(editImageState) {
              Toast.makeText(context, "Image deleted successfully.", Toast.LENGTH_SHORT).show()
              editImageViewModel.resetState()
            }
          }
          else -> {
            // Idle state or no-op
          }
        }
      }
}
