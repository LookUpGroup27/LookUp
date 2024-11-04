package com.github.lookupgroup27.lookup.ui.profile

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.navigation.NavHostController
import androidx.test.core.app.ApplicationProvider
import com.github.lookupgroup27.lookup.ui.navigation.NavigationActions
import org.junit.Rule
import org.junit.Test

class CollectionKtTest {

  @get:Rule val composeTestRule = createComposeRule()

  @Test
  fun collectionScreen_displaysCorrectly() {
    composeTestRule.setContent {
      val mockNavigationActions =
          NavigationActions(
              navController = NavHostController(ApplicationProvider.getApplicationContext()))
      CollectionScreen(navigationActions = mockNavigationActions)
    }

    composeTestRule.onNodeWithText("Your Astronomy Collection").assertIsDisplayed()
  }

  @Test
  fun collectionScreen_displaysNoImagesMessage_whenNoImagesAvailable() {
    composeTestRule.setContent {
      val mockNavigationActions =
          NavigationActions(
              navController = NavHostController(ApplicationProvider.getApplicationContext()))
      CollectionScreen(navigationActions = mockNavigationActions)
    }

    composeTestRule.onNodeWithText("No images in your collection yet.").assertIsDisplayed()
  }

  @Test
  fun collectionScreen_backButton_callsGoBack() {
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

    assert(backButtonClicked)
  }

  @Test
  fun backgroundBox_isDisplayed() {
    composeTestRule.setContent {
      val mockNavigationActions =
          NavigationActions(
              navController = NavHostController(ApplicationProvider.getApplicationContext()))
      CollectionScreen(navigationActions = mockNavigationActions)
    }

    composeTestRule.onNodeWithTag("background_box").assertIsDisplayed()
  }

  @Test
  fun backgroundImage_isDisplayed() {
    composeTestRule.setContent {
      val mockNavigationActions =
          NavigationActions(
              navController = NavHostController(ApplicationProvider.getApplicationContext()))
      CollectionScreen(navigationActions = mockNavigationActions)
    }

    composeTestRule.onNodeWithTag("background_image").assertIsDisplayed()
  }

  @Test
  fun titleText_isDisplayed() {
    composeTestRule.setContent {
      val mockNavigationActions =
          NavigationActions(
              navController = NavHostController(ApplicationProvider.getApplicationContext()))
      CollectionScreen(navigationActions = mockNavigationActions)
    }

    composeTestRule.onNodeWithTag("title_text").assertIsDisplayed()
  }

  @Test
  fun noImagesText_isDisplayed_whenImageUrlsAreEmpty() {
    composeTestRule.setContent {
      val mockNavigationActions =
          NavigationActions(
              navController = NavHostController(ApplicationProvider.getApplicationContext()))
      CollectionScreen(navigationActions = mockNavigationActions)
    }

    composeTestRule.onNodeWithTag("no_images_text").assertIsDisplayed()
  }
}
