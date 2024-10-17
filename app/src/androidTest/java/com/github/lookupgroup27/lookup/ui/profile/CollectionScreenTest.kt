package com.github.lookupgroup27.lookup.ui.profile

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import org.junit.Rule
import org.junit.Test

class CollectionScreenTest {

  @get:Rule val composeTestRule = createComposeRule()

  @Test
  fun collectionScreen_displaysPlaceholderText() {
    // Set the content of the test to the CollectionScreen composable
    composeTestRule.setContent { CollectionScreen() }

    // Find the text node with "Your Collection" and assert it is displayed
    composeTestRule.onNodeWithText("Your Collection").assertIsDisplayed()
  }
}
