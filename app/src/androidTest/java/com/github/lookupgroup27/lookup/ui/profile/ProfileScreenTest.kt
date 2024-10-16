package com.github.lookupgroup27.lookup.ui.profile

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.lookupgroup27.lookup.ui.navigation.LIST_TOP_LEVEL_DESTINATION
import com.github.lookupgroup27.lookup.ui.navigation.NavigationActions
import com.github.lookupgroup27.lookup.ui.navigation.Screen
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito
import org.mockito.kotlin.mock

@RunWith(AndroidJUnit4::class)
class ProfileScreenTest {

  @get:Rule val composeTestRule = createComposeRule()

  private lateinit var mockNavigationActions: NavigationActions

  @Before
  fun setup() {
    // Mock the NavigationActions
    mockNavigationActions = mock()

    // Mock currentRoute to return a valid route, to avoid passing a null value
    Mockito.`when`(mockNavigationActions.currentRoute())
        .thenReturn(Screen.PROFILE) // Provide a valid route
  }

  @Test
  fun testProfileScreenRendersCorrectly() {
    // Launch the ProfileScreen composable
    composeTestRule.setContent { ProfileScreen(navigationActions = mockNavigationActions) }

    // Verify if the profile icon is displayed
    composeTestRule.onNodeWithContentDescription("Profile Icon").assertExists()

    // Verify if the Personal Info button is displayed
    composeTestRule.onNodeWithText("Personal Info").assertExists()

    // Verify if the Your Collection button is displayed
    composeTestRule.onNodeWithText("Your Collection").assertExists()

    // Verify if the Bottom Navigation is displayed with proper tabs
    LIST_TOP_LEVEL_DESTINATION.forEach { destination ->
      composeTestRule.onNodeWithText(destination.textId).assertExists()
    }
  }

  @Test
  fun testPersonalInfoButtonClickNavigatesToProfile() {
    composeTestRule.setContent { ProfileScreen(navigationActions = mockNavigationActions) }

    // Click the "Personal Info" button
    composeTestRule.onNodeWithText("Personal Info").performClick()

    // Verify that the navigation to the Profile screen happens
    Mockito.verify(mockNavigationActions).navigateTo(Screen.PROFILE)
  }

  @Test
  fun testCollectionButtonClickNavigatesToCollection() {
    composeTestRule.setContent { ProfileScreen(navigationActions = mockNavigationActions) }

    // Click the "Your Collection" button
    composeTestRule.onNodeWithText("Your Collection").performClick()

    // Verify that the navigation to the Collection screen happens
    Mockito.verify(mockNavigationActions).navigateTo(Screen.COLLECTION)
  }

  @Test
  fun testBottomNavigationItemClick() {
    composeTestRule.setContent { ProfileScreen(navigationActions = mockNavigationActions) }

    // Get the first item in the bottom navigation
    val firstTab = LIST_TOP_LEVEL_DESTINATION.first()

    // Assert that the node exists and is displayed before performing the click
    composeTestRule.onNodeWithText(firstTab.textId).assertExists()
    composeTestRule.onNodeWithText(firstTab.textId).assertIsDisplayed()

    // Perform the click on the first item in the bottom navigation
    composeTestRule.onNodeWithText(firstTab.textId).performClick()

    // Verify that the corresponding navigation action is triggered
    Mockito.verify(mockNavigationActions).navigateTo(firstTab)
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
  fun testProfileScreenPreviewRendersCorrectly() {
    composeTestRule.setContent { ProfileScreenPreview() }

    // Verify if the profile icon is displayed in the preview
    composeTestRule.onNodeWithContentDescription("Profile Icon").assertExists()

    // Verify if the Personal Info button is displayed in the preview
    composeTestRule.onNodeWithText("Personal Info").assertExists()

    // Verify if the Your Collection button is displayed in the preview
    composeTestRule.onNodeWithText("Your Collection").assertExists()

    // Verify if the Bottom Navigation tabs are displayed in the preview
    composeTestRule.onNodeWithText("Map").assertExists()
    composeTestRule.onNodeWithText("Menu").assertExists()
  }
}
