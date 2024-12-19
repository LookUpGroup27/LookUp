package com.github.lookupgroup27.lookup.ui.overview

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import com.github.lookupgroup27.lookup.ui.navigation.NavigationActions
import org.junit.Rule
import org.junit.Test
import org.mockito.kotlin.mock

class SampleScreenTest {

  @get:Rule val composeTestRule = createComposeRule()

  private val mockNavigationActions: NavigationActions = mock()

  @Test
  fun sampleScreen_displaysCorrectText() {
    // Set up test input
    val testText = "Sample Screen Text"
    val screenTag = "sampleScreenTag"
    val backButtonTag = "backButtonTag"

    // Render the composable
    composeTestRule.setContent {
      SampleScreen(
          screenText = testText,
          navigationActions = mockNavigationActions,
          screenTag = screenTag,
          backButtonTag = backButtonTag)
    }

    // Verify the screen text is displayed
    composeTestRule.onNodeWithText(testText).assertIsDisplayed()
  }

  @Test
  fun sampleScreen_hasCorrectTestTags() {
    // Set up test input
    val screenTag = "sampleScreenTag"
    val backButtonTag = "backButtonTag"

    // Render the composable
    composeTestRule.setContent {
      SampleScreen(
          screenText = "Sample Screen Text",
          navigationActions = mockNavigationActions,
          screenTag = screenTag,
          backButtonTag = backButtonTag)
    }

    // Verify the test tags are present
    composeTestRule.onNodeWithTag(screenTag).assertIsDisplayed()
    composeTestRule.onNodeWithTag(backButtonTag).assertIsDisplayed()
  }
}
