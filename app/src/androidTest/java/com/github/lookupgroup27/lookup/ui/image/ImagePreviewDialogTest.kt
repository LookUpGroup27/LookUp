package com.github.lookupgroup27.lookup.ui.image

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import org.junit.Rule
import org.junit.Test

class ImagePreviewDialogTest {

  @get:Rule val composeTestRule = createComposeRule()

  @Test
  fun imagePreviewDialogDisplaysWithValidUri() {
    // Set up a fake image URI
    val fakeUri = "https://via.placeholder.com/150"
    val fakeUsername = "User1"

    // Set the Compose content to ImagePreviewDialog
    composeTestRule.setContent {
      ImagePreviewDialog(uri = fakeUri, username = fakeUsername, onDismiss = {})
    }

    // Verify that the dialog is displayed
    composeTestRule.onNodeWithTag("imagePreviewDialog").assertIsDisplayed()
    // Verify that the username is displayed
    composeTestRule.onNodeWithText("Posted by: $fakeUsername").assertIsDisplayed()
  }

  @Test
  fun imagePreviewDialogCloseButtonWorks() {
    val fakeUri = "https://via.placeholder.com/150"
    var dialogDismissed = false

    // Set the Compose content to ImagePreviewDialog
    composeTestRule.setContent {
      ImagePreviewDialog(uri = fakeUri, username = "User1", onDismiss = { dialogDismissed = true })
    }

    // Perform click on the "Close" button
    composeTestRule.onNodeWithText("Close").performClick()

    // Verify that the dialog was dismissed
    assert(dialogDismissed)
  }
}
