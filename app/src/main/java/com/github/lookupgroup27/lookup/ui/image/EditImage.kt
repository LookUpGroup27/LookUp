package com.github.lookupgroup27.lookup.ui.image

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.BiasAlignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.github.lookupgroup27.lookup.R
import com.github.lookupgroup27.lookup.ui.image.components.ActionButton
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
    postUri: String,
    postAverageStar: Double,
    postRatedByNb: Int,
    postUid: String,
    postDescription: String,
    editImageViewModel: EditImageViewModel,
    collectionViewModel: CollectionViewModel,
    postsViewModel: PostsViewModel,
    navigationActions: NavigationActions
) {
  val editImageState by editImageViewModel.editImageState.collectAsState()
  val context = LocalContext.current

  var description by remember { mutableStateOf(postDescription) }
  var isEditing by remember { mutableStateOf(false) }

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
            painter = rememberAsyncImagePainter(postUri),
            contentDescription = "Edit Image",
            modifier =
                Modifier.fillMaxWidth()
                    .align(BiasAlignment(0f, -0.5f))
                    .padding(16.dp)
                    .testTag("display_image"))

        // Description box
        Column(
            modifier =
                Modifier.fillMaxWidth()
                    .padding(16.dp)
                    .align(BiasAlignment(0f, 0.55f)) // Consistent alignment for both states
            ) {
              if (isEditing) {
                OutlinedTextField(
                    textStyle = TextStyle(color = Color.White, fontWeight = FontWeight.Bold),
                    placeholder = {
                      Text(
                          "Add the description",
                          style = TextStyle(color = Color.White, fontStyle = FontStyle.Italic))
                    },
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Description", color = Color.White) },
                    modifier =
                        Modifier.fillMaxWidth()
                            .background(Color.Gray.copy(alpha = 0.5f))
                            .padding(8.dp)
                            .testTag("edit_description_field"),
                    keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Done),
                    keyboardActions =
                        KeyboardActions(
                            onDone = {
                              postsViewModel.updateDescription(postUid, description)
                              isEditing = false
                            }))
              } else {
                Text(
                    text = if (description.isEmpty()) "Add the description" else description,
                    color = Color.White,
                    style =
                        if (description.isEmpty())
                            TextStyle(color = Color.White, fontStyle = FontStyle.Italic)
                        else TextStyle(fontWeight = FontWeight.Bold),
                    modifier =
                        Modifier.fillMaxWidth()
                            .height(55.dp)
                            .background(Color.Gray.copy(alpha = 0.5f))
                            .padding(8.dp)
                            .clickable { isEditing = true }
                            .testTag("description_text"))
              }
            }

        // Edit buttons aligned to the bottom center
        Column(
            modifier =
                Modifier.fillMaxWidth()
                    .align(BiasAlignment(0f, 0.85f))
                    .padding(16.dp)
                    .testTag("edit_buttons_column"),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally) {
              Row {
                Image(
                    painter = painterResource(id = R.drawable.full_star),
                    contentDescription = "Star Rating",
                    modifier = Modifier.testTag("star_collection").size(28.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Average Rating: $postAverageStar",
                    color = Color.White,
                    modifier = Modifier.testTag("average_rating_collection"))
                Spacer(modifier = Modifier.width(8.dp))
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = "User Icon",
                    tint = Color.White,
                    modifier = Modifier.testTag("user_icon_collection"))
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Rated by : $postRatedByNb users",
                    color = Color.White,
                    modifier = Modifier.testTag("rated_by_collection"))
              }

              ActionButton(
                  text = "Delete Image",
                  onClick = {
                    editImageViewModel.deleteImage(postUri)
                    navigationActions.navigateTo(Screen.COLLECTION)
                  },
                  color = Color.Red,
                  modifier = Modifier.testTag("delete_button"))
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
          is EditImageState.Error -> {
            val errorMessage = (editImageState as EditImageState.Error).message
            LaunchedEffect(editImageState) {
              Toast.makeText(context, "Error: $errorMessage", Toast.LENGTH_SHORT).show()
            }
          }
          is EditImageState.Deleted -> {
            collectionViewModel.updateImages()
            postsViewModel.deletePost(postUid)
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
