package com.github.lookupgroup27.lookup

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.uiautomator.By
import androidx.test.uiautomator.UiDevice
import androidx.test.uiautomator.UiObject2
import androidx.test.uiautomator.Until
import com.github.lookupgroup27.lookup.ui.navigation.TopLevelDestinations
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

private const val PERMISSION_DIALOG_TIMEOUT = 5000L

@RunWith(AndroidJUnit4::class)
class End2EndTest {

  @get:Rule val composeTestRule = createAndroidComposeRule<MainActivity>()

  @Test
  fun testEndToEndNavigationFlow() {
    val device = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation())

    // Step 1: Verify initial Landing Screen
    composeTestRule.onNodeWithTag("LandingScreen").assertIsDisplayed()

    // Step 2: Navigate to MenuScreen from LandingScreen
    composeTestRule.onNodeWithTag("Home Icon").performClick()
    composeTestRule.waitForIdle()
    composeTestRule.onNodeWithTag("menu_screen").assertIsDisplayed()

    // Step 3: Navigate to CalendarScreen from MenuScreen
    composeTestRule.onNodeWithText("Calendar").performClick()
    composeTestRule.waitForIdle()
    composeTestRule.onNodeWithTag("calendar_screen").assertIsDisplayed()

    // Step 4: Navigate back and check MenuScreen is displayed
    composeTestRule.onNodeWithText("Menu").performClick()
    composeTestRule.waitForIdle()
    composeTestRule.onNodeWithTag("menu_screen").assertIsDisplayed()

    // Step 5: Navigate to ProfileScreen, then SignInScreen if not authenticated
    composeTestRule.onNodeWithTag("profile_button").performClick()
    composeTestRule.waitForIdle()
    composeTestRule.onNodeWithTag("auth_screen").assertIsDisplayed()

    // Step 6: Navigate back and check MenuScreen is displayed
    composeTestRule.onNodeWithTag("go_back_button_signin").performClick()
    composeTestRule.waitForIdle()
    composeTestRule.onNodeWithTag("menu_screen").assertIsDisplayed()

    // Step 7: Navigate to MapScreen from MenuScreen
    composeTestRule.onNodeWithText("Map").performClick()
    composeTestRule.waitForIdle()
    composeTestRule.onNodeWithTag("map_screen").assertIsDisplayed()

    // Step 8: Navigate back and check MenuScreen is displayed
    composeTestRule.onNodeWithTag(TopLevelDestinations.MENU.textId).performClick()
    composeTestRule.waitForIdle()
    composeTestRule.onNodeWithTag("menu_screen").assertIsDisplayed()

    // Step 9: Navigate to Quizzes screen from MenuScreen
    composeTestRule.onNodeWithText("Quizzes").performClick()
    composeTestRule.waitForIdle()
    composeTestRule.onNodeWithTag("quiz_screen").assertIsDisplayed()

    // Step 10: Navigate back and check MenuScreen is displayed
    composeTestRule.onNodeWithTag("go_back_button_quiz").performClick()
    composeTestRule.waitForIdle()
    composeTestRule.onNodeWithTag("menu_screen").assertIsDisplayed()

    // Step 11: Navigate to Google Map from MenuScreen
    composeTestRule.onNodeWithText("Google Map").performClick()
    composeTestRule.waitForIdle()

    val allowButton: UiObject2? =
        device.wait(Until.findObject(By.text("While using the app")), PERMISSION_DIALOG_TIMEOUT)
    if (allowButton != null) {
      allowButton.click()
    } else {
      throw AssertionError("Timeout while waiting for permission dialog")
    }

    composeTestRule.onNodeWithTag("googleMapScreen").assertIsDisplayed()

    // Step 12: Navigate back and check MenuScreen is displayed
    composeTestRule.onNodeWithText("Menu").performClick()
    composeTestRule.waitForIdle()
    composeTestRule.onNodeWithTag("menu_screen").assertIsDisplayed()
  }
}
