package com.github.lookupgroup27.lookup.ui.profile

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
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
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.launch

@Composable
fun CollectionScreen(
    navigationActions: NavigationActions,
    testImageUrls: List<String> = emptyList()
) {
  val context = LocalContext.current
  val scope = rememberCoroutineScope()
  val imageUrls = remember { mutableStateListOf<String>().apply { addAll(testImageUrls) } }

  if (testImageUrls.isEmpty()) {
    // Only perform Firebase fetch if `testImageUrls` is not provided
    LaunchedEffect(Unit) {
      scope.launch {
        val storage = FirebaseStorage.getInstance()
        val imagesRef = storage.getReference().child("images/")

        imagesRef
            .listAll()
            .addOnSuccessListener { result ->
              if (result.items.isEmpty()) {
                Log.d("FirebaseImageDebug", "No items found in the 'images/' folder")
              } else {
                result.items
                    .sortedBy { it.name }
                    .forEach { item ->
                      item.downloadUrl
                          .addOnSuccessListener { uri -> imageUrls.add(uri.toString()) }
                          .addOnFailureListener { exception ->
                            Log.e(
                                "FirebaseImageError",
                                "Failed to get download URL for ${item.name}: ${exception.message}")
                          }
                    }
              }
            }
            .addOnFailureListener { exception ->
              Log.e(
                  "FirebaseImageError",
                  "Failed to list images in Firebase Storage: ${exception.message}")
              Toast.makeText(
                      context, "Failed to load images: ${exception.message}", Toast.LENGTH_SHORT)
                  .show()
            }
      }
    }
  }

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
        onClick = { navigationActions.goBack() },
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

          if (imageUrls.isEmpty()) {
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
                  imageUrls.chunked(2).forEachIndexed { rowIndex, rowImages ->
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier.fillMaxWidth().testTag("image_row_$rowIndex")) {
                          rowImages.forEachIndexed { colIndex, imageUrl ->
                            Box(
                                modifier =
                                    Modifier.weight(1f)
                                        .aspectRatio(1f)
                                        .testTag("image_box_${rowIndex}_$colIndex"),
                                contentAlignment = Alignment.Center) {
                                  Image(
                                      painter = rememberAsyncImagePainter(imageUrl),
                                      contentDescription = "Collection Image",
                                      contentScale = ContentScale.Crop,
                                      modifier =
                                          Modifier.fillMaxSize()
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
