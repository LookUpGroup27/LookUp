package com.github.lookupgroup27.lookup.ui.image

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.rule.GrantPermissionRule
import com.github.lookupgroup27.lookup.ui.navigation.NavigationActions
import com.github.lookupgroup27.lookup.ui.navigation.Screen
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify

@RunWith(AndroidJUnit4::class)
class TakeImageTest {

  @get:Rule val composeTestRule = createComposeRule()

  @get:Rule
  val permissionRule: GrantPermissionRule =
      GrantPermissionRule.grant(android.Manifest.permission.CAMERA)

  private val mockNavigationActions: NavigationActions = mock()

  @Test
  fun testCameraCaptureIsDisplayed() {
    composeTestRule.setContent { CameraCapture(mockNavigationActions) }

    composeTestRule.onNodeWithTag("camera_capture").assertIsDisplayed()
  }

  @Test
  fun testGoBackButtonIsDisplayedAndClickable() {
    composeTestRule.setContent { CameraCapture(mockNavigationActions) }

    composeTestRule.onNodeWithTag("go_back_button_camera").assertIsDisplayed()
    composeTestRule.onNodeWithTag("go_back_button_camera").performClick()

    verify(mockNavigationActions).navigateTo(Screen.GOOGLE_MAP)
  }

  @Test
  fun testTakePictureButtonIsDisplayedAndClickable() {
    composeTestRule.setContent { CameraCapture(mockNavigationActions) }

    composeTestRule.onNodeWithTag("take_picture_button").assertIsDisplayed()
    composeTestRule.onNodeWithTag("take_picture_button").performClick()
  }
}
