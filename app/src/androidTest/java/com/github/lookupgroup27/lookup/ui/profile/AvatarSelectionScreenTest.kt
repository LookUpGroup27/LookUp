package com.github.lookupgroup27.lookup.ui.profile

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import com.github.lookupgroup27.lookup.R
import com.github.lookupgroup27.lookup.model.profile.ProfileRepository
import com.github.lookupgroup27.lookup.ui.navigation.NavigationActions
import com.github.lookupgroup27.lookup.ui.profile.profilepic.AvatarSelectionScreen
import com.github.lookupgroup27.lookup.ui.profile.profilepic.AvatarViewModel
import io.github.kakaocup.kakao.common.utilities.getResourceString
import kotlinx.coroutines.flow.MutableStateFlow
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.kotlin.*

class AvatarSelectionScreenTest {

  @get:Rule val composeTestRule = createComposeRule()

  private lateinit var avatarViewModel: AvatarViewModel
  private lateinit var mockNavigationActions: NavigationActions
  private lateinit var profileRepository: ProfileRepository

  // Use a real MutableStateFlow for testing state updates
  private val selectedAvatarFlow = MutableStateFlow<Int?>(null)

  @Before
  fun setup() {
    // Mock the ProfileRepository
    profileRepository = mock {
      on { getSelectedAvatar(any(), any(), any()) } doAnswer
          { invocation ->
            val onSuccess = invocation.getArgument<(Int?) -> Unit>(1)
            onSuccess(selectedAvatarFlow.value)
          }
    }

    // Initialize the AvatarViewModel with the mocked repository
    avatarViewModel = AvatarViewModel(profileRepository)

    // Mock NavigationActions
    mockNavigationActions = mock()
  }

  @Test
  fun avatarSelectionScreen_avatarClick_updatesViewModel() {
    val userId = "testUser"
    val avatarResourceId = R.drawable.avatar1 // Simulate a valid resource ID

    composeTestRule.setContent {
      AvatarSelectionScreen(
          avatarViewModel = avatarViewModel,
          userId = userId,
          navigationActions = mockNavigationActions)
    }

    // Simulate clicking on the first avatar
    composeTestRule.onAllNodesWithContentDescription("Avatar")[0].performClick()

    // Click the Confirm button
    composeTestRule.onNodeWithText(getResourceString(R.string.confirm_selection)).performClick()

    // Verify that saveSelectedAvatar was called with the correct resource ID
    verify(profileRepository).saveSelectedAvatar(eq(userId), eq(avatarResourceId), any(), any())
  }

  @Test
  fun avatarSelectionScreen_resetButton_disablesForDefaultAvatar() {
    val userId = "testUser"
    selectedAvatarFlow.value = R.drawable.default_profile_icon // Set default avatar

    composeTestRule.setContent {
      AvatarSelectionScreen(
          avatarViewModel = avatarViewModel,
          userId = userId,
          navigationActions = mockNavigationActions)
    }

    // Verify Reset button is disabled
    composeTestRule
        .onNodeWithText(getResourceString(R.string.reset_to_default_avatar))
        .assertIsNotEnabled()
  }

  @Test
  fun avatarSelectionScreen_preselectsExistingAvatar() {
    val userId = "testUser"
    val existingAvatar = R.drawable.avatar2
    selectedAvatarFlow.value = existingAvatar // Simulate an existing avatar

    composeTestRule.setContent {
      AvatarSelectionScreen(
          avatarViewModel = avatarViewModel,
          userId = userId,
          navigationActions = mockNavigationActions)
    }

    // Verify the existing avatar is pre-selected
    composeTestRule.onAllNodesWithContentDescription("Avatar")[1].assert(hasClickAction())
  }

  @Test
  fun avatarSelectionScreen_confirmButton_updatesAndNavigates() {
    val userId = "testUser"
    selectedAvatarFlow.value = R.drawable.avatar2 // Simulate an existing avatar

    composeTestRule.setContent {
      AvatarSelectionScreen(
          avatarViewModel = avatarViewModel,
          userId = userId,
          navigationActions = mockNavigationActions)
    }

    val newAvatarResourceId = R.drawable.avatar4

    // Click on a different avatar
    composeTestRule.onAllNodesWithContentDescription("Avatar")[3].performClick()

    // Click the Confirm button
    composeTestRule.onNodeWithText(getResourceString(R.string.confirm_selection)).performClick()

    // Verify that saveSelectedAvatar was called with the new avatar
    verify(profileRepository).saveSelectedAvatar(eq(userId), eq(newAvatarResourceId), any(), any())

    // Verify navigation back to ProfileScreen
    verify(mockNavigationActions).goBack()
  }

  @Test
  fun avatarSelectionScreen_resetButton_enablesForCustomAvatar() {
    val userId = "testUser"
    val avatarResourceId = R.drawable.avatar1
    selectedAvatarFlow.value = avatarResourceId // Simulate a custom avatar (non-default)

    composeTestRule.setContent {
      AvatarSelectionScreen(
          avatarViewModel = avatarViewModel,
          userId = userId,
          navigationActions = mockNavigationActions)
    }

    // Simulate clicking on the first avatar
    composeTestRule.onAllNodesWithContentDescription("Avatar")[0].performClick()

    // Click the Confirm button
    composeTestRule.onNodeWithText(getResourceString(R.string.confirm_selection)).performClick()

    // Click Reset button (shows that it is enabled)
    composeTestRule
        .onNodeWithText(getResourceString(R.string.reset_to_default_avatar))
        .performClick()

    // Verify that the avatar was reset in the repository
    verify(profileRepository).saveSelectedAvatar(eq(userId), eq(avatarResourceId), any(), any())

    // Verify navigation back to ProfileScreen
    verify(mockNavigationActions).goBack()
  }

  @Test
  fun avatarSelectionScreen_goBackArrow_displaysAndNavigatesBack() {
    val userId = "testUser"

    composeTestRule.setContent {
      AvatarSelectionScreen(
          avatarViewModel = avatarViewModel,
          userId = userId,
          navigationActions = mockNavigationActions)
    }

    // Verify the go-back arrow is displayed
    composeTestRule
        .onNodeWithContentDescription(getResourceString(R.string.back))
        .assertIsDisplayed()

    // Simulate clicking the go-back arrow
    composeTestRule.onNodeWithContentDescription(getResourceString(R.string.back)).performClick()

    // Verify that the navigation action to go back was triggered
    verify(mockNavigationActions).goBack()
  }
}
