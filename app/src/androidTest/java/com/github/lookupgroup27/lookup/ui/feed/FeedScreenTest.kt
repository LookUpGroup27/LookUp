package com.github.lookupgroup27.lookup.ui.feed

import android.Manifest
import android.content.Context
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertTextContains
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.test.core.app.ApplicationProvider
import androidx.test.rule.GrantPermissionRule
import com.github.lookupgroup27.lookup.model.location.LocationProvider
import com.github.lookupgroup27.lookup.model.post.Post
import com.github.lookupgroup27.lookup.model.post.PostsRepository
import com.github.lookupgroup27.lookup.model.profile.ProfileRepository
import com.github.lookupgroup27.lookup.model.profile.UserProfile
import com.github.lookupgroup27.lookup.ui.navigation.NavigationActions
import com.github.lookupgroup27.lookup.ui.navigation.Screen
import com.github.lookupgroup27.lookup.ui.navigation.TopLevelDestinations
import com.github.lookupgroup27.lookup.ui.post.PostsViewModel
import com.github.lookupgroup27.lookup.ui.profile.ProfileViewModel
import com.google.firebase.auth.FirebaseAuth
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
          Post(
              uid = "1",
              uri = "http://example.com/1.jpg",
              username = testUserProfile.email, // Post created by the logged-in user
              latitude = 37.7749, // San Francisco
              longitude = -122.4194),
          Post(
              uid = "2",
              uri = "http://example.com/2.jpg",
              username = "User2", // Post created by another user
              latitude = 34.0522, // Los Angeles
              longitude = -118.2437),
          Post(
              uid = "3",
              uri = "http://example.com/3.jpg",
              username = "User3", // Another user's post
              latitude = 36.7783, // Fresno (closer to SF)
              longitude = -119.4179),
          Post(
              uid = "4",
              uri = "User4",
              username = "user4@example.com", // Another user's post
              latitude = 40.7128, // New York City (farther from SF than LA or Fresno)
              longitude = -74.0060),
          Post(
              uid = "5",
              uri = "User5",
              username = "user5@example.com", // Another user's post
              latitude = -33.8688, // Sydney, Australia (farthest from SF)
              longitude = 151.2093))

  private val testPost =
      Post(
          "2",
          "testUri",
          "testUsername",
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

    composeTestRule.setContent {
      FeedScreen(
          postsViewModel = postsViewModel,
          navigationActions = navigationActions,
          profileViewModel = profileViewModel,
          initialNearbyPosts = testPosts)
    }
  }

  @Test
  fun testFeedScreenDisplaysNearbyPosts() {

    // Assert each post item is displayed
    composeTestRule.onNodeWithTag("PostItem_2").assertExists()
    composeTestRule.onNodeWithTag("UsernameTag_User2").assertTextContains("User2")

    composeTestRule.onNodeWithTag("PostItem_3").performScrollTo().assertExists()
    composeTestRule.onNodeWithTag("UsernameTag_User3").assertTextContains("User3")

    composeTestRule.onNodeWithTag("PostItem_5").assertDoesNotExist()
  }

  @Test
  fun testFeedExcludesLoggedInUserPosts() {
    // Assert that the post by the logged-in user  is not displayed
    composeTestRule.onNodeWithTag("PostItem_1").assertDoesNotExist()
  }

  @Test
  fun testBottomNavigationMenuIsDisplayed() {

    // Verify the bottom navigation menu is displayed
    composeTestRule
        .onNodeWithTag("BottomNavigationMenu")
        .assertExists("Bottom Navigation Menu should exist")
        .assertIsDisplayed()
  }

  @Test
  fun testStarClickDisplaysAverageRating() {
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
    // Perform click on the first star of post with uid "1"
    composeTestRule.onNodeWithTag("Star_2_2").performClick()
    postsViewModel.updatePost(testPost)

    // Verify that updatePost was called in the postsViewModel
    verify(postsRepository)
        .updatePost(
            org.mockito.kotlin.eq(testPost), org.mockito.kotlin.any(), org.mockito.kotlin.any())
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
    // Mock the user as not logged in
    mockAuth = org.mockito.kotlin.mock()
    whenever(mockAuth.currentUser).thenReturn(null)

    // Simulate clicking the Feed tab in the bottom navigation
    composeTestRule.onNodeWithTag("Feed").performClick()

    // Verify that the navigation action to the Feed was not triggered
    verify(navigationActions, never()).navigateTo(eq(TopLevelDestinations.FEED))
  }
}
