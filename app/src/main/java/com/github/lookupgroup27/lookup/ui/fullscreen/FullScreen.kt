package com.github.lookupgroup27.lookup.ui.fullscreen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest

/**
 * A full-screen image viewer composable.
 *
 * This composable displays a single image in full screen mode, along with the username and
 * description of the associated post at the top of the screen. A semi-transparent gradient overlay
 * is applied to ensure readability of the text. The user can navigate back using a back button in
 * the top app bar.
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
  Scaffold(
      containerColor = Color.Black,
      topBar = {
        TopAppBar(
            title = {},
            navigationIcon = {
              IconButton(onClick = onBack, modifier = Modifier.testTag("back_button")) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
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
            modifier =
                Modifier.fillMaxSize().padding(innerPadding).drawBehind {
                  // Vertical gradient overlay for styling consistency
                  drawRect(
                      brush =
                          Brush.verticalGradient(
                              colors =
                                  listOf(
                                      Color.Black.copy(alpha = 0.6f),
                                      Color.Transparent,
                                      Color.Black.copy(alpha = 0.6f))))
                },
            contentAlignment = Alignment.Center) {
              // Display the main image
              Image(
                  painter =
                      rememberAsyncImagePainter(
                          ImageRequest.Builder(LocalContext.current)
                              .data(imageUrl)
                              .crossfade(true)
                              .build()),
                  contentDescription = "Full Screen Image",
                  contentScale = ContentScale.Fit,
                  modifier = Modifier.fillMaxSize().padding(16.dp).testTag("main_image"))

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
