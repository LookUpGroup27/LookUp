package com.github.lookupgroup27.lookup.model.profile

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
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
}
