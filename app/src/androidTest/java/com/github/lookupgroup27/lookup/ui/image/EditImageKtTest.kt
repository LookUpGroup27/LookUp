package com.github.lookupgroup27.lookup.ui.image

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.lookupgroup27.lookup.model.collection.CollectionRepository
import com.github.lookupgroup27.lookup.model.image.EditImageRepository
import com.github.lookupgroup27.lookup.model.post.PostsRepository
import com.github.lookupgroup27.lookup.ui.navigation.NavigationActions
import com.github.lookupgroup27.lookup.ui.navigation.Screen
import com.github.lookupgroup27.lookup.ui.post.PostsViewModel
import com.github.lookupgroup27.lookup.ui.profile.CollectionViewModel
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify

/**
 * Test class for the EditImageScreen composable.
 *
 * This class verifies the functionality and behavior of the EditImageScreen, including the
 * visibility and interactivity of its UI elements, as well as its interactions with ViewModels and
 * navigation actions.
 */
@RunWith(AndroidJUnit4::class)
class EditImageScreenTest {

  @get:Rule val composeTestRule = createComposeRule()

  private lateinit var editImageRepository: EditImageRepository
  private lateinit var editImageViewModel: EditImageViewModel

  private lateinit var collectionRepository: CollectionRepository
  private lateinit var collectionViewModel: CollectionViewModel

  private lateinit var postsRepository: PostsRepository
  private lateinit var postsViewModel: PostsViewModel

  private val mockNavigationActions: NavigationActions = mock()

  /** Sets up the required mock objects and initializes the ViewModels before each test. */
  @Before
  fun setUp() {
    editImageRepository = Mockito.mock(EditImageRepository::class.java)
    editImageViewModel = EditImageViewModel(editImageRepository)

    collectionRepository = Mockito.mock(CollectionRepository::class.java)
    collectionViewModel = CollectionViewModel(collectionRepository)

    postsRepository = Mockito.mock(PostsRepository::class.java)
    postsViewModel = PostsViewModel(postsRepository)
  }

  /** Verifies that the background image is displayed in the EditImageScreen. */
  @Test
  fun testBackgroundImageIsDisplayed() {
    composeTestRule.setContent {
      EditImageScreen(
          postUri = "mock_image_url",
          postAverageStar = 0.0,
          postRatedByNb = 0,
          postUid = "mock_uid",
          editImageViewModel = editImageViewModel,
          collectionViewModel = collectionViewModel,
          navigationActions = mockNavigationActions,
          postsViewModel = postsViewModel)
    }

    composeTestRule.onNodeWithTag("background_image").assertIsDisplayed()
  }

  /**
   * Verifies that the back button is displayed and navigates to the collection screen when clicked.
   */
  @Test
  fun testBackButtonIsDisplayedAndClickable() {
    composeTestRule.setContent {
      EditImageScreen(
          postUri = "mock_image_url",
          postAverageStar = 0.0,
          postRatedByNb = 0,
          postUid = "mock_uid",
          editImageViewModel = editImageViewModel,
          collectionViewModel = collectionViewModel,
          navigationActions = mockNavigationActions,
          postsViewModel = postsViewModel)
    }

    composeTestRule.onNodeWithTag("go_back_button_collection").assertIsDisplayed()
    composeTestRule.onNodeWithTag("go_back_button_collection").performClick()

    verify(mockNavigationActions).navigateTo(Screen.COLLECTION)
  }

  /** Verifies that the image to be edited is displayed in the EditImageScreen. */
  @Test
  fun testEditImageIsDisplayed() {
    composeTestRule.setContent {
      EditImageScreen(
          postUri = "mock_image_url",
          postAverageStar = 0.0,
          postRatedByNb = 0,
          postUid = "mock_uid",
          editImageViewModel = editImageViewModel,
          collectionViewModel = collectionViewModel,
          navigationActions = mockNavigationActions,
          postsViewModel = postsViewModel)
    }

    composeTestRule.onNodeWithTag("display_image").assertIsDisplayed()
  }

  /** Verifies that all edit buttons (crop, resize, delete) are displayed and clickable. */
  @Test
  fun testEditButtonsAreDisplayedAndClickable() {
    composeTestRule.setContent {
      EditImageScreen(
          postUri = "mock_image_url",
          postAverageStar = 0.0,
          postRatedByNb = 0,
          postUid = "mock_uid",
          editImageViewModel = editImageViewModel,
          collectionViewModel = collectionViewModel,
          navigationActions = mockNavigationActions,
          postsViewModel = postsViewModel)
    }

    composeTestRule.onNodeWithTag("delete_button").assertIsDisplayed().performClick()

    verify(mockNavigationActions).navigateTo(Screen.COLLECTION)
  }

  /** Verifies that the loading indicator is displayed when the state is set to Loading. */
  @Test
  fun testLoadingIndicatorIsDisplayedWhenStateIsLoading() {
    editImageViewModel.setEditImageState(EditImageState.Loading)

    composeTestRule.setContent {
      EditImageScreen(
          postUri = "mock_image_url",
          postAverageStar = 0.0,
          postRatedByNb = 0,
          postUid = "mock_uid",
          editImageViewModel = editImageViewModel,
          collectionViewModel = collectionViewModel,
          navigationActions = mockNavigationActions,
          postsViewModel = postsViewModel)
    }

    composeTestRule.onNodeWithTag("loading_indicator").assertIsDisplayed()
  }

  /** Verifies that clicking the delete button navigates to the collection screen after deletion. */
  @Test
  fun testDeleteButtonNavigatesToCollectionAfterDeletion() {
    editImageViewModel.setEditImageState(EditImageState.Deleted)

    composeTestRule.setContent {
      EditImageScreen(
          postUri = "mock_image_url",
          postAverageStar = 0.0,
          postRatedByNb = 0,
          postUid = "mock_uid",
          editImageViewModel = editImageViewModel,
          collectionViewModel = collectionViewModel,
          navigationActions = mockNavigationActions,
          postsViewModel = postsViewModel)
    }

    composeTestRule.onNodeWithTag("delete_button").assertIsDisplayed()
    composeTestRule.onNodeWithTag("delete_button").performClick()

    verify(mockNavigationActions).navigateTo(Screen.COLLECTION)
  }

  /** Verifies that the star icon is displayed on the EditImageScreen. */
  @Test
  fun testStarIconIsDisplayed() {
    composeTestRule.setContent {
      EditImageScreen(
          postUri = "mock_image_url",
          postAverageStar = 0.0,
          postRatedByNb = 0,
          postUid = "mock_uid",
          editImageViewModel = editImageViewModel,
          collectionViewModel = collectionViewModel,
          navigationActions = mockNavigationActions,
          postsViewModel = postsViewModel)
    }

    composeTestRule.onNodeWithTag("star_collection").assertIsDisplayed()
  }

  /** Verifies that the average rating is displayed correctly on the EditImageScreen. */
  @Test
  fun testAverageRatingIsDisplayed() {
    composeTestRule.setContent {
      EditImageScreen(
          postUri = "mock_image_url",
          postAverageStar = 0.0,
          postRatedByNb = 0,
          postUid = "mock_uid",
          editImageViewModel = editImageViewModel,
          collectionViewModel = collectionViewModel,
          navigationActions = mockNavigationActions,
          postsViewModel = postsViewModel)
    }

    composeTestRule.onNodeWithTag("average_rating_collection").assertIsDisplayed()
  }

  /** Verifies that the user icon is displayed on the EditImageScreen. */
  @Test
  fun testUserIconIsDisplayed() {
    composeTestRule.setContent {
      EditImageScreen(
          postUri = "mock_image_url",
          postAverageStar = 0.0,
          postRatedByNb = 0,
          postUid = "mock_uid",
          editImageViewModel = editImageViewModel,
          collectionViewModel = collectionViewModel,
          navigationActions = mockNavigationActions,
          postsViewModel = postsViewModel)
    }

    composeTestRule.onNodeWithTag("user_icon_collection").assertIsDisplayed()
  }

  /** Verifies that the number of users who rated is displayed correctly on the EditImageScreen. */
  @Test
  fun testRatedByNumberIsDisplayed() {
    composeTestRule.setContent {
      EditImageScreen(
          postUri = "mock_image_url",
          postAverageStar = 0.0,
          postRatedByNb = 0,
          postUid = "mock_uid",
          editImageViewModel = editImageViewModel,
          collectionViewModel = collectionViewModel,
          navigationActions = mockNavigationActions,
          postsViewModel = postsViewModel)
    }

    composeTestRule.onNodeWithTag("rated_by_collection").assertIsDisplayed()
  }
}
