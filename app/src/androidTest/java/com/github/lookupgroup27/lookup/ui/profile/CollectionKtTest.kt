package com.github.lookupgroup27.lookup.ui.profile

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.navigation.NavHostController
import androidx.test.core.app.ApplicationProvider
import com.github.lookupgroup27.lookup.ui.navigation.NavigationActions
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

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

    // Verify rows and image boxes based on the mock data
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

  private fun mockNavigationActions() =
      NavigationActions(
          navController = NavHostController(ApplicationProvider.getApplicationContext()))
}
