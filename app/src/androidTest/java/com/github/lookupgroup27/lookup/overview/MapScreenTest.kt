package com.github.lookupgroup27.lookup.overview

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import com.github.lookupgroup27.lookup.ui.map.MapScreen
import com.github.lookupgroup27.lookup.ui.navigation.NavigationActions
import org.junit.*
import org.mockito.kotlin.*

class MapScreenTest {

  @get:Rule val composeTestRule = createComposeRule()

  private val mockNavigationActions: NavigationActions = mock()

  @Test
  fun mapScreen_displaysBackgroundImage() {
    composeTestRule.setContent { MapScreen(navigationActions = mockNavigationActions) }

    // Verify the background image is displayed
    composeTestRule.onNodeWithTag("map_background").assertIsDisplayed()
  }

  @Test
  fun mapScreen_clickBackButton_navigatesBack() {
    composeTestRule.setContent { MapScreen(navigationActions = mockNavigationActions) }

    // Perform click on the back button
    composeTestRule.onNodeWithTag("go_back_button").performClick()

    // Verify navigation back action is triggered
    verify(mockNavigationActions).goBack()
  }
}
