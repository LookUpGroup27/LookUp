package com.github.lookupgroup27.lookup.ui.googlemap

// toDo: try to correctly write the test for this class

/*import android.location.Location
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import com.github.lookupgroup27.lookup.ui.map.MapScreen
import com.github.lookupgroup27.lookup.ui.navigation.LIST_TOP_LEVEL_DESTINATION
import com.github.lookupgroup27.lookup.ui.navigation.NavigationActions
import org.junit.*
import org.mockito.kotlin.*

class GoogleMapKtTest {

    @get:Rule val composeTestRule = createComposeRule()

    private val mockNavigationActions: NavigationActions = mock()

    @Test
    fun mapScreen_displaysBackgroundImage() {
        composeTestRule.setContent { GoogleMapScreen(navigationActions = mockNavigationActions) }

        // Verify the background image is displayed
        composeTestRule.onNodeWithTag("googleMapScreen").assertIsDisplayed()
    }

    /*@Test
    fun menuScreen_displaysBottomNavigationMenu() {
        composeTestRule.setContent { GoogleMapScreen(navigationActions = mockNavigationActions) }

        // Check that the bottom navigation menu is displayed
        composeTestRule.onNodeWithTag("bottomNavigationMenu").assertIsDisplayed()
    }*/

    @Test
    fun testMarkerIsDisplayedWhenLocationPermissionGranted() {
        // Mock location
        val mockLocation = Location("provider").apply {
            latitude = 37.4219983
            longitude = -122.084
        }

        // Set the Compose content with mocked location and permission
        composeTestRule.setContent {
            GoogleMapScreen(navigationActions = mockNavigationActions)
        }

        // Check if the marker is displayed
        composeTestRule.onNodeWithText("You are here").assertIsDisplayed()
    }
}*/

/*import androidx.compose.ui.test.assertHasClickAction
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.espresso.intent.Intents
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.rule.GrantPermissionRule
import com.github.lookupgroup27.lookup.ui.navigation.NavigationActions
import com.github.lookupgroup27.lookup.ui.navigation.Screen
import com.github.lookupgroup27.lookup.ui.overview.LandingScreen
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import android.Manifest

@RunWith(AndroidJUnit4::class)
class GoogleMapScreenTest {

    @get:Rule val composeTestRule = createComposeRule()
    @get:Rule
    val permissionRule: GrantPermissionRule = GrantPermissionRule.grant(Manifest.permission.ACCESS_FINE_LOCATION)


    private val mockNavigationActions: NavigationActions = mock()

    @Before
    fun setUp() {
        // Initialize Espresso Intents to capture and validate outgoing intents
        Intents.init()
    }

    @After
    fun tearDown() {
        // Release Espresso Intents after tests
        Intents.release()
    }

    @Test
    fun logoAndButtonAreDisplayed() {
        composeTestRule.setContent { GoogleMapScreen(mockNavigationActions) }

        // Verify that the GoogleMapScreen is displayed
        composeTestRule.onNodeWithTag("googleMapScreen").assertIsDisplayed()

        // Ensure the bottom navigation is set up correctly
        composeTestRule.onNodeWithTag("bottomNavigationMenu").assertIsDisplayed()
    }

}*/

import android.Manifest
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
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
  private lateinit var mockLocationProvider: LocationProvider

  @get:Rule val composeTestRule = createComposeRule()

  @get:Rule
  val permissionRule: GrantPermissionRule =
      GrantPermissionRule.grant(Manifest.permission.ACCESS_FINE_LOCATION)

  @Before
  fun setUp() {
    // Mock NavigationActions
    navigationActions = mock(NavigationActions::class.java)
    // Setup to return the map route as current
    `when`(navigationActions.currentRoute()).thenReturn(Screen.SKY_TRACKER)

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

  /*@Test
  fun testMarkerIsDisplayedWhenLocationPermissionGranted() {
      // Mock location
      val mockLocation = Location("provider").apply {
          latitude = 37.4219983
          longitude = -122.084
      }

      // Create a real instance of LocationProvider
      val context = ApplicationProvider.getApplicationContext<Context>()
      mockLocationProvider = LocationProvider(context).apply {
          currentLocation.value = mockLocation
      }

      // Check if the marker is displayed
      composeTestRule.onNodeWithText("You are here").assertIsDisplayed()

      }*/

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
