import com.github.lookupgroup27.lookup.model.collection.CollectionRepository
import com.github.lookupgroup27.lookup.model.collection.CollectionViewModel
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
  fun `test imageUrls are empty initially`() = runBlocking {
    val mockRepository: CollectionRepository = mock()
    whenever(mockRepository.getUserImageUrls()).thenReturn(emptyList())

    viewModel = CollectionViewModel(mockRepository)
    val initialImageUrls = viewModel.imageUrls.first()

    assertTrue("Initial image URLs should be empty", initialImageUrls.isEmpty())
  }

  @Test
  fun `test fetchImages updates imageUrls with correct values`() = runTest {
    val mockRepository: CollectionRepository = mock()
    val mockImageUrls = listOf("mock_url_1", "mock_url_2")

    // Mock repository to return the mock image URLs
    whenever(mockRepository.getUserImageUrls()).thenReturn(mockImageUrls)
    whenever(mockRepository.init(any())).thenAnswer { invocation ->
      (invocation.arguments[0] as () -> Unit).invoke()
    }

    // Initialize the ViewModel with the mock repository
    viewModel = CollectionViewModel(mockRepository)

    // Advance coroutine execution until completion
    advanceUntilIdle()

    // Check that imageUrls state contains the mock URLs
    val imageUrls = viewModel.imageUrls.first()
    assertEquals(mockImageUrls, imageUrls)
  }

  @Test
  fun `test fetchImages does not update imageUrls on empty result`() = runBlocking {
    val mockRepository: CollectionRepository = mock()
    whenever(mockRepository.getUserImageUrls()).thenReturn(emptyList())

    viewModel = CollectionViewModel(mockRepository)
    testDispatcher.scheduler.advanceUntilIdle()

    val imageUrls = viewModel.imageUrls.first()
    assertTrue("Image URLs should be empty when no data is returned", imageUrls.isEmpty())
  }

  @Test
  fun `test error state when repository fails`() = runBlocking {
    val failingMockRepository: CollectionRepository = mock()
    whenever(failingMockRepository.getUserImageUrls())
        .thenThrow(RuntimeException("Simulated repository failure"))

    viewModel = CollectionViewModel(failingMockRepository)
    testDispatcher.scheduler.advanceUntilIdle()

    val imageUrls = viewModel.imageUrls.first()
    assertTrue("Image URLs should be empty when an error occurs", imageUrls.isEmpty())
  }
}
