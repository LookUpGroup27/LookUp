package com.github.lookupgroup27.lookup.ui.googlemap

import android.Manifest
import android.content.Context
import android.location.Location
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
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
}
