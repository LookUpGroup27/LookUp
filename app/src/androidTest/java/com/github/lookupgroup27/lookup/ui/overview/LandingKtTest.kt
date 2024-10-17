package com.github.lookupgroup27.lookup.ui.overview

import androidx.compose.ui.test.assertHasClickAction
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.espresso.intent.Intents
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.lookupgroup27.lookup.MainActivity
import com.github.lookupgroup27.lookup.ui.navigation.NavigationActions
import com.github.lookupgroup27.lookup.ui.navigation.Screen
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify

@RunWith(AndroidJUnit4::class)
class LandingKtTest {

  @get:Rule val composeTestRule = createComposeRule()

  private val mockNavigationActions: NavigationActions = mock()

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
    composeTestRule.setContent { LandingScreen(mockNavigationActions) }

    // Verify that the Look Up logo is displayed
    composeTestRule.onNodeWithContentDescription("Look Up Logo").assertIsDisplayed()

    // Verify that the Home button is displayed and clickable
    composeTestRule.onNodeWithContentDescription("Home Icon").assertIsDisplayed()
    composeTestRule.onNodeWithContentDescription("Home Icon").assertHasClickAction()
  }

  @Test
  fun backgroundIsClickableAndNavigatesToMap() {
    composeTestRule.setContent { LandingScreen(mockNavigationActions) }

    // Verify that the background image is displayed and clickable
    composeTestRule.onNodeWithContentDescription("Background").assertIsDisplayed()
    composeTestRule.onNodeWithContentDescription("Background").assertHasClickAction()

    // Perform click action on the background image
    composeTestRule.onNodeWithContentDescription("Background").performClick()

    // Wait for the UI to settle after the click
    composeTestRule.waitForIdle()

    // Assert that the Map screen is displayed by checking for specific text or UI elements
    verify(mockNavigationActions).navigateTo(Screen.MAP)
  }

  @Test
  fun homeButtonIsClickable() {
    composeTestRule.setContent { LandingScreen(mockNavigationActions) }

    // Perform click action on the home button using its testTag
    composeTestRule.onNodeWithTag("Home Icon").performClick()

    // Wait for the UI to settle after the click
    composeTestRule.waitForIdle()

    // Assert that the Menu screen is displayed by checking for specific text
    verify(mockNavigationActions).navigateTo(Screen.MENU)
  }

  @Test
  fun testMapViewPromptIsDisplayed() {
    composeTestRule.setContent { LandingScreen(mockNavigationActions) }

    // Assert that it is displayed
    composeTestRule.onNodeWithText("Click for full map view").assertIsDisplayed()
  }
}
