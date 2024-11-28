package com.github.lookupgroup27.lookup.ui.image

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import com.github.lookupgroup27.lookup.model.post.*
import com.github.lookupgroup27.lookup.model.profile.*
import com.github.lookupgroup27.lookup.ui.post.PostsViewModel
import com.github.lookupgroup27.lookup.ui.profile.ProfileViewModel
import org.junit.*
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.kotlin.any
import org.mockito.kotlin.eq
import org.mockito.kotlin.verify

class ImagePreviewDialogTest {

  @get:Rule val composeTestRule = createComposeRule()
  private lateinit var postsRepository: PostsRepository
  @Mock private lateinit var postsViewModel: PostsViewModel
  private lateinit var profileRepository: ProfileRepository
  @Mock private lateinit var profileViewModel: ProfileViewModel

  private val testPost =
      Post(
          "1",
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
  private val testStarStates = listOf(true, false, false)
  private val testPosts =
      listOf(
          Post(
              uid = "1",
              uri = "http://example.com/1.jpg",
              username = "User1",
              latitude = 37.7749,
              longitude = -122.4194), // San Francisco
          Post(
              uid = "2",
              uri = "http://example.com/2.jpg",
              username = "User2",
              latitude = 34.0522,
              longitude = -118.2437) // Los Angeles
          )

  @Before
  fun setUp() {

    // Mock the repository and other dependencies
    postsRepository = mock(PostsRepository::class.java)
    postsViewModel = PostsViewModel(postsRepository)
    profileRepository = mock(ProfileRepository::class.java)
    profileViewModel = ProfileViewModel(profileRepository)

    // Define behavior for getPosts to immediately invoke the success callback with testPosts
    `when`(postsRepository.getPosts(any(), any())).thenAnswer { invocation ->
      val onSuccessCallback = invocation.getArgument<(List<Post>?) -> Unit>(0)
      onSuccessCallback(testPosts)
    }
  }

  @Test
  fun imagePreviewDialogDisplaysWithValidUri() {
    val fakeUsername = "User1"

    // Set the Compose content to ImagePreviewDialog
    composeTestRule.setContent {
      ImagePreviewDialog(
          post = testPost,
          username = fakeUsername,
          onDismiss = {},
          starStates = testStarStates,
          onRatingChanged = {})
    }

    // Verify that the dialog is displayed
    composeTestRule.onNodeWithTag("imagePreviewDialog").assertIsDisplayed()
    // Verify that the username is displayed
    composeTestRule.onNodeWithText("Posted by: $fakeUsername").assertIsDisplayed()
  }

  @Test
  fun imagePreviewDialogCloseButtonWorks() {
    var dialogDismissed = false

    // Set the Compose content to ImagePreviewDialog
    composeTestRule.setContent {
      ImagePreviewDialog(
          post = testPost,
          username = "User1",
          onDismiss = { dialogDismissed = true },
          testStarStates,
          onRatingChanged = {})
    }

    // Perform click on the "Close" button
    composeTestRule.onNodeWithText("Close").performClick()

    // Verify that the dialog was dismissed
    assert(dialogDismissed)
  }

  @Test
  fun testStarClickCallsUpdatePost() {
    // Set the Compose content to ImagePreviewDialog
    composeTestRule.setContent {
      ImagePreviewDialog(
          post = testPost, username = "User1", onDismiss = {}, testStarStates, onRatingChanged = {})
    }
    // Perform click on the first star of post with uid "1"
    composeTestRule.onNodeWithTag("Star_1_1").performScrollTo().performClick()
    postsViewModel.updatePost(testPost)

    // Verify that updatePost was called in the postsViewModel
    verify(postsRepository).updatePost(eq(testPost), any(), any())
  }

  @Test
  fun testStarClickCallsUpdateUserProfile() {
    composeTestRule.setContent {
      ImagePreviewDialog(
          post = testPost, username = "User1", onDismiss = {}, testStarStates, onRatingChanged = {})
    }
    // Perform click on the first star of post with uid "1"
    composeTestRule.onNodeWithTag("Star_1_1").performClick()

    profileViewModel.updateUserProfile(testProfile)
    // Verify that `updateUserProfile` was called in the profileViewModel
    verify(profileRepository).updateUserProfile(eq(testProfile), any(), any())
  }

  @Test
  fun testStarIsDisplayed() {
    composeTestRule.setContent {
      ImagePreviewDialog(
          post = testPost, username = "User1", onDismiss = {}, testStarStates, onRatingChanged = {})
    }
    // Perform click on the first star icon of a post with uid "1"
    composeTestRule
        .onNodeWithTag("Star_1_1")
        .assertIsDisplayed()
        .performClick() // Click on the first star
  }
}
