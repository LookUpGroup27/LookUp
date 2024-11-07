package com.github.lookupgroup27.lookup.ui.image

import android.net.Uri
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.lookupgroup27.lookup.ui.navigation.NavigationActions
import com.github.lookupgroup27.lookup.ui.navigation.Screen
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify

@RunWith(AndroidJUnit4::class)
class ImageReviewTest {

  @get:Rule val composeTestRule = createComposeRule()

  val fakeUri: Uri = Uri.parse("content://com.example.app/fake_image.jpg")

  private val mockNavigationActions: NavigationActions = mock()

  @Test
  fun testImageReviewIsDisplayed() {
    composeTestRule.setContent { ImageReviewScreen(mockNavigationActions, fakeUri) }

    composeTestRule.onNodeWithTag("image_review").assertIsDisplayed()
  }

  @Test
  fun testConfirmButtonIsDisplayedAndClickable() {
    composeTestRule.setContent { ImageReviewScreen(mockNavigationActions, fakeUri) }

    composeTestRule.onNodeWithTag("confirm_button").assertIsDisplayed()
    composeTestRule.onNodeWithTag("confirm_button").performClick()
  }

  @Test
  fun testCancelButtonIsDisplayedAndClickable() {
    composeTestRule.setContent { ImageReviewScreen(mockNavigationActions, fakeUri) }

    composeTestRule.onNodeWithTag("cancel_button").assertIsDisplayed()
    composeTestRule.onNodeWithTag("cancel_button").performClick()

    verify(mockNavigationActions).navigateTo(Screen.TAKE_IMAGE)
  }
}
