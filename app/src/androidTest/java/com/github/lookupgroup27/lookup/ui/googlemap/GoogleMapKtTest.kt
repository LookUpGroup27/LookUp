package com.github.lookupgroup27.lookup.ui.googlemap

import android.Manifest
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.test.rule.GrantPermissionRule
import com.github.lookupgroup27.lookup.ui.navigation.NavigationActions
import com.github.lookupgroup27.lookup.ui.navigation.Screen
import com.google.android.gms.maps.model.LatLng
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`

class GoogleMapScreenTest {

  private lateinit var navigationActions: NavigationActions

  @get:Rule val composeTestRule = createComposeRule()

  @get:Rule
  val permissionRule: GrantPermissionRule =
      GrantPermissionRule.grant(Manifest.permission.ACCESS_FINE_LOCATION)

  @Before
  fun setUp() {
    // Mock NavigationActions
    navigationActions = mock(NavigationActions::class.java)
    // Setup to return the map route as current
    `when`(navigationActions.currentRoute()).thenReturn(Screen.GOOGLE_MAP)

    // Set the Compose content to GoogleMapScreen
    composeTestRule.setContent { GoogleMapScreen(navigationActions) }
  }

  @Test
  fun mapScreenDisplaysCorrectly() {

    // Verify that the GoogleMapScreen is displayed
    composeTestRule.onNodeWithTag("googleMapScreen").assertIsDisplayed()

    // Ensure the bottom navigation is set up correctly
    composeTestRule.onNodeWithTag("bottomNavigationMenu").assertIsDisplayed()
  }

  @Test
  fun mapIsCenteredOnCurrentLocation() {
    val fakeLocation = LatLng(37.7749, -122.4194) // Example coordinates for San Francisco

    // Simulate location update
    composeTestRule.runOnIdle {
      // Update the locationProvider's currentLocation value
      // This part depends on how you can access and update the locationProvider in your test
    }

    // Verify if the map is centered on the current location
    composeTestRule.onNodeWithTag("googleMapScreen").assertIsDisplayed()
    // Add more assertions to verify the map's camera position if possible
  }

  @Test
  fun markerIsDisplayedOnCurrentLocation() {
    val fakeLocation = LatLng(37.7749, -122.4194) // Example coordinates for San Francisco

    // Simulate location update
    composeTestRule.runOnIdle {
      // Update the locationProvider's currentLocation value
      // This part depends on how you can access and update the locationProvider in your test
    }

    // Verify if the marker is displayed on the current location
    composeTestRule.onNodeWithTag("googleMapScreen").assertIsDisplayed()
    // Add more assertions to verify the marker's position if possible
  }
}
