package com.github.lookupgroup27.lookup.ui.overview

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import com.github.lookupgroup27.lookup.model.profile.ProfileRepository
import com.github.lookupgroup27.lookup.ui.navigation.*
import com.github.lookupgroup27.lookup.ui.profile.profilepic.AvatarViewModel
import com.google.firebase.auth.FirebaseAuth
import org.junit.*
import org.mockito.kotlin.*

class MenuKtTest {

  @get:Rule val composeTestRule = createComposeRule()

  private val mockNavigationActions: NavigationActions = mock()

  private lateinit var mockAuth: FirebaseAuth

  private lateinit var mockAvatarViewModel: AvatarViewModel

  private val mockProfileRepository: ProfileRepository = mock()

  @Before
  fun setup() {
    mockAvatarViewModel = AvatarViewModel(mockProfileRepository)

    // Stub currentRoute for navigation
    whenever(mockNavigationActions.currentRoute()).thenReturn("")
  }

  @Test
  fun menuScreen_displaysBottomNavigationMenu() {
    composeTestRule.setContent {
      MenuScreen(navigationActions = mockNavigationActions, mockAvatarViewModel)
    }

    // Check that the bottom navigation menu is displayed
    composeTestRule.onNodeWithTag("bottomNavigationMenu").assertIsDisplayed()
  }

  @Test
  fun menuScreen_bottomNavigation_clickMenuTab_navigatesToMenu() {
    composeTestRule.setContent {
      MenuScreen(navigationActions = mockNavigationActions, mockAvatarViewModel)
    }

    // Click on the Menu tab
    composeTestRule.onNodeWithTag("Menu").performClick()
    // Find the correct TopLevelDestination for "Map"
    val menuDestination = LIST_TOP_LEVEL_DESTINATION.first { it.textId == "Menu" }

    // Verify that navigation to the Map screen is triggered with the correct object
    verify(mockNavigationActions, never()).navigateTo(menuDestination)
  }

  @Test
  fun menuScreen_bottomNavigation_clickMapTab_navigatesToMap() {
    composeTestRule.setContent {
      MenuScreen(navigationActions = mockNavigationActions, mockAvatarViewModel)
    }

    // Click on the Map tab
    composeTestRule.onNodeWithTag("Map").performClick()

    val mapDestination = LIST_TOP_LEVEL_DESTINATION.first { it.textId == "Map" }

    // Verify that navigation to the Map screen is triggered with the correct object
    verify(mockNavigationActions).navigateTo(mapDestination)
  }

  @Test
  fun menuScreen_displaysAppLogo() {
    composeTestRule.setContent {
      MenuScreen(navigationActions = mockNavigationActions, mockAvatarViewModel)
    }

    // Check that the app logo is displayed
    composeTestRule.onNodeWithTag("app_logo").assertIsDisplayed()
  }

  @Test
  fun menuScreen_displaysAllButtons() {
    composeTestRule.setContent {
      MenuScreen(navigationActions = mockNavigationActions, mockAvatarViewModel)
    }

    // Check that the background is displayed
    composeTestRule.onNodeWithTag("background_image").assertIsDisplayed()
    // Check that all buttons are displayed
    composeTestRule.onNodeWithText("Quizzes").assertIsDisplayed()
    composeTestRule.onNodeWithText("Calendar").assertIsDisplayed()
    composeTestRule.onNodeWithText("Google Map").assertIsDisplayed()
    composeTestRule.onNodeWithTag("profile_button").assertIsDisplayed()
  }

  @Test
  fun menuScreen_clickProfileButton_navigatesToCorrectPlace() {
    // Mock the FirebaseAuth instance
    mockAuth = mock()
    whenever(mockAuth.currentUser).thenReturn(null) // Change this to test different scenarios

    composeTestRule.setContent {
      MenuScreen(navigationActions = mockNavigationActions, mockAvatarViewModel)
    }

    // Click on profile button
    composeTestRule.onNodeWithTag("profile_button").performClick()

    // Verify navigation to Profile screen is triggered
    if (mockAuth.currentUser != null) {
      verify(mockNavigationActions).navigateTo(Screen.PROFILE)
    } else {
      verify(mockNavigationActions).navigateTo(Screen.AUTH)
    }
  }

  @Test
  fun menuScreen_clickQuizzes_navigatesToQuizScreen() {
    composeTestRule.setContent {
      MenuScreen(navigationActions = mockNavigationActions, mockAvatarViewModel)
    }

    // Perform click on "Quizzes" button
    composeTestRule.onNodeWithText("Quizzes").performClick()

    // Verify navigation to Quiz screen is triggered
    verify(mockNavigationActions).navigateTo(Screen.QUIZ)
  }

  @Test
  fun menuScreen_clickCalendar_navigatesToCalendarScreen() {
    composeTestRule.setContent {
      MenuScreen(navigationActions = mockNavigationActions, mockAvatarViewModel)
    }

    // Perform click on "Calendar" button
    composeTestRule.onNodeWithText("Calendar").performClick()

    // Verify navigation to Calendar screen is triggered
    verify(mockNavigationActions).navigateTo(Screen.CALENDAR)
  }

  @Test
  fun menuScreen_clickGoogleMap_navigatesToGoogleMapScreen() {
    composeTestRule.setContent {
      MenuScreen(navigationActions = mockNavigationActions, mockAvatarViewModel)
    }

    // Perform click on "Google Map" button
    composeTestRule.onNodeWithText("Google Map").performClick()

    // Verify navigation to Google Map screen is triggered
    verify(mockNavigationActions).navigateTo(Screen.GOOGLE_MAP)
  }
}
