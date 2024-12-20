package com.github.lookupgroup27.lookup.ui.profile

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import com.github.lookupgroup27.lookup.model.profile.ProfileRepository
import com.github.lookupgroup27.lookup.model.profile.UserProfile
import com.github.lookupgroup27.lookup.ui.navigation.NavigationActions
import com.github.lookupgroup27.lookup.ui.navigation.Screen
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.kotlin.*

@OptIn(ExperimentalCoroutinesApi::class)
class ProfileInformationScreenTest {

  private lateinit var profileRepository: ProfileRepository
  private lateinit var profileViewModel: ProfileViewModel
  private lateinit var navigationActions: NavigationActions
  private lateinit var firebaseAuth: FirebaseAuth

  @get:Rule val composeTestRule = createComposeRule()

  @Before
  fun setUp() {
    profileRepository = mock()
    profileViewModel = ProfileViewModel(profileRepository)
    navigationActions = mock()
    firebaseAuth = mock()

    whenever(navigationActions.currentRoute()).thenReturn(Screen.PROFILE_INFORMATION)

    // Common stubs for all tests:
    // init always calls onSuccess immediately
    whenever(profileRepository.init(any())).thenAnswer { it.getArgument<() -> Unit>(0).invoke() }
    // Checking username always returns false (not taken)
    whenever(profileRepository.isUsernameTaken(any(), any(), any())).thenAnswer {
      val onResult = it.getArgument<(Boolean) -> Unit>(1)
      onResult(false)
    }
    // updateUserProfile and deleteUserProfile always succeed
    whenever(profileRepository.updateUserProfile(any(), any(), any())).thenAnswer {
      it.getArgument<() -> Unit>(1).invoke()
    }
    whenever(profileRepository.deleteUserProfile(any(), any(), any())).thenAnswer {
      it.getArgument<() -> Unit>(1).invoke()
    }
  }

  @Test
  fun displayAllComponents() {
    // No profile needed here, just return null
    whenever(profileRepository.getUserProfile(any(), any())).thenAnswer {
      it.getArgument<(UserProfile?) -> Unit>(0).invoke(null)
    }

    composeTestRule.setContent { ProfileInformationScreen(profileViewModel, navigationActions) }

    composeTestRule.onNodeWithTag("editProfileScreen").assertIsDisplayed()
    composeTestRule.onNodeWithTag("editProfileTitle").assertIsDisplayed()
    composeTestRule.onNodeWithTag("goBackButton").assertIsDisplayed()

    composeTestRule.onNodeWithTag("editProfileUsername").performScrollTo().assertIsDisplayed()
    composeTestRule.onNodeWithTag("editProfileEmail").performScrollTo().assertIsDisplayed()
    composeTestRule.onNodeWithTag("editProfileBio").performScrollTo().assertIsDisplayed()
    composeTestRule.onNodeWithTag("profileSaveButton").performScrollTo().assertIsDisplayed()
    composeTestRule.onNodeWithTag("profileLogout").performScrollTo().assertIsDisplayed()

    // Check button texts
    composeTestRule.onNodeWithTag("profileSaveButton").assertTextEquals("Save")
    composeTestRule.onNodeWithTag("profileLogout").assertTextEquals("Sign out")
  }

  @Test
  fun saveButtonDisabledWhenFieldsAreEmpty() {
    whenever(profileRepository.getUserProfile(any(), any())).thenAnswer {
      it.getArgument<(UserProfile?) -> Unit>(0).invoke(null)
    }

    composeTestRule.setContent { ProfileInformationScreen(profileViewModel, navigationActions) }

    // Initially empty, save should be disabled
    composeTestRule.onNodeWithTag("profileSaveButton").assertIsNotEnabled()

    // Fill all fields
    composeTestRule.onNodeWithTag("editProfileUsername").performTextInput("JohnDoe")
    composeTestRule.onNodeWithTag("editProfileEmail").performTextInput("john.doe@example.com")
    composeTestRule.onNodeWithTag("editProfileBio").performTextInput("This is a bio")

    // Now enabled
    composeTestRule.onNodeWithTag("profileSaveButton").assertIsEnabled()
  }

  @Test
  fun logoutButtonWorks() {
    whenever(profileRepository.getUserProfile(any(), any())).thenAnswer {
      it.getArgument<(UserProfile?) -> Unit>(0).invoke(null)
    }

    composeTestRule.setContent { ProfileInformationScreen(profileViewModel, navigationActions) }

    composeTestRule.onNodeWithTag("profileLogout").performScrollTo().performClick()
    composeTestRule.waitForIdle()

    verify(navigationActions).navigateTo(Screen.LANDING)
  }

  @Test
  fun saveButtonWorks() {
    // Start with no profile
    whenever(profileRepository.getUserProfile(any(), any())).thenAnswer {
      it.getArgument<(UserProfile?) -> Unit>(0).invoke(null)
    }

    composeTestRule.setContent { ProfileInformationScreen(profileViewModel, navigationActions) }

    // Fill fields
    composeTestRule.onNodeWithTag("editProfileUsername").performTextInput("JohnDoe")
    composeTestRule.onNodeWithTag("editProfileEmail").performTextInput("john.doe@example.com")
    composeTestRule.onNodeWithTag("editProfileBio").performTextInput("This is a bio")

    // Click Save
    composeTestRule.onNodeWithTag("profileSaveButton").performClick()
    composeTestRule.waitForIdle()

    // Verify navigation after success
    verify(navigationActions).navigateTo(Screen.PROFILE)
  }

  @Test
  fun saveButtonDisabledWhenAllFieldsAreEmpty() {
    whenever(profileRepository.getUserProfile(any(), any())).thenAnswer {
      it.getArgument<(UserProfile?) -> Unit>(0).invoke(null)
    }

    composeTestRule.setContent { ProfileInformationScreen(profileViewModel, navigationActions) }

    // Nothing typed in, should be disabled
    composeTestRule.onNodeWithTag("profileSaveButton").assertIsNotEnabled()
  }

  @Test
  fun deleteButtonDisabledWhenProfileIsNull() {
    // No profile returned
    whenever(profileRepository.getUserProfile(any(), any())).thenAnswer {
      it.getArgument<(UserProfile?) -> Unit>(0).invoke(null)
    }

    composeTestRule.setContent { ProfileInformationScreen(profileViewModel, navigationActions) }

    // Profile is null, so delete is disabled
    composeTestRule.onNodeWithTag("profileDelete").assertIsNotEnabled()
  }
}
