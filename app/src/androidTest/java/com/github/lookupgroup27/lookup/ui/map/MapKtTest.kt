package com.github.lookupgroup27.lookup.ui.map

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import com.github.lookupgroup27.lookup.R
import com.github.lookupgroup27.lookup.ui.navigation.LIST_TOP_LEVEL_DESTINATION
import com.github.lookupgroup27.lookup.ui.navigation.NavigationActions
import com.github.lookupgroup27.lookup.ui.navigation.Screen
import com.github.lookupgroup27.lookup.util.NetworkUtils
import io.github.kakaocup.kakao.common.utilities.getResourceString
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkObject
import org.junit.*
import org.mockito.kotlin.*

class MapKtTest {

  @get:Rule val composeTestRule = createComposeRule()

  private val mockNavigationActions: NavigationActions = mock()
  private val mockViewModel: MapViewModel = mockk(relaxed = true)

  @Before
  fun setUp() {
    composeTestRule.setContent {
      MapScreen(
          navigationActions = mockNavigationActions,
          mapViewModel = mockViewModel // Injecting the mocked ViewModel
          )
    }
  }

  @Test
  fun mapScreen_displays_glSurfaceView() {
    // Verify the GL surface view node is displayed
    composeTestRule.onNodeWithTag("glSurfaceView").assertIsDisplayed()
  }

  @Test
  fun menuScreen_displaysBottomNavigationMenu() {
    // Check that the bottom navigation menu is displayed
    composeTestRule.onNodeWithTag("bottomNavigationMenu").assertIsDisplayed()
  }

  @Test
  fun menuScreen_bottomNavigation_clickMenuTab_navigatesToMenu() {
    // Click on the "Menu" tab
    composeTestRule.onNodeWithTag("Menu").performClick()

    val menuDestination = LIST_TOP_LEVEL_DESTINATION.first { it.textId == "Menu" }

    // Verify navigation to "Menu" destination
    verify(mockNavigationActions).navigateTo(menuDestination)
  }

  @Test
  fun mapScreen_bottomNavigation_clickMapTab_doesNotNavigateToMap() {
    // Click on the "Map" tab
    composeTestRule.onNodeWithTag("Map").performClick()

    val mapDestination = LIST_TOP_LEVEL_DESTINATION.first { it.textId == "Map" }

    // Verify that navigation to the Map screen does NOT occur
    verify(mockNavigationActions, never()).navigateTo(mapDestination)
  }

  @Test
  fun mapScreen_displayZoom_components() {
    // Verify the reset button is correctly displayed
    composeTestRule
        .onNodeWithText(getResourceString(R.string.map_button_reset_text))
        .assertIsDisplayed()

    // Verify the zoom slider is correctly displayed
    composeTestRule
        .onNodeWithTag(getResourceString(R.string.map_slider_test_tag))
        .assertIsDisplayed()
  }

  @Test
  fun testNavigationToFeedBlockedForLoggedOutUser() {
    // Simulate offline mode
    mockkObject(NetworkUtils)
    every { NetworkUtils.isNetworkAvailable(any()) } returns false
    // Simulate clicking the Feed tab in the bottom navigation
    composeTestRule.onNodeWithTag("Map").performClick()

    // Wait for the UI to settle after the click
    composeTestRule.waitForIdle()

    // Verify that navigation to the Sky Map is never triggered
    verify(mockNavigationActions, never()).navigateTo(Screen.MAP)
  }
}
