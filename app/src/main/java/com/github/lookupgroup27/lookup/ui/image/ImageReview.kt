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
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
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
import com.github.lookupgroup27.lookup.ui.theme.DarkPurple
import com.google.firebase.auth.FirebaseAuth
import java.io.File

@OptIn(ExperimentalMaterial3Api::class)
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

  val scrollState = rememberScrollState()

  var description by remember { mutableStateOf("") }
  var isEditing by remember { mutableStateOf(false) }

  val focusRequester = remember { FocusRequester() }
  val keyboardController = LocalSoftwareKeyboardController.current

  Box(
      modifier =
          Modifier.fillMaxSize()
              .background(MaterialTheme.colorScheme.background)
              .testTag("image_review"),
      contentAlignment = Alignment.TopStart) {
        Image(
            painter = painterResource(id = R.drawable.landing_screen_bckgrnd),
            contentDescription = "Background",
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize().blur(10.dp).testTag("background_image"))

        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally) {
              Text(
                  text = "Post Your Picture",
                  style =
                      MaterialTheme.typography.headlineMedium.copy(
                          fontWeight = FontWeight.Bold, color = Color.White),
                  modifier =
                      Modifier.fillMaxWidth()
                          .padding(16.dp)
                          .align(Alignment.CenterHorizontally), // Center the title
                  textAlign = TextAlign.Center)

              Spacer(modifier = Modifier.height(10.dp))

              Column(
                  modifier = Modifier.fillMaxWidth().padding(16.dp).verticalScroll(scrollState),
                  horizontalAlignment = Alignment.CenterHorizontally) {
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
                      Text(text = "No image available", color = Color.White)
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = "Description",
                        modifier = Modifier.padding(horizontal = 8.dp).align(Alignment.Start),
                        style =
                            TextStyle(
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = MaterialTheme.typography.bodySmall.fontSize))

                    if (isEditing) {
                      OutlinedTextField(
                          value = description,
                          onValueChange = { description = it },
                          textStyle = TextStyle(color = Color.White),
                          placeholder = {
                            Text(
                                "Enter description",
                                style =
                                    TextStyle(
                                        color = Color.White.copy(alpha = 0.7f),
                                        fontStyle = FontStyle.Italic))
                          },
                          colors =
                              TextFieldDefaults.outlinedTextFieldColors(
                                  cursorColor = Color.White,
                                  focusedBorderColor = Color.White,
                                  unfocusedBorderColor = Color.White.copy(alpha = 0.5f)),
                          modifier =
                              Modifier.fillMaxWidth()
                                  .clip(RoundedCornerShape(12.dp))
                                  .background(DarkPurple.copy(alpha = 0.5f))
                                  .padding(8.dp)
                                  .focusRequester(focusRequester)
                                  .testTag("edit_description_field"),
                          keyboardOptions =
                              KeyboardOptions.Default.copy(imeAction = ImeAction.Done),
                          keyboardActions =
                              KeyboardActions(
                                  onDone = {
                                    isEditing = false
                                    keyboardController?.hide()
                                  }))

                      LaunchedEffect(Unit) {
                        focusRequester.requestFocus()
                        keyboardController?.show()
                      }
                    } else {
                      Text(
                          text = if (description.isEmpty()) "Add a description" else description,
                          color = Color.White,
                          style =
                              if (description.isEmpty())
                                  TextStyle(color = Color.White, fontStyle = FontStyle.Italic)
                              else TextStyle(color = Color.White),
                          modifier =
                              Modifier.fillMaxWidth()
                                  .clip(RoundedCornerShape(12.dp))
                                  .height(75.dp)
                                  .background(DarkPurple.copy(alpha = 0.5f))
                                  .padding(8.dp)
                                  .clickable { isEditing = true }
                                  .testTag("description_text"))
                    }

                    Spacer(modifier = Modifier.height(35.dp))

                    if (uploadStatus.isLoading) {
                      CircularProgressIndicator(
                          modifier =
                              Modifier.size(35.dp)
                                  .align(Alignment.CenterHorizontally)
                                  .testTag("loading_indicator"),
                          color = DarkPurple)
                    } else {

                      ActionButton(
                          text = "Post",
                          onClick = { imageFile?.let { imageViewModel.uploadImage(it) } },
                          modifier = Modifier.testTag("confirm_button"),
                          color = DarkPurple)
                    }

                    ActionButton(
                        text = "Discard Image",
                        onClick = {
                          Toast.makeText(context, "Image discarded", Toast.LENGTH_SHORT).show()
                          navigationActions.navigateTo(Screen.TAKE_IMAGE)
                        },
                        modifier = Modifier.testTag("cancel_button").padding(vertical = 10.dp),
                        color = Color.Red)
                  }
            }

        // Display upload status messages and create a post upon successful upload
        when {
          uploadStatus.downloadUrl != null -> {
            val downloadUrl = uploadStatus.downloadUrl
            val currentLocation = locationProvider.currentLocation.value
            if (downloadUrl != null && currentLocation != null) {
              val newPost =
                  Post(
                      uid = postsViewModel.generateNewUid(),
                      uri = downloadUrl,
                      userMail = FirebaseAuth.getInstance().currentUser?.email ?: "Anonymous",
                      username = FirebaseAuth.getInstance().currentUser?.displayName ?: "Anonymous",
                      latitude = currentLocation.latitude,
                      longitude = currentLocation.longitude,
                      description = description,
                      timestamp = currentTimestamp)
              postsViewModel.addPost(newPost)
              collectionViewModel.updateImages()
              Toast.makeText(context, "Image saved", Toast.LENGTH_SHORT).show()
              navigationActions.navigateTo(Screen.GOOGLE_MAP)
            } else {
              Toast.makeText(
                      context, "Failed to get current location or download URL", Toast.LENGTH_SHORT)
                  .show()
            }
            imageViewModel.resetUploadStatus()
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
