package com.github.lookupgroup27.lookup.ui.fullscreen

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import org.junit.Rule
import org.junit.Test

/**
 * UI tests for [FullScreenImageScreen].
 *
 * These tests verify that the main elements (back button, image, username, and description) are
 * displayed correctly and are accessible through their respective test tags.
 */
class FullScreenImageScreenTest {

  @get:Rule val composeTestRule = createComposeRule()

  private val testImageUrl = "https://example.com/test_image.jpg"
  private val testUsername = "TestUser"
  private val testDescription = "This is a test description."

  @Test
  fun fullScreenImageScreen_isDisplayed() {
    composeTestRule.setContent {
      FullScreenImageScreen(
          imageUrl = testImageUrl,
          onBack = {},
          username = testUsername,
          description = testDescription)
    }
    composeTestRule.onNodeWithTag("full_screen_image_screen").assertIsDisplayed()
  }

  @Test
  fun topAppBar_isDisplayed() {
    composeTestRule.setContent {
      FullScreenImageScreen(
          imageUrl = testImageUrl,
          onBack = {},
          username = testUsername,
          description = testDescription)
    }
    composeTestRule.onNodeWithTag("top_app_bar").assertIsDisplayed()
  }

  @Test
  fun backButton_isDisplayed() {
    composeTestRule.setContent {
      FullScreenImageScreen(
          imageUrl = testImageUrl,
          onBack = {},
          username = testUsername,
          description = testDescription)
    }
    composeTestRule.onNodeWithTag("back_button").assertIsDisplayed()
  }

  @Test
  fun mainImage_isDisplayed() {
    composeTestRule.setContent {
      FullScreenImageScreen(
          imageUrl = testImageUrl,
          onBack = {},
          username = testUsername,
          description = testDescription)
    }
    composeTestRule.onNodeWithTag("main_image").assertIsDisplayed()
  }

  @Test
  fun username_isDisplayed() {
    composeTestRule.setContent {
      FullScreenImageScreen(
          imageUrl = testImageUrl,
          onBack = {},
          username = testUsername,
          description = testDescription)
    }
    composeTestRule.onNodeWithTag("username_text").assertIsDisplayed()
  }

  @Test
  fun description_isDisplayed() {
    composeTestRule.setContent {
      FullScreenImageScreen(
          imageUrl = testImageUrl,
          onBack = {},
          username = testUsername,
          description = testDescription)
    }
    composeTestRule.onNodeWithTag("description_text").assertIsDisplayed()
  }
}
