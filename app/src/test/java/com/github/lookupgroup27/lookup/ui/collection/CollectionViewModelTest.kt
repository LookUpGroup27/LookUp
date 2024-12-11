package com.github.lookupgroup27.lookup.ui.collection

import com.github.lookupgroup27.lookup.model.collection.CollectionRepository
import com.github.lookupgroup27.lookup.model.post.Post
import com.github.lookupgroup27.lookup.ui.profile.CollectionViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever

@OptIn(ExperimentalCoroutinesApi::class)
class CollectionViewModelTest {

  private lateinit var viewModel: CollectionViewModel
  private val testDispatcher = StandardTestDispatcher()

  @Before
  fun setUp() {
    Dispatchers.setMain(testDispatcher)
  }

  @After
  fun tearDown() {
    Dispatchers.resetMain()
  }

  @Test
  fun `test myPosts are empty initially`() = runBlocking {
    val mockRepository: CollectionRepository = mock()
    whenever(mockRepository.getUserPosts(any(), any())).thenAnswer { invocation ->
      val onSuccess = invocation.arguments[0] as (List<Post>?) -> Unit
      onSuccess(emptyList())
    }

    viewModel = CollectionViewModel(mockRepository)
    val initialPosts = viewModel.myPosts.first()

    assertTrue("Initial image URLs should be empty", initialPosts.isEmpty())
  }

  @Test
  fun `test fetchPosts updates myPosts with correct values`() = runTest {
    val mockRepository: CollectionRepository = mock()
    val mockPosts = listOf(Post("mock_url_1"), Post("mock_url_2"))

    // Mock repository to execute success callback with mock data
    whenever(mockRepository.getUserPosts(any(), any())).thenAnswer { invocation ->
      val onSuccess = invocation.arguments[0] as (List<Post>?) -> Unit
      onSuccess(mockPosts)
    }
    whenever(mockRepository.init(any())).thenAnswer { invocation ->
      (invocation.arguments[0] as () -> Unit).invoke()
    }

    // Initialize the ViewModel with the mock repository
    val viewModel = CollectionViewModel(mockRepository)

    // Advance coroutine execution until completion
    advanceUntilIdle()

    // Check that myPosts state contains the mock posts
    val myPosts = viewModel.myPosts.first()
    assertEquals(mockPosts, myPosts)
  }

  @Test
  fun `test fetchPosts does not update posts on empty result`() = runBlocking {
    val mockRepository: CollectionRepository = mock()
    whenever(mockRepository.getUserPosts(any(), any())).thenAnswer { invocation ->
      val onSuccess = invocation.arguments[0] as (List<Post>?) -> Unit
      onSuccess(emptyList())
    }

    viewModel = CollectionViewModel(mockRepository)
    testDispatcher.scheduler.advanceUntilIdle()

    val myPosts = viewModel.myPosts.first()
    assertTrue("myPosts should be empty when no data is returned", myPosts.isEmpty())
  }

  @Test
  fun `test error state when repository fails`() = runBlocking {
    val failingMockRepository: CollectionRepository = mock()
    whenever(failingMockRepository.getUserPosts(any(), any())).thenAnswer { invocation ->
      val onFailure = invocation.arguments[1] as (Exception) -> Unit
      onFailure(RuntimeException("Simulated repository failure"))
    }

    viewModel = CollectionViewModel(failingMockRepository)
    testDispatcher.scheduler.advanceUntilIdle()

    val myPosts = viewModel.myPosts.first()
    assertTrue("myPosts should be empty when an error occurs", myPosts.isEmpty())
  }
}
