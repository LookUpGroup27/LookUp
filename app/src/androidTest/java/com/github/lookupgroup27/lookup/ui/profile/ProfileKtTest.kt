package com.github.lookupgroup27.lookup.ui.profile

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.uiautomator.UiDevice
import com.github.lookupgroup27.lookup.ui.navigation.LIST_TOP_LEVEL_DESTINATION
import com.github.lookupgroup27.lookup.ui.navigation.NavigationActions
import com.github.lookupgroup27.lookup.ui.navigation.Screen
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito
import org.mockito.Mockito.mock

@RunWith(AndroidJUnit4::class)
class ProfileKtTest {

  @get:Rule val composeTestRule = createComposeRule()

  private lateinit var mockNavigationActions: NavigationActions

  @Before
  fun setup() {
    // Initialize the mock object
    mockNavigationActions = mock(NavigationActions::class.java)

    // Complete the stubbing correctly by using `thenReturn`
    Mockito.`when`(mockNavigationActions.currentRoute()).thenReturn(Screen.PROFILE_INFORMATION)
  }

  @Test
  fun testProfileScreenRendersCorrectly() {
    // Launch the ProfileScreen composable
    composeTestRule.setContent { ProfileScreen(navigationActions = mockNavigationActions) }

    // Verify if the profile icon is displayed
    composeTestRule.onNodeWithContentDescription("Profile Icon").assertExists()

    // Verify if the Personal Info button is displayed
    composeTestRule.onNodeWithText("Personal Info     >").assertExists()

    // Verify if the Your Collection button is displayed
    composeTestRule.onNodeWithText("Your Collection   >").assertExists()

    // Verify if the Bottom Navigation is displayed with proper tabs
    LIST_TOP_LEVEL_DESTINATION.forEach { destination ->
      composeTestRule.onNodeWithText(destination.textId).assertExists()
    }
  }

  @Test
  fun testPersonalInfoButtonClickNavigatesToProfileInformation() {
    composeTestRule.setContent { ProfileScreen(navigationActions = mockNavigationActions) }

    // Ensure UI is fully rendered
    composeTestRule.waitForIdle()

    // Click the "Personal Info" button
    composeTestRule.onNodeWithText("Personal Info     >").performClick()

    // Verify that the navigation to the Profile screen happens
    Mockito.verify(mockNavigationActions).navigateTo(Screen.PROFILE_INFORMATION)
  }

  @Test
  fun testCollectionButtonClickNavigatesToCollection() {
    composeTestRule.setContent { ProfileScreen(navigationActions = mockNavigationActions) }

    // Click the "Your Collection" button
    composeTestRule.onNodeWithText("Your Collection   >").performClick()

    // Verify that the navigation to the Collection screen happens
    Mockito.verify(mockNavigationActions).navigateTo(Screen.COLLECTION)
  }

  @Test
  fun testBottomNavigationHandlesEmptyRoute() {
    // Mock currentRoute to return an empty string (indicating no screen is selected)
    Mockito.`when`(mockNavigationActions.currentRoute()).thenReturn("")

    composeTestRule.setContent { ProfileScreen(navigationActions = mockNavigationActions) }

    // Ensure that the "Map" and "Menu" tabs are still displayed even if the route is empty
    composeTestRule.onNodeWithText("Map").assertExists()
    composeTestRule.onNodeWithText("Menu").assertExists()

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
    composeTestRule.setContent { ProfileScreen(navigationActions = mockNavigationActions) }

    // Check that main elements are displayed after scrolling in landscape mode
    composeTestRule.onNodeWithContentDescription("Profile Icon").assertExists()
    composeTestRule.onNodeWithText("Personal Info     >").performScrollTo().assertExists()
    composeTestRule.onNodeWithText("Your Collection   >").performScrollTo().assertExists()

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

  @Test
  fun testProfileScreenPreviewRendersCorrectly() {
    composeTestRule.setContent { ProfileScreenPreview() }

    // Verify if the profile icon is displayed in the preview
    composeTestRule.onNodeWithContentDescription("Profile Icon").assertExists()

    // Verify if the Personal Info button is displayed in the preview
    composeTestRule.onNodeWithText("Personal Info     >").assertExists()

    // Verify if the Your Collection button is displayed in the preview
    composeTestRule.onNodeWithText("Your Collection   >").assertExists()

    // Verify if the Bottom Navigation tabs are displayed in the preview
    composeTestRule.onNodeWithText("Map").assertExists()
    composeTestRule.onNodeWithText("Menu").assertExists()
  }
}
