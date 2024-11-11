package com.github.lookupgroup27.lookup.ui.map

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import com.github.lookupgroup27.lookup.ui.navigation.LIST_TOP_LEVEL_DESTINATION
import com.github.lookupgroup27.lookup.ui.navigation.NavigationActions
import org.junit.*
import org.mockito.kotlin.*

class MapKtTest {

  @get:Rule val composeTestRule = createComposeRule()

  private val mockNavigationActions: NavigationActions = mock()

  @Before
  fun setUp() {
    composeTestRule.setContent { MapScreen(navigationActions = mockNavigationActions) }
  }

  @Test
  fun mapScreen_displays_glSurfaceView() {
    // Verify the background image is displayed
    composeTestRule.onNodeWithTag("glSurfaceView").assertIsDisplayed()
  }

  @Test
  fun menuScreen_displaysBottomNavigationMenu() {
    // Check that the bottom navigation menu is displayed
    composeTestRule.onNodeWithTag("bottomNavigationMenu").assertIsDisplayed()
  }

  @Test
  fun menuScreen_bottomNavigation_clickMenuTab_navigatesToMenu() {
    // Click on the Menu tab
    composeTestRule.onNodeWithTag("Menu").performClick()
    // Find the correct TopLevelDestination for "Map"
    val menuDestination = LIST_TOP_LEVEL_DESTINATION.first { it.textId == "Menu" }

    // Verify that navigation to the Map screen is triggered with the correct object
    verify(mockNavigationActions).navigateTo(menuDestination)
  }

  @Test
  fun mapScreen_bottomNavigation_clickMapTab_doesNotNavigateToMap() {
    // Click on the Map tab
    composeTestRule.onNodeWithTag("Map").performClick()

    val mapDestination = LIST_TOP_LEVEL_DESTINATION.first { it.textId == "Map" }

    // Verify that navigation to the Map screen is not triggered
    verify(mockNavigationActions, never()).navigateTo(mapDestination)
  }
}
