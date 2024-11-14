package com.github.lookupgroup27.lookup

import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.semantics.getOrNull
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
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
  fun quizFlow() {
    composeTestRule.onNodeWithContentDescription("Home Icon").performClick()
    composeTestRule.waitForIdle()
    composeTestRule.onNodeWithText("Quizzes").performClick()
    composeTestRule.waitForIdle()

    // Play Earth Quiz
    composeTestRule.onNodeWithText("Earth").performClick()
    composeTestRule.waitForIdle()

    // Answer the quiz randomly
    for (i in 1..15) {
      var randomOption = (0..3).random()
      val changeOption = (Math.random() < 0.3)
      composeTestRule.onNodeWithTag("answer_button_$randomOption").performClick()
      composeTestRule.waitForIdle()

      if (changeOption) {
        randomOption = (0..3).random()
        composeTestRule.onNodeWithTag("answer_button_$randomOption").performClick()
        composeTestRule.waitForIdle()
      }

      composeTestRule.onNodeWithText("Next Question").performClick()
      composeTestRule.waitForIdle()
    }

    // Find the score and extract it
    val scoreText =
        composeTestRule
            .onNode(hasText("Your score:", substring = true))
            .fetchSemanticsNode()
            .config
            .getOrNull(SemanticsProperties.Text)
            ?.get(0)
            ?.text ?: throw AssertionError("Score not found")
    val score = scoreText.substringAfter("Your score:").substringBefore("/").trim().toInt()

    composeTestRule.onNodeWithText("Return to Quiz Selection").performClick()
    composeTestRule.waitForIdle()

    // Check if the Earth score is displayed and matches the score obtained
    val earthScore =
        composeTestRule
            .onNode(hasText("Earth", substring = true))
            .fetchSemanticsNode()
            .config
            .getOrNull(SemanticsProperties.Text)
            ?.filter { it.text.contains("Best Score:") }
            ?.map { it.text.substringAfter("Best Score:").substringBefore("/").trim().toInt() }
            ?.get(0) ?: throw AssertionError("Earth score not found")

    assert(earthScore == score) { "Earth score does not match" }
  }

  @Test
  fun navigationFlow() {
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
    composeTestRule.onNodeWithText("Menu").performClick()
    composeTestRule.waitForIdle()
    composeTestRule.onNodeWithTag("menu_screen").assertIsDisplayed()

    // Step 4: Navigate to ProfileScreen, then SignInScreen if not authenticated
    composeTestRule.onNodeWithTag("profile_button").performClick()
    composeTestRule.waitForIdle()
    composeTestRule.onNodeWithTag("auth_screen").assertIsDisplayed()
    composeTestRule.onNodeWithTag("go_back_button_signin").performClick()
    composeTestRule.waitForIdle()
    composeTestRule.onNodeWithTag("menu_screen").assertIsDisplayed()

    // Step 5: Navigate to MapScreen from MenuScreen
    composeTestRule.onNodeWithText("Map").performClick()
    composeTestRule.waitForIdle()
    composeTestRule.onNodeWithTag("map_screen").assertIsDisplayed()
    composeTestRule.onNodeWithTag(TopLevelDestinations.MENU.textId).performClick()
    composeTestRule.waitForIdle()
    composeTestRule.onNodeWithTag("menu_screen").assertIsDisplayed()

    // Step 6: Navigate to Quizzes screen from MenuScreen
    composeTestRule.onNodeWithText("Quizzes").performClick()
    composeTestRule.waitForIdle()
    composeTestRule.onNodeWithTag("quiz_screen").assertIsDisplayed()
    composeTestRule.onNodeWithTag("go_back_button_quiz").performClick()
    composeTestRule.waitForIdle()
    composeTestRule.onNodeWithTag("menu_screen").assertIsDisplayed()

    // Step 7: Navigate to Google Map from MenuScreen
    composeTestRule.onNodeWithText("Google Map").performClick()
    composeTestRule.waitForIdle()

    val allowButton: UiObject2? =
        device.wait(Until.findObject(By.text("While using the app")), PERMISSION_DIALOG_TIMEOUT)
    if (allowButton != null) {
      allowButton.click()
    } else {
      throw AssertionError("Timeout while waiting for permission dialog")
    }

    composeTestRule.waitForIdle()
    composeTestRule.onNodeWithTag("googleMapScreen").assertIsDisplayed()
    composeTestRule.onNodeWithText("Menu").performClick()
    composeTestRule.waitForIdle()
    composeTestRule.onNodeWithTag("menu_screen").assertIsDisplayed()

    // Step 8: Navigate to FeedScreen from MenuScreen
    composeTestRule.onNodeWithText("Feed").performClick()
    composeTestRule.waitForIdle()

    composeTestRule.waitForIdle()
    composeTestRule.onNodeWithTag("feed_screen").assertIsDisplayed()
    composeTestRule.onNodeWithText("Menu").performClick()
    composeTestRule.waitForIdle()
    composeTestRule.onNodeWithTag("menu_screen").assertIsDisplayed()
  }
}
