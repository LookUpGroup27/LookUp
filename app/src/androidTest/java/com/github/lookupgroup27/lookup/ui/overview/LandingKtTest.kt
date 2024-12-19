package com.github.lookupgroup27.lookup.ui.overview

import androidx.compose.ui.test.assertHasClickAction
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.test.espresso.intent.Intents
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.uiautomator.UiDevice
import com.github.lookupgroup27.lookup.ui.navigation.NavigationActions
import com.github.lookupgroup27.lookup.ui.navigation.Screen
import com.github.lookupgroup27.lookup.utils.TestNetworkUtils.simulateOnlineMode
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.kotlin.mock
import org.mockito.kotlin.never
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

    // Ensure the Logo is displayed by scrolling if needed and checking for visibility
    composeTestRule.onNodeWithContentDescription("Look Up Logo").assertIsDisplayed()

    // Ensure Home Icon is displayed and clickable
    composeTestRule.onNodeWithContentDescription("Home Icon").performScrollTo().assertIsDisplayed()
  }

  @Test
  fun backgroundIsClickableAndNavigatesToMap() {

    // simulate online mode
    simulateOnlineMode(true)
    composeTestRule.setContent { LandingScreen(mockNavigationActions) }

    // Verify that the background image is displayed and clickable
    composeTestRule.onNodeWithContentDescription("Background").assertIsDisplayed()
    composeTestRule.onNodeWithContentDescription("Background").assertHasClickAction()

    // Perform click action on the background image
    composeTestRule.onNodeWithContentDescription("Background").performClick()

    // Wait for the UI to settle after the click
    composeTestRule.waitForIdle()

    // Assert that the Map screen is displayed by checking for specific text or UI elements
    verify(mockNavigationActions).navigateTo(Screen.SKY_MAP)
  }

  @Test
  fun homeButtonIsClickable() {
    composeTestRule.setContent { LandingScreen(mockNavigationActions) }

    // Perform click action on the home button using its testTag
    composeTestRule.onNodeWithContentDescription("Home Icon").performClick()

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

  @Test
  fun testLandingScreenIsFullyVisibleInLandscape() {
    // Set device orientation to landscape
    setLandscapeOrientation()

    // Launch LandingScreen in landscape mode
    composeTestRule.setContent { LandingScreen(mockNavigationActions) }

    // Ensure main UI elements are visible and accessible by scrolling if needed
    composeTestRule.onNodeWithContentDescription("Look Up Logo").assertIsDisplayed()
    composeTestRule
        .onNodeWithContentDescription("Home Icon")
        .performScrollTo()
        .assertIsDisplayed()
        .assertHasClickAction()
    composeTestRule
        .onNodeWithContentDescription("Background")
        .assertIsDisplayed()
        .assertHasClickAction()
    composeTestRule.onNodeWithText("Click for full map view").assertIsDisplayed()

    // Reset orientation to portrait after the test
    resetOrientation()
  }

  @Test
  fun testBackgroundClickDoesNotNavigateToMapWhenOffline() {
    // Simulate offline mode
    simulateOnlineMode(false)

    composeTestRule.setContent { LandingScreen(mockNavigationActions) }

    // Verify the background is clickable
    composeTestRule.onNodeWithTag("LandingScreen").assertHasClickAction()

    // Perform a click action on the background
    composeTestRule.onNodeWithTag("LandingScreen").performClick()

    // Wait for the UI to settle after the click
    composeTestRule.waitForIdle()

    // Verify that navigation to the Sky Map is never triggered
    verify(mockNavigationActions, never()).navigateTo(Screen.SKY_MAP)
  }

  private fun setLandscapeOrientation() {
    val device = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation())
    device.setOrientationLeft()
  }

  private fun resetOrientation() {
    val device = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation())
    device.setOrientationNatural()
  }
}
