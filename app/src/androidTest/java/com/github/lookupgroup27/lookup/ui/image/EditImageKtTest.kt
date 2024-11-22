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

  @Before
  fun setUp() {
    editImageRepository = Mockito.mock(EditImageRepository::class.java)
    editImageViewModel = EditImageViewModel(editImageRepository)

    collectionRepository = Mockito.mock(CollectionRepository::class.java)
    collectionViewModel = CollectionViewModel(collectionRepository)

    postsRepository = Mockito.mock(PostsRepository::class.java)
    postsViewModel = PostsViewModel(postsRepository)
  }

  @Test
  fun testBackgroundImageIsDisplayed() {
    composeTestRule.setContent {
      EditImageScreen(
          imageUrl = "mock_image_url",
          editImageViewModel = editImageViewModel,
          collectionViewModel = collectionViewModel,
          navigationActions = mockNavigationActions,
          postsViewModel = postsViewModel)
    }

    composeTestRule.onNodeWithTag("background_image").assertIsDisplayed()
  }

  @Test
  fun testBackButtonIsDisplayedAndClickable() {
    composeTestRule.setContent {
      EditImageScreen(
          imageUrl = "mock_image_url",
          editImageViewModel = editImageViewModel,
          collectionViewModel = collectionViewModel,
          navigationActions = mockNavigationActions,
          postsViewModel = postsViewModel)
    }

    composeTestRule.onNodeWithTag("go_back_button_collection").assertIsDisplayed()
    composeTestRule.onNodeWithTag("go_back_button_collection").performClick()

    verify(mockNavigationActions).navigateTo(Screen.COLLECTION)
  }

  @Test
  fun testEditImageIsDisplayed() {
    composeTestRule.setContent {
      EditImageScreen(
          imageUrl = "mock_image_url",
          editImageViewModel = editImageViewModel,
          collectionViewModel = collectionViewModel,
          navigationActions = mockNavigationActions,
          postsViewModel = postsViewModel)
    }

    composeTestRule.onNodeWithTag("display_image").assertIsDisplayed()
  }

  @Test
  fun testEditButtonsAreDisplayedAndClickable() {
    composeTestRule.setContent {
      EditImageScreen(
          imageUrl = "mock_image_url",
          editImageViewModel = editImageViewModel,
          collectionViewModel = collectionViewModel,
          navigationActions = mockNavigationActions,
          postsViewModel = postsViewModel)
    }

    composeTestRule.onNodeWithTag("crop_button").assertIsDisplayed()
    composeTestRule.onNodeWithTag("resize_button").assertIsDisplayed()
    composeTestRule.onNodeWithTag("delete_button").assertIsDisplayed().performClick()

    verify(mockNavigationActions).navigateTo(Screen.COLLECTION)
  }

  @Test
  fun testLoadingIndicatorIsDisplayedWhenStateIsLoading() {
    editImageViewModel.setEditImageState(EditImageState.Loading)

    composeTestRule.setContent {
      EditImageScreen(
          imageUrl = "mock_image_url",
          editImageViewModel = editImageViewModel,
          collectionViewModel = collectionViewModel,
          navigationActions = mockNavigationActions,
          postsViewModel = postsViewModel)
    }

    composeTestRule.onNodeWithTag("loading_indicator").assertIsDisplayed()
  }

  @Test
  fun testDeleteButtonNavigatesToCollectionAfterDeletion() {
    editImageViewModel.setEditImageState(EditImageState.Deleted)

    composeTestRule.setContent {
      EditImageScreen(
          imageUrl = "mock_image_url",
          editImageViewModel = editImageViewModel,
          collectionViewModel = collectionViewModel,
          navigationActions = mockNavigationActions,
          postsViewModel = postsViewModel)
    }

    composeTestRule.onNodeWithTag("delete_button").assertIsDisplayed()
    composeTestRule.onNodeWithTag("delete_button").performClick()

    verify(mockNavigationActions).navigateTo(Screen.COLLECTION)
  }
}
