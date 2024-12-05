package com.github.lookupgroup27.lookup.ui.profile

import com.github.lookupgroup27.lookup.model.profile.ProfileRepository
import com.github.lookupgroup27.lookup.model.profile.ProfileRepositoryFirestore
import com.github.lookupgroup27.lookup.ui.profile.profilepic.AvatarViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`
import org.mockito.kotlin.any
import org.mockito.kotlin.eq

@OptIn(ExperimentalCoroutinesApi::class)
class AvatarViewModelTest {

  private lateinit var repository: ProfileRepositoryFirestore
  private lateinit var viewModel: AvatarViewModel

  private val testDispatcher = UnconfinedTestDispatcher()

  @Before
  fun setUp() {
    Dispatchers.setMain(testDispatcher)

    // Use a simple mocked ProfileRepository
    repository = mock(ProfileRepositoryFirestore::class.java)

    viewModel = AvatarViewModel(repository)
  }

  @After
  fun tearDown() {
    Dispatchers.resetMain() // Reset the dispatcher
  }

  @Test
  fun `fetchSelectedAvatar updates selectedAvatar on success`() = runTest {
    val userId = "testUser"
    val avatarId = 123

    // Mock repository behavior for getSelectedAvatar
    `when`(repository.getSelectedAvatar(eq(userId), any(), any())).thenAnswer { invocation ->
      val onSuccess = invocation.arguments[1] as (Int?) -> Unit
      onSuccess(avatarId) // Simulate a successful fetch
    }

    // Call the method in ViewModel
    viewModel.fetchSelectedAvatar(userId)

    // Assert that the selectedAvatar is updated
    assertEquals(avatarId, viewModel.selectedAvatar.value)
  }

  @Test
  fun `saveSelectedAvatar updates error on failure`() = runTest {
    val userId = "testUser"
    val avatarId = 789

    // Modify the repository to simulate a failure
    val failingRepository =
        object : ProfileRepository by repository {
          override fun saveSelectedAvatar(
              userId: String,
              avatarId: Int?,
              onSuccess: () -> Unit,
              onFailure: (Exception) -> Unit
          ) {
            onFailure(Exception("Save failed"))
          }
        }

    viewModel = AvatarViewModel(failingRepository)

    viewModel.saveSelectedAvatar(userId, avatarId)

    assertEquals("Save failed", viewModel.error.value)
    assertNull(viewModel.selectedAvatar.value) // Ensure selectedAvatar is not updated
  }
}
