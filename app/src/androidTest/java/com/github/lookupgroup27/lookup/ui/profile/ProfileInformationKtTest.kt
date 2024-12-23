package com.github.lookupgroup27.lookup.ui.profile

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import com.github.lookupgroup27.lookup.model.profile.*
import com.github.lookupgroup27.lookup.ui.navigation.*
import com.google.firebase.auth.FirebaseAuth
import org.junit.*
import org.mockito.Mockito.*

class ProfileInformationScreenTest {

  private lateinit var profileRepository: ProfileRepository
  private lateinit var profileViewModel: ProfileViewModel
  private lateinit var navigationActions: NavigationActions
  private lateinit var firebaseAuth: FirebaseAuth

  @get:Rule val composeTestRule = createComposeRule()

  @Before
  fun setUp() {

    profileRepository = mock(ProfileRepository::class.java)
    profileViewModel = ProfileViewModel(profileRepository)
    navigationActions = mock(NavigationActions::class.java)
    firebaseAuth = mock(FirebaseAuth::class.java)

    // Define navigation action behavior
    `when`(navigationActions.currentRoute()).thenReturn(Screen.PROFILE_INFORMATION)
  }

  @Test
  fun displayAllComponents() {
    composeTestRule.setContent { ProfileInformationScreen(profileViewModel, navigationActions) }

    // Scroll to and check that the main components are displayed
    composeTestRule.onNodeWithTag("editProfileScreen").assertIsDisplayed()
    composeTestRule.onNodeWithTag("editProfileTitle").assertIsDisplayed()
    composeTestRule.onNodeWithTag("goBackButton").assertIsDisplayed()

    composeTestRule.onNodeWithTag("editProfileUsername").performScrollTo().assertIsDisplayed()

    composeTestRule.onNodeWithTag("editProfileEmail").performScrollTo().assertIsDisplayed()

    composeTestRule.onNodeWithTag("editProfileBio").performScrollTo().assertIsDisplayed()

    composeTestRule.onNodeWithTag("profileSaveButton").performScrollTo().assertIsDisplayed()

    composeTestRule.onNodeWithTag("profileLogout").performScrollTo().assertIsDisplayed()

    // Check button texts after scrolling
    composeTestRule.onNodeWithTag("profileSaveButton").assertTextEquals("Save")
    composeTestRule.onNodeWithTag("profileLogout").assertTextEquals("Sign out")
  }

  @Test
  fun saveButtonDisabledWhenFieldsAreEmpty() {
    composeTestRule.setContent { ProfileInformationScreen(profileViewModel, navigationActions) }

    // Initially, all fields are empty, so the save button should be disabled
    composeTestRule.onNodeWithTag("profileSaveButton").assertIsNotEnabled()

    // Fill in username, bio, and email
    composeTestRule.onNodeWithTag("editProfileUsername").performTextInput("JohnDoe")
    composeTestRule.onNodeWithTag("editProfileEmail").performTextInput("john.doe@example.com")
    composeTestRule.onNodeWithTag("editProfileBio").performTextInput("This is a bio")

    // Now all fields are filled, so the save button should be enabled
    composeTestRule.onNodeWithTag("profileSaveButton").assertIsEnabled()
  }

  @Test
  fun logoutButtonWorks() {
    composeTestRule.setContent { ProfileInformationScreen(profileViewModel, navigationActions) }

    // Scroll to the sign-out button if it's off-screen, then click it
    composeTestRule.onNodeWithTag("profileLogout").performScrollTo().performClick()

    // Verify that the navigation action to the landing screen was triggered
    verify(navigationActions).navigateTo(Screen.LANDING)
  }

  @Test
  fun saveButtonWorks() {
    composeTestRule.setContent { ProfileInformationScreen(profileViewModel, navigationActions) }

    // Assert: Save button is initially disabled
    composeTestRule.onNodeWithTag("profileSaveButton").assertIsNotEnabled()

    // Act: Fill in only the username
    composeTestRule.onNodeWithTag("editProfileUsername").performTextInput("JohnDoe")
    // Assert: Save button is still disabled
    composeTestRule.onNodeWithTag("profileSaveButton").assertIsNotEnabled()

    // Act: Fill in email
    composeTestRule.onNodeWithTag("editProfileEmail").performTextInput("john.doe@example.com")
    // Assert: Save button is still disabled because bio is empty
    composeTestRule.onNodeWithTag("profileSaveButton").assertIsNotEnabled()

    // Act: Fill in the bio
    composeTestRule.onNodeWithTag("editProfileBio").performTextInput("This is a bio")
    // Assert: Save button should now be enabled
    composeTestRule.onNodeWithTag("profileSaveButton").assertIsEnabled()
    composeTestRule.onNodeWithTag("profileSaveButton").performClick()
    verify(navigationActions).navigateTo(Screen.PROFILE)
  }

  @Test
  fun saveButtonDisabledWhenAllFieldsAreEmpty() {
    composeTestRule.setContent { ProfileInformationScreen(profileViewModel, navigationActions) }

    // Assert: Save button is disabled because no fields have been populated
    composeTestRule.onNodeWithTag("profileSaveButton").assertIsNotEnabled()
  }

  @Test
  fun deleteButtonDisabledWhenProfileIsNull() {
    composeTestRule.setContent { ProfileInformationScreen(profileViewModel, navigationActions) }

    // Assert: Delete button is disabled because the profile is null
    composeTestRule.onNodeWithTag("profileDelete").assertIsNotEnabled()
  }

  @Test
  fun deleteButtonShowsConfirmationDialog() {
    composeTestRule.setContent { ProfileInformationScreen(profileViewModel, navigationActions) }

    // Fill in the fields to enable the delete button
    composeTestRule.onNodeWithTag("editProfileUsername").performTextInput("JohnDoe")
    composeTestRule.onNodeWithTag("editProfileEmail").performTextInput("john.doe@example.com")
    composeTestRule.onNodeWithTag("editProfileBio").performTextInput("This is a bio")

    // Click delete button
    composeTestRule.onNodeWithTag("profileDelete").performScrollTo().performClick()

    // Verify dialog appears
    composeTestRule.onNodeWithTag("deleteConfirmationDialog").assertExists()
  }

  @Test
  fun deleteConfirmationDialogCancelWorks() {
    composeTestRule.setContent { ProfileInformationScreen(profileViewModel, navigationActions) }

    // Fill in the fields to enable the delete button
    composeTestRule.onNodeWithTag("editProfileUsername").performTextInput("JohnDoe")
    composeTestRule.onNodeWithTag("editProfileEmail").performTextInput("john.doe@example.com")
    composeTestRule.onNodeWithTag("editProfileBio").performTextInput("This is a bio")

    // Click delete button
    composeTestRule.onNodeWithTag("profileDelete").performScrollTo().performClick()

    // Click cancel button
    composeTestRule.onNodeWithTag("cancelDeleteButton").performClick()

    // Verify dialog disappears
    composeTestRule.onNodeWithTag("deleteConfirmationDialog").assertDoesNotExist()
  }

  @Test
  fun deleteConfirmationDialogConfirmWorks() {
    composeTestRule.setContent { ProfileInformationScreen(profileViewModel, navigationActions) }

    // Fill in the fields to enable the delete button
    composeTestRule.onNodeWithTag("editProfileUsername").performTextInput("JohnDoe")
    composeTestRule.onNodeWithTag("editProfileEmail").performTextInput("john.doe@example.com")
    composeTestRule.onNodeWithTag("editProfileBio").performTextInput("This is a bio")

    // Click delete button
    composeTestRule.onNodeWithTag("profileDelete").performScrollTo().performClick()

    // Click confirm button
    composeTestRule.onNodeWithTag("confirmDeleteButton").performClick()

    // Verify navigation to menu screen
    verify(navigationActions).navigateTo(Screen.MENU)
  }

  @Test
  fun deleteButtonWorks() {
    composeTestRule.setContent { ProfileInformationScreen(profileViewModel, navigationActions) }

    // Fill in the fields
    composeTestRule.onNodeWithTag("editProfileUsername").performTextInput("JohnDoe")
    composeTestRule.onNodeWithTag("editProfileEmail").performTextInput("john.doe@example.com")
    composeTestRule.onNodeWithTag("editProfileBio").performTextInput("This is a bio")

    // Click delete button
    composeTestRule.onNodeWithTag("profileDelete").performScrollTo().performClick()

    // Verify dialog appears
    composeTestRule.onNodeWithTag("deleteConfirmationDialog").assertExists()

    // Click confirm button
    composeTestRule.onNodeWithTag("confirmDeleteButton").performClick()

    // Verify navigation to menu screen
    verify(navigationActions).navigateTo(Screen.MENU)
  }
}
