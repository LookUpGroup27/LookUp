package com.github.lookupgroup27.lookup

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.lookupgroup27.lookup.ui.navigation.TopLevelDestinations
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class End2EndTest {

  @get:Rule val composeTestRule = createAndroidComposeRule<MainActivity>()

  @Test
  fun testEndToEndNavigationFlow() {

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
    composeTestRule.onNodeWithTag(TopLevelDestinations.MENU.textId).performClick()
    composeTestRule.waitForIdle()
    composeTestRule.onNodeWithTag("menu_screen").assertIsDisplayed()

    // Step 5: Navigate to ProfileScreen, then SignInScreen if not authenticated
    composeTestRule.onNodeWithTag("profile_button").performClick()
    composeTestRule.waitForIdle()
    composeTestRule.onNodeWithTag("auth_screen").assertIsDisplayed()
  }
}
