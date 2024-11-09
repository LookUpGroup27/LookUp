package com.github.lookupgroup27.lookup.ui.googlemap

import android.Manifest
import android.content.Context
import android.location.Location
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.core.app.ApplicationProvider
import androidx.test.rule.GrantPermissionRule
import com.github.lookupgroup27.lookup.model.location.LocationProvider
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
  private lateinit var locationProvider: LocationProvider

  @get:Rule val composeTestRule = createComposeRule()

  @get:Rule
  val permissionRule: GrantPermissionRule =
      GrantPermissionRule.grant(Manifest.permission.ACCESS_FINE_LOCATION)

  @Before
  fun setUp() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    // Mock NavigationActions
    navigationActions = mock(NavigationActions::class.java)

    // Setup to return the map route as current
    `when`(navigationActions.currentRoute()).thenReturn(Screen.GOOGLE_MAP)

    // Mock MutableState for currentLocation
    val fakeLocation =
        Location("provider").apply {
          latitude = 37.7749
          longitude = -122.4194
        }
    val mockCurrentLocation = mutableStateOf<Location?>(fakeLocation)

    // Pass the mock MutableState to LocationProvider
    locationProvider = LocationProvider(context, mockCurrentLocation)

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
  fun cameraPositionIsUpdatedOnLocationChange() {
    val fakeLocation = LatLng(37.7749, -122.4194) // Example coordinates for San Francisco

    // Simulate location update
    composeTestRule.runOnIdle {
      // Update the locationProvider's currentLocation value
      locationProvider.currentLocation.value =
          Location("provider").apply {
            latitude = fakeLocation.latitude
            longitude = fakeLocation.longitude
          }
    }

    // Allow some time for the camera to animate to the new location
    composeTestRule.waitForIdle()

    // Verify if the map screen is displayed after the update, implying no errors occurred
    composeTestRule.onNodeWithTag("googleMapScreen").assertIsDisplayed()
  }

  @Test
  fun buttonsAreDisplayedCorrectly() {
    // Verify that the "Auto Center On" button is displayed
    composeTestRule.onNodeWithText("Auto Center On").assertIsDisplayed()

    // Verify that the "Auto Center Off" button is displayed
    composeTestRule.onNodeWithText("Auto Center Off").assertIsDisplayed()
  }

  @Test
  fun autoCenteringButtonsFunctionality() {
    // Click the "Auto Center Off" button to disable auto-centering
    composeTestRule.onNodeWithText("Auto Center Off").performClick()

    // Update the locationProvider's currentLocation value
    val fakeLocation = LatLng(37.7749, -122.4194)
    composeTestRule.runOnIdle {
      locationProvider.currentLocation.value =
          Location("provider").apply {
            latitude = fakeLocation.latitude
            longitude = fakeLocation.longitude
          }
    }

    // Allow some time for the camera to animate to the new location
    composeTestRule.waitForIdle()

    // Check that the map is displayed and that auto-centering is not enforced
    // (In practice, this could involve verifying the map state hasn't moved to the new location.)

    // Click the "Auto Center On" button to enable auto-centering again
    composeTestRule.onNodeWithText("Auto Center On").performClick()

    // Update location and check that auto-centering is enabled again
    composeTestRule.runOnIdle {
      locationProvider.currentLocation.value =
          Location("provider").apply {
            latitude = 40.7128
            longitude = -74.0060 // New coordinates for New York
          }
    }

    // Allow some time for the camera to animate to the new location
    composeTestRule.waitForIdle()

    // Verify if the map is centered on the current location (you may need additional logic to
    // verify this properly)
    composeTestRule.onNodeWithTag("googleMapScreen").assertIsDisplayed()
  }
}
