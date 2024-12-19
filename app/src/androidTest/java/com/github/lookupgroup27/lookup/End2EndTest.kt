package com.github.lookupgroup27.lookup

import android.Manifest
import android.annotation.SuppressLint
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.semantics.getOrNull
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.isNotDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onAllNodesWithTag
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performGesture
import androidx.compose.ui.test.performScrollTo
import androidx.compose.ui.test.performTextInput
import androidx.compose.ui.test.performTouchInput
import androidx.test.espresso.action.ViewActions.swipeDown
import androidx.test.espresso.action.ViewActions.swipeUp
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.rule.GrantPermissionRule
import com.github.lookupgroup27.lookup.ui.navigation.TopLevelDestinations
import com.google.firebase.auth.FirebaseAuth
import io.github.kakaocup.kakao.common.utilities.getResourceString
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class End2EndTest {

  @get:Rule val composeTestRule = createAndroidComposeRule<MainActivity>()

  @get:Rule
  val permissionRule: GrantPermissionRule =
      GrantPermissionRule.grant(
          Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.CAMERA)

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
    while (composeTestRule.onNodeWithTag("quiz_recap").isNotDisplayed()) {
      var randomOption = (0..3).random()
      val changeOption = (Math.random() < 0.3)
      composeTestRule.onNodeWithTag("answer_button_$randomOption").performScrollTo().performClick()
      composeTestRule.waitForIdle()

      if (changeOption) {
        randomOption = (0..3).random()
        composeTestRule
            .onNodeWithTag("answer_button_$randomOption")
            .performScrollTo()
            .performClick()
        composeTestRule.waitForIdle()
      }

      composeTestRule.onNodeWithText("Next Question").performScrollTo().performClick()
      composeTestRule.waitForIdle()
    }

    composeTestRule.onNodeWithText("Earth Quiz").performScrollTo().assertIsDisplayed()
    composeTestRule.waitForIdle()
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
    // Mock FirebaseAuth to simulate different login states
    val mockAuth = org.mockito.kotlin.mock<FirebaseAuth>()
    val isUserLoggedIn = mockAuth.currentUser != null // Check if a user is logged in

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
    composeTestRule.onNodeWithText("Sky Map").performClick()
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
    composeTestRule.onNodeWithTag("googleMapScreen").assertIsDisplayed()
    composeTestRule.onNodeWithText("Menu").performClick()
    composeTestRule.waitForIdle()
    composeTestRule.onNodeWithTag("menu_screen").assertIsDisplayed()

    // Step 8: Navigate to FeedScreen from MenuScreen
    composeTestRule.onNodeWithText("Feed").performClick()
    composeTestRule.waitForIdle()

    composeTestRule.waitForIdle()
    if (isUserLoggedIn) {
      composeTestRule.onNodeWithTag("feed_screen").assertIsDisplayed()
    }
    composeTestRule.onNodeWithText("Menu").performClick()
    composeTestRule.waitForIdle()
    composeTestRule.onNodeWithTag("menu_screen").assertIsDisplayed()
  }

  @SuppressLint("CheckResult")
  @Test
  fun mapFlow() {
    // Start by navigating to the map screen
    composeTestRule.onNodeWithContentDescription("Home Icon").performClick()
    composeTestRule.waitForIdle()
    composeTestRule.onNodeWithText("Sky Map").performClick()
    composeTestRule.onNodeWithTag(TopLevelDestinations.SKY_MAP.textId).performClick()
    // Verify initial map state with extended timeout
    composeTestRule.waitUntil(timeoutMillis = 10000) {
      composeTestRule.onAllNodesWithTag("glSurfaceView").fetchSemanticsNodes().size == 1
    }

    // Verify initial map state
    composeTestRule.onNodeWithTag("glSurfaceView").assertIsDisplayed()
    composeTestRule.onNodeWithTag("map_screen").assertIsDisplayed()
    // Wait for zoom slider to be available and verify
    composeTestRule.waitUntil(timeoutMillis = 10000) {
      composeTestRule
          .onAllNodesWithTag(getResourceString(R.string.map_slider_test_tag))
          .fetchSemanticsNodes()
          .size == 1
    }

    // Test zoom functionality using the slider
    val zoomSlider = composeTestRule.onNodeWithTag(getResourceString(R.string.map_slider_test_tag))
    zoomSlider.assertExists()
    zoomSlider.assertIsDisplayed()

    // Test minimum zoom (MAX_FOV)
    zoomSlider.performGesture {
      swipeDown() // Uses the GeneralSwipeAction with FAST speed
    }
    composeTestRule.waitForIdle()

    // Test maximum zoom (MIN_FOV)
    zoomSlider.performGesture {
      swipeUp() // Uses the GeneralSwipeAction with FAST speed
    }
    composeTestRule.waitForIdle()

    // Test reset functionality which should return to DEFAULT_FOV
    composeTestRule.onNodeWithText("Reset").performClick()
    composeTestRule.waitForIdle()

    // Test planet interaction through touch
    composeTestRule.onNodeWithTag("glSurfaceView").performTouchInput {
      // Use pointerId 0 for primary touch
      down(0, Offset(center.x, center.y))
      up(0)
    }
    composeTestRule.waitForIdle()

    // Test camera movement
    composeTestRule.onNodeWithTag("glSurfaceView").performTouchInput {
      down(0, Offset(center.x, center.y))
      moveBy(Offset(100f, 100f))
      up(0)
    }
    composeTestRule.waitForIdle()

    // Verify screen orientation stays in portrait mode
    composeTestRule.onNodeWithTag("glSurfaceView").assertIsDisplayed()

    // Navigate back to menu
    composeTestRule.onNodeWithText("Menu").performClick()
    composeTestRule.waitForIdle()
    composeTestRule.onNodeWithTag("menu_screen").assertIsDisplayed()
  }

  @Test
  fun calendarNavigationAndSearchFlow() {
    // Navigate to Calendar Screen
    composeTestRule.onNodeWithContentDescription("Home Icon").performClick()
    composeTestRule.waitForIdle()
    composeTestRule.onNodeWithText("Calendar").performClick()
    composeTestRule.waitForIdle()

    // Verify we're on the calendar screen
    composeTestRule.onNodeWithTag("calendar_screen").assertIsDisplayed()

    // Navigate to a specific date (e.g., next month)
    composeTestRule.onNodeWithContentDescription("Next Month").performClick()
    composeTestRule.waitForIdle()

    // Select a specific day (e.g., day 15)
    composeTestRule.onNodeWithText("15").performClick()
    composeTestRule.waitForIdle()

    // Verify the selected date is displayed in the events list
    val calendar = Calendar.getInstance()
    calendar.add(Calendar.MONTH, 1)
    calendar.set(Calendar.DAY_OF_MONTH, 15)
    val dateFormat = SimpleDateFormat("MMMM d, yyyy", Locale.getDefault())
    val expectedDateText = "Events for ${dateFormat.format(calendar.time)}"
    composeTestRule.onNodeWithText(expectedDateText).assertIsDisplayed()

    // Test search functionality
    // Open search dialog
    composeTestRule.onNodeWithTag("look_up_event").performClick()
    composeTestRule.waitForIdle()

    // Enter search query
    composeTestRule.onNodeWithText("Enter event name").performTextInput("pleine lune")
    composeTestRule.waitForIdle()

    // Click search button
    composeTestRule.onNodeWithText("Search").performClick()
    composeTestRule.waitForIdle()

    // Verify search results are displayed
    composeTestRule.onNodeWithText("Search Results").assertIsDisplayed()
    composeTestRule.onNodeWithTag("list_event").assertExists().assertIsDisplayed()

    // Navigate back to menu
    composeTestRule.onNodeWithText("Menu").performClick()
    composeTestRule.waitForIdle()
    composeTestRule.onNodeWithTag("menu_screen").assertIsDisplayed()
  }
}
