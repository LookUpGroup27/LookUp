package com.github.lookupgroup27.lookup.ui.authentication

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.Intents.intended
import androidx.test.espresso.intent.matcher.IntentMatchers.toPackage
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.uiautomator.UiDevice
import com.github.lookupgroup27.lookup.ui.navigation.NavigationActions
import com.kaspersky.kaspresso.testcases.api.testcase.TestCase
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify

@RunWith(AndroidJUnit4::class)
class SignInKtTest : TestCase() {

  @get:Rule val composeTestRule = createComposeRule()

  private val mockNavigationActions: NavigationActions = mock()

  @Before
  fun setUp() {
    Intents.init()
  }

  @After
  fun tearDown() {
    Intents.release()
  }

  @Test
  fun titleAndButtonAreCorrectlyDisplayed() {
    composeTestRule.setContent { SignInScreen(mockNavigationActions) }

    // Scroll to make sure components are visible on smaller screens
    composeTestRule.onNodeWithTag("loginTitle").performScrollTo().assertIsDisplayed()
    composeTestRule.onNodeWithTag("loginTitle").assertTextEquals("Welcome to the Cosmos")

    composeTestRule.onNodeWithTag("loginButton").performScrollTo().assertIsDisplayed()
    composeTestRule.onNodeWithTag("loginButton").assertHasClickAction()
  }

  @Test
  fun googleSignInReturnsValidActivityResult() {
    composeTestRule.setContent { SignInScreen(mockNavigationActions) }

    // Perform click on the Google Sign-In button after scrolling to it
    composeTestRule.onNodeWithTag("loginButton").performScrollTo().performClick()
    composeTestRule.waitForIdle()

    // Assert that an Intent to Google Mobile Services was sent
    intended(toPackage("com.google.android.gms"))
  }

  @Test
  fun goBackButtonIsCorrectlyDisplayed() {
    composeTestRule.setContent { SignInScreen(mockNavigationActions) }

    // Scroll to ensure the go-back button is visible
    composeTestRule.onNodeWithTag("go_back_button_signin").performScrollTo().assertIsDisplayed()
  }

  @Test
  fun goBackButtonNavigatesBack() {
    composeTestRule.setContent { SignInScreen(mockNavigationActions) }

    // Perform click on the go-back button after scrolling to it
    composeTestRule.onNodeWithTag("go_back_button_signin").performScrollTo().performClick()
    composeTestRule.waitForIdle()

    // Assert that the user is navigated back to the previous screen
    verify(mockNavigationActions).goBack()
  }

  @Test
  fun testSignInScreenIsFullyVisibleInLandscape() {
    // Set device orientation to landscape
    setLandscapeOrientation()

    // Launch the SignInScreen in landscape mode
    composeTestRule.setContent { SignInScreen(mockNavigationActions) }

    // Scroll to each component and verify it is displayed
    composeTestRule.onNodeWithTag("loginTitle").performScrollTo().assertIsDisplayed()
    composeTestRule.onNodeWithTag("loginTitle").assertTextEquals("Welcome to the Cosmos")
    composeTestRule
        .onNodeWithTag("loginButton")
        .performScrollTo()
        .assertIsDisplayed()
        .assertHasClickAction()
    composeTestRule
        .onNodeWithTag("go_back_button_signin")
        .performScrollTo()
        .assertIsDisplayed()
        .assertHasClickAction()

    // Reset orientation to portrait after the test
    resetOrientation()
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
