package com.github.lookupgroup27.lookup.model.profile

import com.github.lookupgroup27.lookup.ui.profile.ProfileViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertFalse
import kotlinx.coroutines.*
import kotlinx.coroutines.test.*
import org.junit.*
import org.mockito.Mockito.*
import org.mockito.kotlin.any
import org.mockito.kotlin.eq

@OptIn(ExperimentalCoroutinesApi::class)
class ProfileViewModelTest {

  private lateinit var repository: ProfileRepositoryFirestore
  private lateinit var viewModel: ProfileViewModel
  private lateinit var mockFirestore: FirebaseFirestore
  private lateinit var mockCollectionReference: CollectionReference
  private lateinit var mockAuth: FirebaseAuth
  private lateinit var mockUser: FirebaseUser

  private val testProfile = UserProfile("Test User", "test@example.com", "A short bio")
  private val testDispatcher = UnconfinedTestDispatcher() // Using UnconfinedTestDispatcher

  @Before
  fun setUp() {
    Dispatchers.setMain(testDispatcher)

    // Mock FirebaseAuth and FirebaseUser
    mockAuth = mock(FirebaseAuth::class.java)
    mockUser = mock(FirebaseUser::class.java)
    mockFirestore = mock(FirebaseFirestore::class.java)
    mockCollectionReference = mock(CollectionReference::class.java)

    // Mock current user and UID
    `when`(mockAuth.currentUser).thenReturn(mockUser)
    `when`(mockUser.uid).thenReturn("testUserId")

    // Mock Firestore collection behavior
    `when`(mockFirestore.collection("users")).thenReturn(mockCollectionReference)

    // Initialize repository with mocks and viewModel with mocked repository
    repository = mock(ProfileRepositoryFirestore::class.java)
    viewModel = ProfileViewModel(repository)
  }

  @After
  fun tearDown() {
    Dispatchers.resetMain() // reset the Main dispatcher to the original
  }

  @Test
  fun `fetchUserProfile calls getUserProfile in repository`() = runTest {
    // Simulate a successful fetch with a mocked behavior
    `when`(repository.getUserProfile(any(), any())).thenAnswer { invocation ->
      val onSuccess = invocation.arguments[0] as (UserProfile?) -> Unit
      onSuccess(testProfile)
    }

    viewModel.fetchUserProfile()
    verify(repository).getUserProfile(any(), any())
  }

  @Test
  fun `updateUserProfile calls updateUserProfile in repository`() = runTest {
    // Simulate successful update behavior
    `when`(repository.updateUserProfile(eq(testProfile), any(), any())).thenAnswer { invocation ->
      val onSuccess = invocation.arguments[1] as () -> Unit
      onSuccess()
    }

    viewModel.updateUserProfile(testProfile)

    // Verifying that the method is indeed called
    verify(repository).updateUserProfile(eq(testProfile), any(), any())
  }

  @Test
  fun `logoutUser calls logoutUser in repository`() {
    viewModel.logoutUser()
    verify(repository).logoutUser()
  }

  @Test
  fun `fetchUserProfile sets error on failure`() = runTest {
    // Mock an error scenario in the repository
    val exception = Exception("Network error")
    `when`(repository.getUserProfile(any(), any())).thenAnswer {
      val onFailure = it.getArgument<(Exception) -> Unit>(1)
      onFailure(exception)
    }

    // Call the method
    viewModel.fetchUserProfile()

    // Assert that _error was updated correctly
    assertEquals("Failed to load profile: Network error", viewModel.error.value)
  }

  @Test
  fun `updateUserProfile sets profileUpdateStatus to false and error on failure`() = runTest {
    // Mock an error scenario in the repository
    val exception = Exception("Update failed")
    `when`(repository.updateUserProfile(any(), any(), any())).thenAnswer {
      val onFailure = it.getArgument<(Exception) -> Unit>(2)
      onFailure(exception)
    }

    // Call the method
    viewModel.updateUserProfile(testProfile)

    // Assert that _profileUpdateStatus is false and _error is set
    assertFalse(viewModel.profileUpdateStatus.value!!)
    assertEquals("Failed to update profile: Update failed", viewModel.error.value)
  }

  @Test
  fun `deleteUserProfile calls deleteUserProfile in repository on success`() = runTest {
    // Simulate successful deletion behavior
    `when`(repository.deleteUserProfile(any(), any(), any())).thenAnswer { invocation ->
      val onSuccess = invocation.arguments[1] as () -> Unit
      onSuccess()
    }

    viewModel.deleteUserProfile(testProfile)

    // Verifying that the method is indeed called
    verify(repository).deleteUserProfile(eq(testProfile), any(), any())
    assertEquals(true, viewModel.profileUpdateStatus.value) // Check if status is set to true
  }

  @Test
  fun `deleteUserProfile sets profileUpdateStatus to false and error on failure`() = runTest {
    // Mock an error scenario in the repository
    val exception = Exception("Delete failed")
    `when`(repository.deleteUserProfile(any(), any(), any())).thenAnswer {
      val onFailure = it.getArgument<(Exception) -> Unit>(2)
      onFailure(exception)
    }

    // Call the method
    viewModel.deleteUserProfile(testProfile)

    // Assert that _profileUpdateStatus is false and _error is set
    assertFalse(viewModel.profileUpdateStatus.value!!)
    assertEquals("Failed to delete profile: Delete failed", viewModel.error.value)
  }
}
