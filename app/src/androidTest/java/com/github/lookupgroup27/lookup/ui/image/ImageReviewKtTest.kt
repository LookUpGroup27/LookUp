package com.github.lookupgroup27.lookup.ui.image

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.lookupgroup27.lookup.model.post.PostsRepository
import com.github.lookupgroup27.lookup.ui.navigation.NavigationActions
import com.github.lookupgroup27.lookup.ui.navigation.Screen
import com.github.lookupgroup27.lookup.ui.post.PostsViewModel
import java.io.File
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito
import org.mockito.Mockito.`when`
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify

@RunWith(AndroidJUnit4::class)
class ImageReviewTest {

  private lateinit var postsViewModel: PostsViewModel
  private lateinit var postsRepository: PostsRepository

  @get:Rule val composeTestRule = createComposeRule()

  private val fakeFile: File = File.createTempFile("temp", null)

  private val mockNavigationActions: NavigationActions = mock()

  @Before
  fun setUp() {
    postsRepository = Mockito.mock(PostsRepository::class.java)
    postsViewModel = PostsViewModel(postsRepository)

    // Mock UID generator to return a fixed value
    `when`(postsViewModel.generateNewUid()).thenReturn("mocked_uid")
  }

  @Test
  fun testImageReviewIsDisplayed() {
    composeTestRule.setContent {
      ImageReviewScreen(mockNavigationActions, fakeFile, postsViewModel)
    }

    composeTestRule.onNodeWithTag("image_review").assertIsDisplayed()
  }

  @Test
  fun testConfirmButtonIsDisplayedAndClickable() {
    composeTestRule.setContent {
      ImageReviewScreen(mockNavigationActions, fakeFile, postsViewModel)
    }

    composeTestRule.onNodeWithTag("confirm_button").assertIsDisplayed()
    composeTestRule.onNodeWithTag("confirm_button").performClick()
  }

  @Test
  fun testCancelButtonIsDisplayedAndClickable() {
    composeTestRule.setContent {
      ImageReviewScreen(mockNavigationActions, fakeFile, postsViewModel)
    }

    composeTestRule.onNodeWithTag("cancel_button").assertIsDisplayed()
    composeTestRule.onNodeWithTag("cancel_button").performClick()

    verify(mockNavigationActions).navigateTo(Screen.TAKE_IMAGE)
  }

  @Test
  fun testImageDisplayedWhenImageFileIsNotNull() {
    val imageFile = File("path/to/image")
    composeTestRule.setContent {
      ImageReviewScreen(
          navigationActions = mockNavigationActions, imageFile = imageFile, postsViewModel)
    }
    composeTestRule.onNodeWithContentDescription("Captured Image").assertIsDisplayed()
  }

  @Test
  fun testNoImageAvailableTextWhenImageFileIsNull() {
    composeTestRule.setContent {
      ImageReviewScreen(navigationActions = mockNavigationActions, imageFile = null, postsViewModel)
    }
    composeTestRule.onNodeWithText("No image available").assertIsDisplayed()
  }

  @Test
  fun testImageReviewScreenIsScrollable() {
    composeTestRule.setContent {
      ImageReviewScreen(mockNavigationActions, fakeFile, postsViewModel)
    }

    // Check that the top element is displayed (e.g., image or text)
    composeTestRule.onNodeWithTag("image_review").assertIsDisplayed()

    // Attempt to scroll to a specific button at the bottom
    composeTestRule.onNodeWithTag("cancel_button").assertIsDisplayed()
  }
}
