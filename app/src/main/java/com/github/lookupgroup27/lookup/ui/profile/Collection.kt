package com.github.lookupgroup27.lookup.ui.profile

import android.content.pm.ActivityInfo
import androidx.activity.ComponentActivity
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import com.github.lookupgroup27.lookup.R
import com.github.lookupgroup27.lookup.ui.navigation.NavigationActions
import com.github.lookupgroup27.lookup.ui.navigation.Route
import com.github.lookupgroup27.lookup.ui.navigation.Screen
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

/**
 * Composable function for displaying a user's collection of astronomy images.
 *
 * The `CollectionScreen` shows a list of images fetched from the user's collection. It allows users
 * to navigate back to the profile screen or select an image to edit it. The layout dynamically
 * adjusts based on whether the collection is empty or contains images.
 *
 * @param navigationActions The [NavigationActions] object for handling navigation between screens.
 * @param viewModel The [CollectionViewModel] instance managing the state of the user's image
 *   collection.
 */
@Composable
fun CollectionScreen(
    navigationActions: NavigationActions,
    viewModel: CollectionViewModel =
        androidx.lifecycle.viewmodel.compose.viewModel(factory = CollectionViewModel.Factory)
) {
  val context = LocalContext.current

  // Lock the screen orientation to portrait mode.
  DisposableEffect(Unit) {
    val activity = context as? ComponentActivity
    activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
    onDispose { activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED }
  }

  val myPosts by viewModel.myPosts.collectAsState()

  Box(
      modifier = Modifier.fillMaxSize().testTag("background_box"),
      contentAlignment = Alignment.TopCenter,
  ) {
    Image(
        painter = painterResource(id = R.drawable.landing_screen_bckgrnd),
        contentDescription = "Background",
        contentScale = ContentScale.Crop,
        modifier = Modifier.fillMaxSize().blur(10.dp).testTag("background_image"))

    IconButton(
        onClick = { navigationActions.navigateTo(Screen.PROFILE) },
        modifier =
            Modifier.padding(16.dp)
                .align(Alignment.TopStart)
                .testTag("go_back_button_collection")) {
          Icon(
              imageVector = Icons.AutoMirrored.Filled.ArrowBack,
              contentDescription = "Back",
              tint = Color.White)
        }

    Column(
        modifier =
            Modifier.fillMaxSize()
                .padding(top = 80.dp)
                .verticalScroll(rememberScrollState())
                .testTag("scrollable_column"),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top) {
          Text(
              text = "Your Astronomy Collection",
              fontSize = 24.sp,
              fontWeight = FontWeight.Bold,
              color = Color.White,
              modifier = Modifier.padding(bottom = 16.dp).testTag("title_text"))

          if (myPosts.isEmpty()) {
            Text(
                text = "No images in your collection yet.",
                color = Color.White,
                fontSize = 16.sp,
                modifier = Modifier.padding(top = 24.dp).testTag("no_images_text"))
          } else {
            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(8.dp)) {
                  myPosts.chunked(2).forEachIndexed { rowIndex, rowImages ->
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier.fillMaxWidth().testTag("image_row_$rowIndex")) {
                          rowImages.forEachIndexed { colIndex, post ->
                            post.uri.let {
                              Box(
                                  modifier =
                                      Modifier.weight(1f)
                                          .aspectRatio(1f)
                                          .testTag("image_box_${rowIndex}_$colIndex"),
                                  contentAlignment = Alignment.Center) {
                                    Image(
                                        painter = rememberAsyncImagePainter(it),
                                        contentDescription = "Collection Image",
                                        contentScale = ContentScale.Crop,
                                        modifier =
                                            Modifier.fillMaxSize()
                                                .clickable {
                                                  val encodedImageUrl =
                                                      URLEncoder.encode(
                                                          post.uri,
                                                          StandardCharsets.UTF_8.toString())
                                                  // val timestamp = System.currentTimeMillis()
                                                  navigationActions.navigateToWithPostInfo(
                                                      encodedUri = encodedImageUrl,
                                                      route = Route.EDIT_IMAGE,
                                                      postUid = post.uid,
                                                      postAverageStar = post.averageStars.toFloat(),
                                                      postDescription = post.description,
                                                      postRatedByNb = post.ratedBy.size)
                                                }
                                                .background(
                                                    MaterialTheme.colorScheme.surface.copy(
                                                        alpha = 0.3f)))
                                  }
                            }
                          }
                        }
                  }
                }
          }
        }
  }
}
