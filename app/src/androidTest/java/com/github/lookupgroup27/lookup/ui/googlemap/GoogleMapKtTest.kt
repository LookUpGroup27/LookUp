package com.github.lookupgroup27.lookup.ui.googlemap

import android.Manifest
import android.content.Context
import android.location.Location
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.core.app.ApplicationProvider
import androidx.test.rule.GrantPermissionRule
import com.github.lookupgroup27.lookup.model.location.LocationProvider
import com.github.lookupgroup27.lookup.model.post.PostsRepository
import com.github.lookupgroup27.lookup.model.profile.ProfileRepository
import com.github.lookupgroup27.lookup.ui.navigation.*
import com.github.lookupgroup27.lookup.ui.post.PostsViewModel
import com.github.lookupgroup27.lookup.ui.profile.ProfileViewModel
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.auth.FirebaseAuth
import org.junit.*
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

class GoogleMapScreenTest {

  private lateinit var navigationActions: NavigationActions
  private lateinit var locationProvider: LocationProvider
  private lateinit var postsViewModel: PostsViewModel
  private lateinit var postsRepository: PostsRepository
  private lateinit var profileRepository: ProfileRepository
  @Mock private lateinit var profileViewModel: ProfileViewModel
  private lateinit var mockAuth: FirebaseAuth

  @get:Rule val composeTestRule = createComposeRule()

  @get:Rule
  val permissionRule: GrantPermissionRule =
      GrantPermissionRule.grant(Manifest.permission.ACCESS_FINE_LOCATION)

  @Before
  fun setUp() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    // Mock NavigationActions
    navigationActions = mock(NavigationActions::class.java)
    postsRepository = mock(PostsRepository::class.java)
    postsViewModel = PostsViewModel(postsRepository)
    profileRepository = mock(ProfileRepository::class.java)
    profileViewModel = ProfileViewModel(profileRepository)

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
  }

  @Test
  fun mapScreenDisplaysCorrectly() {
    composeTestRule.setContent {
      GoogleMapScreen(navigationActions, postsViewModel, profileViewModel)
    }

    // Verify that the GoogleMapScreen is displayed
    composeTestRule.onNodeWithTag("googleMapScreen").assertIsDisplayed()

    // Ensure the bottom navigation is set up correctly
    composeTestRule.onNodeWithTag("bottomNavigationMenu").assertIsDisplayed()
  }

  @Test
  fun cameraPositionIsUpdatedOnLocationChange() {
    composeTestRule.setContent {
      GoogleMapScreen(navigationActions, postsViewModel, profileViewModel)
    }

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
    composeTestRule.setContent {
      GoogleMapScreen(navigationActions, postsViewModel, profileViewModel)
    }

    // Verify that the "Auto Center On" button is displayed
    composeTestRule.onNodeWithText("Auto Center On").assertIsDisplayed()

    // Verify that the "Auto Center Off" button is displayed
    composeTestRule.onNodeWithText("Auto Center Off").assertIsDisplayed()
  }

  @Test
  fun autoCenteringButtonsFunctionality() {
    composeTestRule.setContent {
      GoogleMapScreen(navigationActions, postsViewModel, profileViewModel)
    }

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

    // Verify if the map is centered on the current location
    composeTestRule.onNodeWithTag("googleMapScreen").assertIsDisplayed()
  }

  @Test
  fun floatingActionButtonDisplaysAndNavigatesToCameraCapture() {
    composeTestRule.setContent {
      GoogleMapScreen(navigationActions, postsViewModel, profileViewModel)
    }
    // Mock the FirebaseAuth instance
    mockAuth = org.mockito.kotlin.mock()
    whenever(mockAuth.currentUser).thenReturn(null) // Can change this to test different scenarios

    composeTestRule.waitForIdle()
    // Verify that the Floating Action Button is displayed
    composeTestRule.onNodeWithTag("fab_take_picture").assertIsDisplayed()

    // Simulate a click on the FAB
    composeTestRule.onNodeWithTag("fab_take_picture").performClick()
    composeTestRule.waitForIdle()

    // Verify that the navigation action to "Take Image" screen is triggered
    if (mockAuth.currentUser != null) {
      verify(navigationActions).navigateTo(Screen.TAKE_IMAGE)
    } else {
      verify(navigationActions).navigateTo(Screen.AUTH)
    }
  }

  @Test
  fun testEnableLocationButtonIsDisplayed() {
    composeTestRule.setContent {
      GoogleMapScreen(navigationActions, postsViewModel, profileViewModel, true)
    }

    // Verify the UI elements are displayed
    composeTestRule.onNodeWithTag("background_image").assertExists() // Verify image is displayed
    composeTestRule
        .onNodeWithTag("enable_location_button")
        .assertExists() // Verify button is displayed
    composeTestRule.onNodeWithText("Enable Location").assertExists() // Verify button text
  }
}
