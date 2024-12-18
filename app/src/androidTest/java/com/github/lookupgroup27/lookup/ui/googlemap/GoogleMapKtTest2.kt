package com.github.lookupgroup27.lookup.ui.googlemap

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.core.content.ContextCompat
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.lookupgroup27.lookup.model.location.LocationProvider
import com.github.lookupgroup27.lookup.model.post.PostsRepository
import com.github.lookupgroup27.lookup.model.profile.ProfileRepository
import com.github.lookupgroup27.lookup.ui.navigation.NavigationActions
import com.github.lookupgroup27.lookup.ui.navigation.Screen
import com.github.lookupgroup27.lookup.ui.post.PostsViewModel
import com.github.lookupgroup27.lookup.ui.profile.ProfileViewModel
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`

// This test the UI behaviour of the GoogleMap screen before granting the location permission
@RunWith(AndroidJUnit4::class)
class GoogleMapKtTest2 {
  private lateinit var navigationActions: NavigationActions
  private lateinit var locationProvider: LocationProvider
  private lateinit var postsViewModel: PostsViewModel
  private lateinit var postsRepository: PostsRepository
  private lateinit var profileRepository: ProfileRepository
  @Mock private lateinit var profileViewModel: ProfileViewModel

  @get:Rule val composeTestRule = createComposeRule()

  @Before
  fun setUp() {
    val mockContext = mock(Context::class.java)

    // Mock NavigationActions
    navigationActions = mock(NavigationActions::class.java)
    postsRepository = mock(PostsRepository::class.java)
    postsViewModel = PostsViewModel(postsRepository)
    profileRepository = mock(ProfileRepository::class.java)
    profileViewModel = ProfileViewModel(profileRepository)

    // Setup to return the map route as current
    `when`(navigationActions.currentRoute()).thenReturn(Screen.GOOGLE_MAP)
    `when`(ContextCompat.checkSelfPermission(mockContext, Manifest.permission.ACCESS_FINE_LOCATION))
        .thenReturn(PackageManager.PERMISSION_DENIED)

    // Set the Compose content to GoogleMapScreen
    composeTestRule.setContent {
      GoogleMapScreen(navigationActions, postsViewModel, profileViewModel)
    }
  }

  @Test
  fun testEnableLocationButtonIsDisplayed() {
    // Verify the UI elements are displayed
    composeTestRule.onNodeWithTag("background_image").assertExists() // Verify image is displayed
    composeTestRule
        .onNodeWithTag("enable_location_button")
        .assertExists() // Verify button is displayed
    composeTestRule.onNodeWithText("Enable Location").assertExists() // Verify button text
  }
}
