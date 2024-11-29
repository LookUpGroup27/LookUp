package com.github.lookupgroup27.lookup.ui.profile

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import com.github.lookupgroup27.lookup.model.profile.ProfileRepository
import com.github.lookupgroup27.lookup.ui.navigation.NavigationActions
import com.github.lookupgroup27.lookup.ui.profile.profilepic.AvatarSelectionScreen
import com.github.lookupgroup27.lookup.ui.profile.profilepic.AvatarViewModel
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

    composeTestRule.setContent {
      AvatarSelectionScreen(
          avatarViewModel = avatarViewModel,
          userId = userId,
          navigationActions = mockNavigationActions)
    }

    // Simulate clicking on an avatar
    composeTestRule.onAllNodesWithContentDescription("Avatar")[0].performClick()

    // Verify that saveSelectedAvatar was called on the ProfileRepository
    verify(profileRepository).saveSelectedAvatar(eq(userId), any(), any(), any())
  }

  @Test
  fun avatarSelectionScreen_confirmButton_navigatesBack() {
    val userId = "testUser"

    composeTestRule.setContent {
      AvatarSelectionScreen(
          avatarViewModel = avatarViewModel,
          userId = userId,
          navigationActions = mockNavigationActions)
    }

    // Click the Confirm button
    composeTestRule.onNodeWithText("Confirm Selection").performClick()

    // Verify that the navigation action to go back was triggered
    verify(mockNavigationActions).goBack()
  }
}
