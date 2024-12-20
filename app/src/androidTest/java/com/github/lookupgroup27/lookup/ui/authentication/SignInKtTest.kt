package com.github.lookupgroup27.lookup.ui.authentication

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.Intents.intended
import androidx.test.espresso.intent.matcher.IntentMatchers.toPackage
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.lookupgroup27.lookup.ui.navigation.NavigationActions
import com.github.lookupgroup27.lookup.ui.navigation.Screen
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
    composeTestRule.onNodeWithTag("go_back_button_signin").assertIsDisplayed()
  }

  @Test
  fun goBackButtonNavigatesBack() {
    composeTestRule.setContent { SignInScreen(mockNavigationActions) }

    // Perform click on the go-back button after scrolling to it
    composeTestRule.onNodeWithTag("go_back_button_signin").performClick()
    composeTestRule.waitForIdle()

    // Assert that the user is navigated back to the previous screen
    verify(mockNavigationActions).navigateTo(Screen.MENU)
  }

  @Test
  fun backArrowIsInTopLeftCorner() {
    composeTestRule.setContent { SignInScreen(mockNavigationActions) }

    val backArrowBounds =
        composeTestRule.onNodeWithTag("go_back_button_signin").fetchSemanticsNode().boundsInRoot

    val screenWidth = composeTestRule.onRoot().fetchSemanticsNode().boundsInRoot.width
    val screenHeight = composeTestRule.onRoot().fetchSemanticsNode().boundsInRoot.height

    assert(backArrowBounds.left < screenWidth * 0.1f)
    assert(backArrowBounds.top < screenHeight * 0.1f)
  }
}
