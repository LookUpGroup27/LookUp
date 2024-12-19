package com.github.lookupgroup27.lookup.ui.post

import android.content.Context
import android.location.Location
import androidx.test.core.app.ApplicationProvider
import com.github.lookupgroup27.lookup.model.location.LocationProvider
import com.github.lookupgroup27.lookup.model.post.Post
import com.github.lookupgroup27.lookup.model.post.PostsRepository
import com.github.lookupgroup27.lookup.model.post.PostsRepositoryFirestore
import com.google.firebase.FirebaseApp
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`
import org.mockito.kotlin.any
import org.mockito.kotlin.whenever
import org.robolectric.RobolectricTestRunner

/** Unit tests for sorting functionality in the PostsViewModel. */
@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(RobolectricTestRunner::class)
class PostsViewModelSortingTest {

  @Mock private lateinit var mockFirestore: FirebaseFirestore
  @Mock private lateinit var mockCollectionReference: CollectionReference

  private lateinit var postsRepositoryFirestore: PostsRepositoryFirestore
  private lateinit var postsViewModel: PostsViewModel
  private lateinit var context: Context
  private lateinit var locationProvider: TestLocationProvider
  private lateinit var postsRepository: PostsRepository

  @Before
  fun setUp() {
    // Initialize Firebase
    context = ApplicationProvider.getApplicationContext()
    if (FirebaseApp.getApps(context).isEmpty()) {
      FirebaseApp.initializeApp(context)
    }

    postsRepository = mock(PostsRepository::class.java)
    mockFirestore = mock(FirebaseFirestore::class.java)
    mockCollectionReference = mock(CollectionReference::class.java)
    `when`(mockFirestore.collection(any())).thenReturn(mockCollectionReference)
    postsRepositoryFirestore = PostsRepositoryFirestore(mockFirestore)
    postsViewModel = PostsViewModel(postsRepositoryFirestore)
    locationProvider = TestLocationProvider()

    // Initialize the ViewModel with context
    postsViewModel.setContext(context)
  }

  @After
  fun tearDown() {
    Dispatchers.resetMain()
  }

  @Test
  fun `fetchNearbyPostsWithImages returns empty list when location is null`() = runTest {
    // Simulate location as null
    locationProvider.setLocation(null, null)

    // Set the locationProvider in ViewModel
    postsViewModel.setLocationProviderForTesting(locationProvider)

    // Call the function
    postsViewModel.fetchSortedPosts()

    // Wait for any asynchronous updates
    testScheduler.advanceUntilIdle()

    // Assert that no nearby posts are returned when location is null
    assertTrue(postsViewModel.nearbyPosts.value.isEmpty())
  }

  @Test
  fun `fetchNearbyPostsWithImages returns empty list when no posts are available`() = runTest {
    // Set a valid location
    locationProvider.setLocation(37.7749, -122.4194) // San Francisco coordinates

    // Set the locationProvider in ViewModel
    postsViewModel.setLocationProviderForTesting(locationProvider)

    // Simulate empty list of posts in the repository
    whenever(postsRepository.getPosts(any(), any())).thenAnswer {
      (it.arguments[0] as (List<Post>?) -> Unit).invoke(emptyList())
    }

    // Fetch posts
    postsViewModel.fetchSortedPosts()

    // Wait for asynchronous updates
    testScheduler.advanceUntilIdle()

    // Assert that no nearby posts are returned when there are no posts in the repository
    assertTrue(postsViewModel.nearbyPosts.value.isEmpty())
  }

  @Test
  fun `getSortedNearbyPosts sorts posts correctly`() {
    // Arrange
    val post1 = Post("Post 1", latitude = 37.7750, longitude = -122.4195, timestamp = 1000)
    val post2 = Post("Post 2", latitude = 37.7755, longitude = -122.4190, timestamp = 2000)
    val post3 = Post("Post 3", latitude = 37.7760, longitude = -122.4185, timestamp = 3000)
    val posts = listOf(post3, post2, post1)

    val userLatitude = 37.7749
    val userLongitude = -122.4194

    // Act
    val sortedPosts = postsViewModel.getSortedNearbyPosts(posts, userLatitude, userLongitude)

    // Assert
    assertEquals(post1, sortedPosts[0]) // Closest post
    assertEquals(post2, sortedPosts[1]) // Second closest
    assertEquals(post3, sortedPosts[2]) // Third closest
  }

  /** TestLocationProvider allows for manual setting of location values. */
  class TestLocationProvider : LocationProvider(ApplicationProvider.getApplicationContext()) {
    fun setLocation(latitude: Double?, longitude: Double?) {
      if (latitude != null && longitude != null) {
        currentLocation.value =
            Location("test").apply {
              this.latitude = latitude
              this.longitude = longitude
            }
      } else {
        currentLocation.value = null
      }
    }
  }
}
