package com.github.lookupgroup27.lookup.overview

import androidx.compose.ui.test.assertHasClickAction
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.espresso.intent.Intents
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.lookupgroup27.lookup.MainActivity
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class LandingScreenTest {

  @get:Rule val composeTestRule = createAndroidComposeRule<MainActivity>()

  @Before
  fun setUp() {
    // Initialize Espresso Intents to capture and validate outgoing intents
    Intents.init()
  }

  @After
  fun tearDown() {
    // Release Espresso Intents after tests
    Intents.release()
  }

  @Test
  fun logoAndButtonAreDisplayed() {
    // Verify that the Look Up logo is displayed
    composeTestRule.onNodeWithContentDescription("Look Up Logo").assertIsDisplayed()

    // Verify that the Home button is displayed and clickable
    composeTestRule.onNodeWithContentDescription("Home Icon").assertIsDisplayed()
    composeTestRule.onNodeWithContentDescription("Home Icon").assertHasClickAction()
  }

  @Test
  fun backgroundIsClickableAndNavigatesToMap() {
    // Verify that the background image is displayed and clickable
    composeTestRule.onNodeWithContentDescription("Background").assertIsDisplayed()
    composeTestRule.onNodeWithContentDescription("Background").assertHasClickAction()

    // Perform click action on the background image
    composeTestRule.onNodeWithContentDescription("Background").performClick()

    // Wait for the UI to settle after the click
    composeTestRule.waitForIdle()

    // Assert that the Map screen is displayed by checking for specific text or UI elements
    composeTestRule.onNodeWithText("This is the Map screen.").assertIsDisplayed()
  }

  @Test
  fun homeButtonIsClickable() {
    // Perform click action on the home button using its testTag
    composeTestRule.onNodeWithTag("Home Icon").performClick()

    // Wait for the UI to settle after the click
    composeTestRule.waitForIdle()

    // Assert that the Menu screen is displayed by checking for specific text
    composeTestRule.onNodeWithText("This is the Menu screen.").assertIsDisplayed()
  }
}
