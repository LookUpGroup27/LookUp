package com.github.lookupgroup27.lookup.ui.image

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
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
import androidx.constraintlayout.compose.ConstraintLayout
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
  var showSaveDialog by remember { mutableStateOf(false) }
  var originalDescription by remember { mutableStateOf(description) }

  // Enable vertical scrolling
  val scrollState = rememberScrollState()

  Box(modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {
    // Background image
    Image(
        painter = painterResource(id = R.drawable.landing_screen_bckgrnd),
        contentDescription = "Background",
        contentScale = ContentScale.Crop,
        modifier = Modifier.fillMaxSize().blur(10.dp).testTag("background_image"))

    // Layout using ConstraintLayout for consistency
    ConstraintLayout(modifier = Modifier.fillMaxSize().verticalScroll(scrollState)) {
      val (backButton, image, descriptionBox, buttonsColumn, starRow) = createRefs()

      // Back button
      IconButton(
          onClick = { navigationActions.navigateTo(Screen.COLLECTION) },
          modifier =
              Modifier.constrainAs(backButton) {
                    top.linkTo(parent.top, margin = 16.dp)
                    start.linkTo(parent.start, margin = 16.dp)
                  }
                  .testTag("go_back_button_collection")) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Back",
                tint = Color.White,
                modifier = Modifier.testTag("back_icon"))
          }

      Image(
          painter = rememberAsyncImagePainter(postUri),
          contentDescription = "Edit Image",
          contentScale = ContentScale.Crop,
          modifier =
              Modifier.fillMaxWidth(0.85f) // Smaller width in landscape
                  .aspectRatio(3f / 4f) // Adjust height proportionally
                  .padding(16.dp)
                  .constrainAs(image) {
                    top.linkTo(backButton.bottom, margin = 16.dp)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                  }
                  .testTag("display_image"))

      // Description box
      Column(
          modifier =
              Modifier.fillMaxWidth().padding(16.dp).constrainAs(descriptionBox) {
                top.linkTo(image.bottom, margin = 16.dp)
                start.linkTo(parent.start)
                end.linkTo(parent.end)
              }) {
            // Title for the Description Box
            Text(
                text = "Description",
                style =
                    TextStyle(
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = MaterialTheme.typography.bodySmall.fontSize))
            if (isEditing) {
              OutlinedTextField(
                  textStyle = TextStyle(color = Color.White),
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
                          .clip(RoundedCornerShape(12.dp))
                          .background(Color.Gray.copy(alpha = 0.5f))
                          .padding(8.dp)
                          .testTag("edit_description_field"),
                  keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Done),
                  keyboardActions = KeyboardActions(onDone = { showSaveDialog = true }))

              if (showSaveDialog) {
                AlertDialog(
                    onDismissRequest = { showSaveDialog = false },
                    title = { Text("Save Changes?") },
                    text = { Text("Do you want to save the new description?") },
                    confirmButton = {
                      TextButton(
                          onClick = {
                            originalDescription = description
                            postsViewModel.updateDescription(postUid, description)
                            isEditing = false
                            showSaveDialog = false
                          }) {
                            Text("Save")
                          }
                    },
                    dismissButton = {
                      TextButton(
                          onClick = {
                            description = originalDescription
                            isEditing = false
                            showSaveDialog = false
                          }) {
                            Text("Discard")
                          }
                    })
              }
            } else {
              Text(
                  text = if (description.isEmpty()) "Add the description" else description,
                  color = Color.White,
                  style =
                      if (description.isEmpty())
                          TextStyle(color = Color.White, fontStyle = FontStyle.Italic)
                      else TextStyle(color = Color.White),
                  modifier =
                      Modifier.fillMaxWidth()
                          .clip(RoundedCornerShape(12.dp))
                          .height(55.dp)
                          .background(Color.Gray.copy(alpha = 0.5f))
                          .padding(8.dp)
                          .clickable { isEditing = true }
                          .testTag("description_text"))
            }
          }

      // Star rating and user info
      Row(
          modifier =
              Modifier.fillMaxWidth().padding(horizontal = 16.dp).constrainAs(starRow) {
                top.linkTo(descriptionBox.bottom, margin = 16.dp)
                start.linkTo(parent.start)
                end.linkTo(parent.end)
              },
          horizontalArrangement = Arrangement.SpaceEvenly,
          verticalAlignment = Alignment.CenterVertically) {
            Image(
                painter = painterResource(id = R.drawable.full_star),
                contentDescription = "Star Rating",
                modifier = Modifier.testTag("star_collection").size(28.dp))
            Text(
                text = "Average Rating: $postAverageStar",
                color = Color.White,
                modifier = Modifier.testTag("average_rating_collection"))
            Icon(
                imageVector = Icons.Default.Person,
                contentDescription = "User Icon",
                tint = Color.White,
                modifier = Modifier.testTag("user_icon_collection"))
            Text(
                text = "Rated by: $postRatedByNb users",
                color = Color.White,
                modifier = Modifier.testTag("rated_by_collection"))
          }

      // Edit buttons
      Column(
          modifier =
              Modifier.fillMaxWidth().padding(16.dp).constrainAs(buttonsColumn) {
                bottom.linkTo(parent.bottom, margin = 16.dp)
                start.linkTo(parent.start)
                end.linkTo(parent.end)
              },
          verticalArrangement = Arrangement.spacedBy(12.dp),
          horizontalAlignment = Alignment.CenterHorizontally) {
            ActionButton(
                text = "Delete Image",
                onClick = {
                  editImageViewModel.deleteImage(postUri)
                  navigationActions.navigateTo(Screen.COLLECTION)
                },
                color = Color.Red,
                modifier = Modifier.testTag("delete_button"))
          }
    }

    // Loading and error states
    when (editImageState) {
      is EditImageState.Loading -> {
        CircularProgressIndicator(
            modifier =
                Modifier.align(Alignment.Center).padding(top = 16.dp).testTag("loading_indicator"))
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
      else -> Unit
    }
  }
}
