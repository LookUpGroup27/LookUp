package com.github.lookupgroup27.lookup.ui.fullscreen

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.github.lookupgroup27.lookup.R

/**
 * A full-screen image viewer composable with toast notifications for empty or invalid image URLs.
 *
 * This composable displays a single image in full screen mode, along with the username and
 * description of the associated post at the top of the screen. It includes a background image and
 * provides toast notifications for empty or failed image loads. The user can navigate back using a
 * back button in the top app bar.
 *
 * @param imageUrl The URL of the image to display.
 * @param onBack The callback function invoked when the back button is pressed.
 * @param username The username of the post's owner.
 * @param description The description of the post.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FullScreenImageScreen(
    imageUrl: String,
    onBack: () -> Unit,
    username: String,
    description: String
) {
  val context = LocalContext.current

  // State to track if a toast for empty imageUrl has been shown
  var hasShownEmptyToast by remember { mutableStateOf(false) }

  // State to track if a toast for image loading error has been shown
  var hasShownErrorToast by remember { mutableStateOf(false) }

  // Side effect to show toast if imageUrl is empty
  LaunchedEffect(imageUrl) {
    if (imageUrl.isBlank() && !hasShownEmptyToast) {
      Toast.makeText(context, "No image available.", Toast.LENGTH_SHORT).show()
      hasShownEmptyToast = true
    }
  }

  Scaffold(
      containerColor = Color.Black,
      topBar = {
        TopAppBar(
            title = {},
            navigationIcon = {
              IconButton(onClick = onBack, modifier = Modifier.testTag("back_button")) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Navigation Back",
                    tint = Color.White)
              }
            },
            colors =
                TopAppBarDefaults.smallTopAppBarColors(
                    containerColor = Color.Black.copy(alpha = 0.5f)),
            modifier = Modifier.testTag("top_app_bar"))
      },
      modifier = Modifier.fillMaxSize().testTag("full_screen_image_screen")) { innerPadding ->
        Box(
            modifier = Modifier.fillMaxSize().padding(innerPadding),
            contentAlignment = Alignment.Center) {
              // Background Image
              Image(
                  painter = painterResource(id = R.drawable.landing_screen_bckgrnd),
                  contentDescription = "Background Image",
                  contentScale = ContentScale.Crop,
                  modifier = Modifier.fillMaxSize().testTag("background_image"))

              if (imageUrl.isNotBlank()) {
                val painter =
                    rememberAsyncImagePainter(
                        ImageRequest.Builder(context).data(imageUrl).crossfade(true).build())

                Image(
                    painter = painter,
                    contentDescription = "Full Screen Image",
                    contentScale = ContentScale.Fit,
                    modifier = Modifier.fillMaxSize().padding(16.dp).testTag("main_image"))

                // Monitor the state of the image loading
                val imageState = painter.state

                // Side effect to show toast if image fails to load
                LaunchedEffect(imageState) {
                  if (imageState is coil.compose.AsyncImagePainter.State.Error &&
                      !hasShownErrorToast) {
                    Toast.makeText(context, "Failed to load image.", Toast.LENGTH_SHORT).show()
                    hasShownErrorToast = true
                  }
                }

                if (imageState is coil.compose.AsyncImagePainter.State.Loading) {
                  CircularProgressIndicator(
                      color = Color.White,
                      modifier =
                          Modifier.size(48.dp).align(Alignment.Center).testTag("loading_indicator"))
                }
              }

              // Display username and description overlay at the top
              Box(
                  modifier =
                      Modifier.fillMaxWidth()
                          .align(Alignment.TopCenter)
                          .background(
                              brush =
                                  Brush.verticalGradient(
                                      colors =
                                          listOf(
                                              Color.Black.copy(alpha = 0.7f), Color.Transparent)))
                          .padding(horizontal = 16.dp, vertical = 16.dp)
                          .testTag("top_overlay")) {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        horizontalAlignment = Alignment.Start,
                        modifier = Modifier.testTag("user_info_column")) {
                          if (username.isNotEmpty()) {
                            Text(
                                text = username,
                                style =
                                    MaterialTheme.typography.titleMedium.copy(color = Color.White),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                modifier = Modifier.testTag("username_text"))
                          }

                          if (description.isNotEmpty()) {
                            Text(
                                text = description,
                                style =
                                    MaterialTheme.typography.bodyMedium.copy(color = Color.White),
                                maxLines = 3,
                                overflow = TextOverflow.Ellipsis,
                                modifier = Modifier.testTag("description_text"))
                          }
                        }
                  }
            }
      }
}
