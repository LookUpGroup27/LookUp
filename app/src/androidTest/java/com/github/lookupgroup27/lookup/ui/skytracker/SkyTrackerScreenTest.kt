package com.github.lookupgroup27.lookup.ui.skytracker

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.github.lookupgroup27.lookup.ui.navigation.NavigationActions
import org.junit.Rule
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify

class SkyTrackerScreenTest {

  @get:Rule val composeTestRule = createComposeRule()

  private val mockNavigationActions: NavigationActions = mock()

  @Test
  fun skyTrackerScreen_displaysSkyTrackerText() {
    composeTestRule.setContent { SkyTrackerScreen(navigationActions = mockNavigationActions) }

    // Verify that the "Sky Tracker Screen" text is displayed
    composeTestRule.onNodeWithText("Sky Tracker Screen").assertIsDisplayed()
  }

  @Test
  fun skyTrackerScreen_clickBackButton_navigatesBack() {
    composeTestRule.setContent { SkyTrackerScreen(navigationActions = mockNavigationActions) }

    // Perform click on the back button
    composeTestRule.onNodeWithTag("go_back_button_skyTracker").performClick()

    // Verify navigation back action is triggered
    verify(mockNavigationActions).goBack()
  }
}

