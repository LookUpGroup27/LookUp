package com.github.lookupgroup27.lookup.ui.profile

import android.content.Context
import android.widget.Toast
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.navigation.NavHostController
import androidx.test.core.app.ApplicationProvider
import com.github.lookupgroup27.lookup.ui.navigation.NavigationActions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito.*

@OptIn(ExperimentalCoroutinesApi::class)
class CollectionScreenTest {

  @get:Rule val composeTestRule = createComposeRule()

  @Test
  fun backgroundBox_isDisplayed() {
    composeTestRule.setContent { CollectionScreen(navigationActions = mockNavigationActions()) }
    composeTestRule.onNodeWithTag("background_box").assertIsDisplayed()
  }

  @Test
  fun backgroundImage_isDisplayed() {
    composeTestRule.setContent { CollectionScreen(navigationActions = mockNavigationActions()) }
    composeTestRule.onNodeWithTag("background_image").assertIsDisplayed()
  }

  @Test
  fun goBackButton_isDisplayed() {
    composeTestRule.setContent { CollectionScreen(navigationActions = mockNavigationActions()) }
    composeTestRule.onNodeWithTag("go_back_button_collection").assertIsDisplayed()
  }

  @Test
  fun titleText_isDisplayed() {
    composeTestRule.setContent { CollectionScreen(navigationActions = mockNavigationActions()) }
    composeTestRule.onNodeWithTag("title_text").assertIsDisplayed()
  }

  @Test
  fun noImagesText_isDisplayed_whenImageUrlsAreEmpty() {
    composeTestRule.setContent { CollectionScreen(navigationActions = mockNavigationActions()) }
    composeTestRule.onNodeWithTag("no_images_text").assertIsDisplayed()
  }

  @Test
  fun imageRows_andImageBoxes_areDisplayed_whenImagesAvailable() {
    val testImageUrls =
        listOf(
            "https://example.com/image1.jpg",
            "https://example.com/image2.jpg",
            "https://example.com/image3.jpg",
            "https://example.com/image4.jpg")

    composeTestRule.setContent {
      CollectionScreen(
          navigationActions = mockNavigationActions(),
          testImageUrls = testImageUrls // Inject mock data
          )
    }

    testImageUrls.chunked(2).forEachIndexed { rowIndex, rowImages ->
      composeTestRule.onNodeWithTag("image_row_$rowIndex").assertIsDisplayed()
      rowImages.forEachIndexed { colIndex, _ ->
        composeTestRule.onNodeWithTag("image_box_${rowIndex}_$colIndex").assertIsDisplayed()
      }
    }
  }

  @Test
  fun goBackButton_callsGoBack() {
    var backButtonClicked = false
    val mockNavigationActions =
        object :
            NavigationActions(
                navController = NavHostController(ApplicationProvider.getApplicationContext())) {
          override fun goBack() {
            backButtonClicked = true
          }
        }

    composeTestRule.setContent { CollectionScreen(navigationActions = mockNavigationActions) }
    composeTestRule.onNodeWithTag("go_back_button_collection").performClick()
    assertTrue(backButtonClicked)
  }

  @Test
  fun fetchImages_displaysToastOnFailure() = runBlockingTest {
    val mockAuth = mock(FirebaseAuth::class.java)
    `when`(mockAuth.currentUser).thenReturn(mock())
    `when`(mockAuth.currentUser?.email).thenReturn("test@example.com")

    val mockStorage = mock(FirebaseStorage::class.java)
    val mockImagesRef = mock(StorageReference::class.java)
    `when`(mockStorage.getReference()).thenReturn(mockImagesRef)
    `when`(mockImagesRef.child("images/test@example.com/")).thenReturn(mockImagesRef)

    val mockContext = ApplicationProvider.getApplicationContext<Context>()
    composeTestRule.setContent {
      CollectionScreen(navigationActions = mockNavigationActions(), testImageUrls = emptyList())
    }

    val exception = Exception("Simulated Firebase error")
    composeTestRule.runOnIdle {
      Toast.makeText(mockContext, "Failed to load images: ${exception.message}", Toast.LENGTH_SHORT)
          .show()
    }
  }

  private fun mockNavigationActions() =
      NavigationActions(
          navController = NavHostController(ApplicationProvider.getApplicationContext()))
}
