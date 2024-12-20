package com.github.lookupgroup27.lookup.ui.profile

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.uiautomator.UiDevice
import com.github.lookupgroup27.lookup.R
import com.github.lookupgroup27.lookup.model.profile.ProfileRepository
import com.github.lookupgroup27.lookup.ui.navigation.LIST_TOP_LEVEL_DESTINATION
import com.github.lookupgroup27.lookup.ui.navigation.NavigationActions
import com.github.lookupgroup27.lookup.ui.navigation.Screen
import com.github.lookupgroup27.lookup.ui.profile.profilepic.AvatarViewModel
import io.github.kakaocup.kakao.common.utilities.getResourceString
import kotlinx.coroutines.flow.MutableStateFlow
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito
import org.mockito.Mockito.mock
import org.mockito.kotlin.any
import org.mockito.kotlin.whenever

@RunWith(AndroidJUnit4::class)
class ProfileKtTest {

  @get:Rule val composeTestRule = createComposeRule()

  private lateinit var mockNavigationActions: NavigationActions
  private lateinit var mockAvatarViewModel: AvatarViewModel
  private val profileRepository: ProfileRepository = org.mockito.kotlin.mock()

  @Before
  fun setup() {
    // Initialize the mock object
    mockNavigationActions = mock(NavigationActions::class.java)

    mockAvatarViewModel = AvatarViewModel(profileRepository)

    // Complete the stubbing correctly by using `thenReturn`
    Mockito.`when`(mockNavigationActions.currentRoute()).thenReturn(Screen.PROFILE_INFORMATION)
  }

  @Test
  fun testProfileScreenRendersCorrectly() {
    // Launch the ProfileScreen composable
    composeTestRule.setContent {
      ProfileScreen(
          navigationActions = mockNavigationActions, avatarViewModel = mockAvatarViewModel)
    }

    // Verify if the profile icon is displayed
    composeTestRule
        .onNodeWithContentDescription(getResourceString(R.string.profile_profile_icon_description))
        .assertExists()

    // Verify if the Personal Info button is displayed
    composeTestRule
        .onNodeWithText(getResourceString(R.string.profile_collection_button))
        .assertExists()

    // Verify if the Your Collection button is displayed
    composeTestRule
        .onNodeWithText(getResourceString(R.string.profile_personal_info_button))
        .assertExists()

    // Verify if the Bottom Navigation is displayed with proper tabs
    LIST_TOP_LEVEL_DESTINATION.forEach { destination ->
      composeTestRule.onNodeWithText(destination.textId).assertExists()
    }
  }

  @Test
  fun testProfileScreenDisplaysAvatarCorrectly() {
    // Create a real MutableStateFlow for mocking
    val selectedAvatarFlow = MutableStateFlow<Int?>(R.drawable.avatar1)

    // Mock the repository to return the desired StateFlow
    val mockRepository = mock<ProfileRepository>()
    whenever(mockRepository.getSelectedAvatar(any(), any(), any())).thenAnswer {
      val onSuccess = it.getArgument<(Int?) -> Unit>(1)
      onSuccess(R.drawable.avatar1)
    }

    // Use a real AvatarViewModel with the mocked repository
    val avatarViewModel = AvatarViewModel(mockRepository)

    composeTestRule.setContent {
      ProfileScreen(navigationActions = mockNavigationActions, avatarViewModel = avatarViewModel)
    }

    // Verify that the correct avatar is displayed
    composeTestRule
        .onNodeWithContentDescription(getResourceString(R.string.profile_profile_icon_description))
        .assertExists()
  }

  @Test
  fun testFloatingActionButtonDisplaysWhenNoAvatarIsSelected() {
    val mockRepository = mock<ProfileRepository>()
    whenever(mockRepository.getSelectedAvatar(any(), any(), any())).thenAnswer {
      val onSuccess = it.getArgument<(Int?) -> Unit>(1)
      onSuccess(null) // Simulate no avatar selected
    }

    val avatarViewModel = AvatarViewModel(mockRepository)

    composeTestRule.setContent {
      ProfileScreen(navigationActions = mockNavigationActions, avatarViewModel = avatarViewModel)
    }

    // Verify that the FAB is displayed
    composeTestRule
        .onNodeWithContentDescription(getResourceString(R.string.profile_add_avatar))
        .assertExists()

    // Click the FAB to navigate to the Avatar Selection Screen
    composeTestRule
        .onNodeWithContentDescription(getResourceString(R.string.profile_add_avatar))
        .performClick()

    // Verify that navigation to the Avatar Selection Screen occurred
    Mockito.verify(mockNavigationActions).navigateTo(Screen.AVATAR_SELECTION)
  }

  @Test
  fun testPersonalInfoButtonClickNavigatesToProfileInformation() {
    composeTestRule.setContent {
      ProfileScreen(
          navigationActions = mockNavigationActions, avatarViewModel = mockAvatarViewModel)
    }

    // Ensure UI is fully rendered
    composeTestRule.waitForIdle()

    // Click the "Personal Info" button
    composeTestRule
        .onNodeWithText(getResourceString(R.string.profile_personal_info_button))
        .performClick()

    // Verify that the navigation to the Profile screen happens
    Mockito.verify(mockNavigationActions).navigateTo(Screen.PROFILE_INFORMATION)
  }

  @Test
  fun testCollectionButtonClickNavigatesToCollection() {
    composeTestRule.setContent {
      ProfileScreen(
          navigationActions = mockNavigationActions, avatarViewModel = mockAvatarViewModel)
    }

    // Click the "Your Collection" button
    composeTestRule
        .onNodeWithText(getResourceString(R.string.profile_collection_button))
        .performClick()

    // Verify that the navigation to the Collection screen happens
    Mockito.verify(mockNavigationActions).navigateTo(Screen.COLLECTION)
  }

  @Test
  fun testBottomNavigationHandlesEmptyRoute() {
    // Mock currentRoute to return an empty string (indicating no screen is selected)
    Mockito.`when`(mockNavigationActions.currentRoute()).thenReturn("")

    composeTestRule.setContent {
      ProfileScreen(
          navigationActions = mockNavigationActions, avatarViewModel = mockAvatarViewModel)
    }

    // Ensure that the "Map" and "Menu" tabs are still displayed even if the route is empty
    composeTestRule.onNodeWithText(getResourceString(R.string.sky_map)).assertExists()
    composeTestRule.onNodeWithText(getResourceString(R.string.menu)).assertExists()

    // Verify that currentRoute() was called
    Mockito.verify(mockNavigationActions).currentRoute()

    // Verify that no further interactions (including `navigateTo()`) occurred
    Mockito.verifyNoMoreInteractions(mockNavigationActions)
  }

  @Test
  fun testProfileScreenIsScrollableAndFullyVisibleInLandscape() {
    // Set the device to landscape orientation
    setLandscapeOrientation()

    // Launch the ProfileScreen in landscape mode
    composeTestRule.setContent {
      ProfileScreen(
          navigationActions = mockNavigationActions, avatarViewModel = mockAvatarViewModel)
    }

    // Check that main elements are displayed after scrolling in landscape mode
    composeTestRule
        .onNodeWithContentDescription(getResourceString(R.string.profile_profile_icon_description))
        .assertExists()
    composeTestRule
        .onNodeWithText(getResourceString(R.string.profile_personal_info_button))
        .performScrollTo()
        .assertExists()
    composeTestRule
        .onNodeWithText(getResourceString(R.string.profile_collection_button))
        .performScrollTo()
        .assertExists()

    // Reset orientation to portrait after the test
    resetOrientation()
  }

  private fun setLandscapeOrientation() {
    val device = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation())
    device.setOrientationLeft()
  }

  private fun resetOrientation() {
    val device = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation())
    device.setOrientationNatural()
  }
}
