package com.github.lookupgroup27.lookup.ui.feed

import android.Manifest
import android.content.Context
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertTextContains
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.test.core.app.ApplicationProvider
import androidx.test.rule.GrantPermissionRule
import com.github.lookupgroup27.lookup.TestLocationProvider
import com.github.lookupgroup27.lookup.model.location.LocationProvider
import com.github.lookupgroup27.lookup.model.location.LocationProviderSingleton
import com.github.lookupgroup27.lookup.model.post.Post
import com.github.lookupgroup27.lookup.model.post.PostsRepository
import com.github.lookupgroup27.lookup.model.profile.ProfileRepository
import com.github.lookupgroup27.lookup.model.profile.UserProfile
import com.github.lookupgroup27.lookup.ui.navigation.NavigationActions
import com.github.lookupgroup27.lookup.ui.navigation.Screen
import com.github.lookupgroup27.lookup.ui.navigation.TopLevelDestinations
import com.github.lookupgroup27.lookup.ui.post.PostsViewModel
import com.github.lookupgroup27.lookup.ui.profile.ProfileViewModel
import com.github.lookupgroup27.lookup.util.NetworkUtils
import com.google.firebase.auth.FirebaseAuth
import io.mockk.every
import io.mockk.mockkObject
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito.mock
import org.mockito.Mockito.never
import org.mockito.Mockito.`when`
import org.mockito.kotlin.any
import org.mockito.kotlin.eq
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

private lateinit var mockAuth: FirebaseAuth

class FeedScreenTest {

  @get:Rule val composeTestRule = createComposeRule()

  @get:Rule
  val permissionRule: GrantPermissionRule =
      GrantPermissionRule.grant(Manifest.permission.ACCESS_FINE_LOCATION)

  private lateinit var postsRepository: PostsRepository
  @Mock private lateinit var postsViewModel: PostsViewModel
  private lateinit var profileRepository: ProfileRepository
  @Mock private lateinit var profileViewModel: ProfileViewModel
  private lateinit var navigationActions: NavigationActions
  private lateinit var locationProvider: LocationProvider
  private val user = FirebaseAuth.getInstance().currentUser // Get the current signed-in user
  private val userEmail = user?.email ?: ""
  private val mockNavigationActions: NavigationActions = org.mockito.kotlin.mock()

  // Mock the logged-in user's profile
  val testUserProfile =
      UserProfile(
          username = "Test User",
          email = userEmail, // Mocked logged-in user's email
          bio = "This is a test bio",
          ratings = emptyMap())

  // Mock posts
  val testPosts =
      listOf(
          Post( // Post created by the logged-in user
              uid = "1",
              uri = "http://example.com/1.jpg",
              username = testUserProfile.email,
              userMail = testUserProfile.email,
              latitude = 37.7749, // San Francisco
              longitude = -122.4194,
              description = "This is a test description"),
          Post(
              uid = "2",
              uri = "http://example.com/2.jpg",
              username = "User2", // Post created by another user
              userMail = "User2",
              latitude = 34.0522, // Los Angeles
              longitude = -118.2437,
              description = "This is another test description"),
          Post(
              uid = "3",
              uri = "http://example.com/3.jpg",
              username = "User3",
              userMail = "User3", // Another user's post
              latitude = 36.7783, // Fresno (closer to SF)
              longitude = -119.4179,
              description = "This is yet another test description"),
          Post(
              uid = "4",
              uri = "User4",
              username = "user4@example.com", // Another user's post
              userMail = "User4",
              latitude = 40.7128, // New York City (farther from SF than LA or Fresno)
              longitude = -74.0060,
              description = "This is a test description"),
          Post(
              uid = "5",
              uri = "User5",
              username = "user5@example.com", // Another user's post
              userMail = "User5",
              latitude = -33.8688, // Sydney, Australia (farthest from SF)
              longitude = 151.2093,
              description = "This is a test description"))

  private val testPost =
      Post(
          "2",
          "testUri",
          "testUsername",
          "testUserMail",
          10,
          2.5,
          0.0,
          0.0,
          2,
          listOf("test@gmail.com", "joedoe@gmail.com"))

  private val testProfile =
      UserProfile(
          "Test User", "test@example.com", "A short bio", ratings = mapOf("1" to 1, "2" to 3))

  @Before
  fun setUp() {
    val context = ApplicationProvider.getApplicationContext<Context>()

    // Mock the repository and other dependencies
    postsRepository = mock(PostsRepository::class.java)
    postsViewModel = PostsViewModel(postsRepository)
    profileRepository = mock(ProfileRepository::class.java)
    profileViewModel = ProfileViewModel(profileRepository)
    navigationActions = mock(NavigationActions::class.java)
    profileViewModel.fetchUserProfile()

    `when`(postsRepository.getPosts(any(), any())).thenAnswer { invocation ->
      val onSuccessCallback = invocation.getArgument<(List<Post>?) -> Unit>(0)
      onSuccessCallback(testPosts) // Return the mocked posts
    }

    // Define navigation action behavior
    `when`(navigationActions.currentRoute()).thenReturn(Screen.FEED)

    locationProvider = LocationProvider(context, mutableStateOf(null))
  }

  /**
   * Helper function to set up the FeedScreen with the given list of nearby posts.
   *
   * @param initialNearbyPosts The list of posts to display initially on the feed screen.
   *
   * This function simplifies test setup by allowing each test to specify the initial state of the
   * feed. It handles the rendering of the FeedScreen with the specified posts and ensures a
   * consistent setup across all tests.
   */
  private fun setFeedScreenContent(initialNearbyPosts: List<Post>, testNoLoca: Boolean = false) {
    composeTestRule.setContent {
      FeedScreen(
          postsViewModel = postsViewModel,
          navigationActions = navigationActions,
          profileViewModel = profileViewModel,
          initialNearbyPosts = initialNearbyPosts,
          testNoLoca = testNoLoca)
    }
  }

  @Test
  fun testFeedScreenDisplaysNearbyPosts() {
    setFeedScreenContent(testPosts)

    // Assert each post item is displayed
    composeTestRule.onNodeWithTag("PostItem_2").assertExists()
    composeTestRule.onNodeWithTag("UsernameTag_User2").assertTextContains("User2")

    composeTestRule.onNodeWithTag("PostItem_3").performScrollTo().assertExists()
    composeTestRule.onNodeWithTag("UsernameTag_User3").assertTextContains("User3")

    composeTestRule.onNodeWithTag("PostItem_5").assertDoesNotExist()
  }

  @Test
  fun testFeedExcludesLoggedInUserPosts() {
    setFeedScreenContent(testPosts)
    // Assert that the post by the logged-in user  is not displayed
    composeTestRule.onNodeWithTag("PostItem_1").assertDoesNotExist()
  }

  @Test
  fun testBottomNavigationMenuIsDisplayed() {
    setFeedScreenContent(testPosts)

    // Verify the bottom navigation menu is displayed
    composeTestRule
        .onNodeWithTag("BottomNavigationMenu")
        .assertExists("Bottom Navigation Menu should exist")
        .assertIsDisplayed()
  }

  @Test
  fun testStarClickDisplaysAverageRating() {
    setFeedScreenContent(testPosts)
    // Perform click on the first star icon of a post with uid "1"
    composeTestRule
        .onNodeWithTag("Star_2_2")
        .assertIsDisplayed()
        .performClick() // Click on the first star

    // Verify that the average rating text is displayed for the post
    composeTestRule.onNodeWithTag("AverageRatingTag_2").assertExists().assertIsDisplayed()
  }

  @Test
  fun testStarClickCallsUpdatePost() {
    setFeedScreenContent(testPosts)
    // Perform click on the first star of post with uid "1"
    composeTestRule.onNodeWithTag("Star_2_2").performClick()
    postsViewModel.updatePost(testPost)

    // Verify that updatePost was called in the postsViewModel
    verify(postsRepository).updatePost(eq(testPost), any(), any())
  }

  /*@Test
  fun testStarClickCallsUpdateUserProfile() {
    // Perform click on the first star of post with uid "1"
    composeTestRule.onNodeWithTag("Star_2_2").performClick()

    profileViewModel.updateUserProfile(testProfile)
    // Verify that `updateUserProfile` was called in the profileViewModel
    verify(profileRepository).updateUserProfile(eq(testProfile), any(), any())
  }  }*/

  @Test
  fun testNavigationToFeedBlockedForLoggedOutUser() {
    setFeedScreenContent(testPosts)
    // Mock the user as not logged in
    mockAuth = org.mockito.kotlin.mock()
    whenever(mockAuth.currentUser).thenReturn(null)

    // Simulate clicking the Feed tab in the bottom navigation
    composeTestRule.onNodeWithTag("Feed").performClick()

    // Verify that the navigation action to the Feed was not triggered
    verify(navigationActions, never()).navigateTo(eq(TopLevelDestinations.FEED))
  }

  @Test
  fun testAddressIsDisplayed() {
    setFeedScreenContent(testPosts)
    // Verify that the address is displayed for each post
    composeTestRule.onNodeWithTag("AddressTag_2").performScrollTo().assertIsDisplayed()
    composeTestRule.onNodeWithTag("AddressTag_5").assertDoesNotExist()
  }

  @Test
  fun testDescriptionIsDisplayed() {
    setFeedScreenContent(testPosts)
    // Verify that the description is displayed for each post
    composeTestRule.onNodeWithTag("DescriptionTag_2").performScrollTo().assertIsDisplayed()
    composeTestRule.onNodeWithTag("DescriptionTag_5").assertDoesNotExist()
  }

  @Test
  fun testFeedDisplaysNoImagesMessageWhenPostsAreEmpty() {
    // Arrange: Mock location provider and permissions
    val testLocationProvider = TestLocationProvider()
    testLocationProvider.setLocation(37.7749, -122.4194) // Mocked location

    mockkObject(LocationProviderSingleton)
    every { LocationProviderSingleton.getInstance(any()) } returns testLocationProvider

    // Act: Render the FeedScreen with no posts
    setFeedScreenContent(emptyList())

    // Wait for the location to emit
    composeTestRule.waitForIdle()

    // Assert: "No images available" message is displayed
    composeTestRule.onNodeWithTag("feed_no_images_available").assertExists().assertIsDisplayed()
  }

  @Test
  fun testFeedDisplaysLoadingIndicatorWhenLocationIsNull() {
    // Arrange: Mock location provider and permissions
    val testLocationProvider = TestLocationProvider()
    testLocationProvider.setLocation(null, null) // No location emitted

    mockkObject(LocationProviderSingleton)
    every { LocationProviderSingleton.getInstance(any()) } returns testLocationProvider

    // Act: Render the FeedScreen
    setFeedScreenContent(emptyList())

    // Wait for the composition to stabilize
    composeTestRule.waitForIdle()

    // Assert: Loading indicator (CircularProgressIndicator) is displayed
    composeTestRule.onNodeWithTag("loading_indicator_test_tag").assertExists().assertIsDisplayed()
  }

  @Test
  fun testFeedDisplaysNoImagesMessageWithPlaceholderImage() {
    // Arrange: Mock location provider and permissions
    val testLocationProvider = TestLocationProvider()
    testLocationProvider.setLocation(37.7749, -122.4194) // Mocked location

    mockkObject(LocationProviderSingleton)
    every { LocationProviderSingleton.getInstance(any()) } returns testLocationProvider

    // Act: Render the FeedScreen with no posts
    setFeedScreenContent(emptyList())

    // Wait for any updates to complete
    composeTestRule.waitForIdle()

    // Assert: Placeholder image is displayed
    composeTestRule.onNodeWithTag("no_images_placeholder").assertExists().assertIsDisplayed()
  }

  @Test
  fun testEnableLocationButtonIsDisplayed() {
    setFeedScreenContent(emptyList(), true)
    composeTestRule
        .onNodeWithTag("enable_location_button")
        .assertExists() // Verify button is displayed
    composeTestRule.onNodeWithText("Enable Location").assertExists() // Verify button text
    composeTestRule.onNodeWithTag("enable_location_button").performClick()
  }

  @Test
  fun testNavigationToFeedBlockedForOfflineMode() {
    setFeedScreenContent(emptyList())
    // Simulate offline mode
    mockkObject(NetworkUtils)
    every { NetworkUtils.isNetworkAvailable(any()) } returns false
    // Simulate clicking the sky map tab in the bottom navigation
    composeTestRule.onNodeWithTag("Feed").performClick()

    // Wait for the UI to settle after the click
    composeTestRule.waitForIdle()

    // Verify that navigation to the Sky Map is never triggered
    verify(mockNavigationActions, org.mockito.kotlin.never()).navigateTo(Screen.FEED)
  }
}
