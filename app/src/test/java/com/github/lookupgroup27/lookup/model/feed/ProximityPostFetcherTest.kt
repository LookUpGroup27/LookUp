package com.github.lookupgroup27.lookup.model.feed

import android.content.Context
import android.location.Location
import androidx.test.core.app.ApplicationProvider
import com.github.lookupgroup27.lookup.model.location.LocationProvider
import com.github.lookupgroup27.lookup.model.post.Post
import com.github.lookupgroup27.lookup.model.post.PostsRepository
import com.github.lookupgroup27.lookup.model.post.PostsRepositoryFirestore
import com.github.lookupgroup27.lookup.ui.post.PostsViewModel
import com.google.firebase.FirebaseApp
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.*
import org.junit.After
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

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(RobolectricTestRunner::class)
class ProximityPostFetcherTest {

  @Mock private lateinit var mockFirestore: FirebaseFirestore
  @Mock private lateinit var mockCollectionReference: CollectionReference

  private lateinit var postsRepositoryFirestore: PostsRepositoryFirestore
  private lateinit var postsViewModel: PostsViewModel
  private lateinit var proximityPostFetcher: ProximityPostFetcher
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

    proximityPostFetcher = ProximityPostFetcher(postsViewModel, context)
    locationProvider = TestLocationProvider()
  }

  @After
  fun tearDown() {
    Dispatchers.resetMain()
  }

  @Test
  fun `fetchNearbyPostsWithImages returns empty list when location is null`() = runTest {
    // Simulate location as null
    locationProvider.setLocation(null, null)

    // Call the function
    proximityPostFetcher.fetchNearbyPostsWithImages()

    // Wait for any asynchronous updates
    testScheduler.advanceUntilIdle()

    // Assert that no nearby posts are returned when location is null
    assertTrue(proximityPostFetcher.nearbyPosts.value.isEmpty())
  }

  @Test
  fun `fetchNearbyPostsWithImages returns empty list when no posts are available`() = runTest {
    // Set a valid location
    locationProvider.setLocation(37.7749, -122.4194) // San Francisco coordinates

    // Simulate empty list of posts in the repository
    whenever(postsRepository.getPosts(any(), any())).thenAnswer {
      (it.arguments[0] as (List<Post>?) -> Unit).invoke(emptyList())
    }

    // Fetch posts
    proximityPostFetcher.fetchNearbyPostsWithImages()

    // Wait for asynchronous updates
    testScheduler.advanceUntilIdle()

    // Assert that no nearby posts are returned when there are no posts in the repository
    assertTrue(proximityPostFetcher.nearbyPosts.value.isEmpty())
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
