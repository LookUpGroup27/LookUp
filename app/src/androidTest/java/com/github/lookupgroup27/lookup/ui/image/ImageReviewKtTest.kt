package com.github.lookupgroup27.lookup.ui.image

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.lookupgroup27.lookup.model.collection.CollectionRepository
import com.github.lookupgroup27.lookup.model.image.ImageRepository
import com.github.lookupgroup27.lookup.model.post.PostsRepository
import com.github.lookupgroup27.lookup.ui.navigation.NavigationActions
import com.github.lookupgroup27.lookup.ui.navigation.Screen
import com.github.lookupgroup27.lookup.ui.post.PostsViewModel
import com.github.lookupgroup27.lookup.ui.profile.CollectionViewModel
import java.io.File
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify

@RunWith(AndroidJUnit4::class)
class ImageReviewTest {

  private lateinit var postsViewModel: PostsViewModel
  private lateinit var postsRepository: PostsRepository

  private lateinit var collectionViewModel: CollectionViewModel
  private lateinit var collectionRepository: CollectionRepository

  private lateinit var imageViewModel: ImageViewModel
  private lateinit var imageRepository: ImageRepository

  @get:Rule val composeTestRule = createComposeRule()

  private val fakeFile: File = File.createTempFile("temp", null)
  private val mockNavigationActions: NavigationActions = mock()

  @Before
  fun setUp() {
    postsRepository = Mockito.mock(PostsRepository::class.java)
    postsViewModel = PostsViewModel(postsRepository)

    imageRepository = Mockito.mock(ImageRepository::class.java)
    imageViewModel = ImageViewModel(imageRepository)

    collectionRepository = Mockito.mock(CollectionRepository::class.java)
    collectionViewModel = CollectionViewModel(collectionRepository)
  }

  @Test
  fun testImageReviewScreenIsDisplayed() {
    composeTestRule.setContent {
      ImageReviewScreen(
          mockNavigationActions,
          fakeFile,
          imageViewModel,
          postsViewModel,
          collectionViewModel,
          timestamp = 123456789L)
    }
    composeTestRule.onNodeWithTag("image_review").assertIsDisplayed()
  }

  @Test
  fun testConfirmButtonIsDisplayedAndClickable() {
    composeTestRule.setContent {
      ImageReviewScreen(
          mockNavigationActions,
          fakeFile,
          imageViewModel,
          postsViewModel,
          collectionViewModel,
          timestamp = 123456789L)
    }
    composeTestRule.onNodeWithTag("confirm_button").performScrollTo().assertIsDisplayed()
    composeTestRule.onNodeWithTag("confirm_button").performClick()
  }

  @Test
  fun testCancelButtonIsDisplayedAndClickable() {
    composeTestRule.setContent {
      ImageReviewScreen(
          mockNavigationActions,
          fakeFile,
          imageViewModel,
          postsViewModel,
          collectionViewModel,
          timestamp = 123456789L)
    }
    composeTestRule.onNodeWithTag("cancel_button").performScrollTo().assertIsDisplayed()
    composeTestRule.onNodeWithTag("cancel_button").performClick()

    verify(mockNavigationActions).navigateTo(Screen.TAKE_IMAGE)
  }

  @Test
  fun testDescriptionFieldIsDisplayedAndEditable() {
    composeTestRule.setContent {
      ImageReviewScreen(
          mockNavigationActions,
          fakeFile,
          imageViewModel,
          postsViewModel,
          collectionViewModel,
          timestamp = 123456789L)
    }
      composeTestRule.onNodeWithTag("description_title").assertIsDisplayed()
    // Initially, the description field is displayed in read-only mode
    composeTestRule.onNodeWithTag("description_text").assertIsDisplayed().performClick()

    // Enter edit mode and input text
    composeTestRule
        .onNodeWithTag("edit_description_field")
        .assertIsDisplayed()
        .performTextInput("New Description")

    // Verify that the input text is displayed correctly
    composeTestRule.onNodeWithTag("edit_description_field").assert(hasText("New Description"))
  }

  @Test
  fun testLoadingIndicatorReplacesPostButtonWhenUploading() {
    // Simulate loading state
    imageViewModel.setEditImageState(ImageViewModel.UploadStatus(isLoading = true))

    composeTestRule.setContent {
      ImageReviewScreen(
          mockNavigationActions,
          fakeFile,
          imageViewModel,
          postsViewModel,
          collectionViewModel,
          timestamp = 123456789L)
    }

    // Verify that the loading indicator is displayed
    composeTestRule.onNodeWithTag("loading_indicator").assertIsDisplayed()

    // Verify that the Post button is not displayed during loading
    composeTestRule.onNodeWithTag("confirm_button").assertDoesNotExist()
  }

  @Test
  fun testImageDisplayedWhenImageFileIsNotNull() {
    val imageFile = File("path/to/image")
    composeTestRule.setContent {
      ImageReviewScreen(
          navigationActions = mockNavigationActions,
          imageFile = imageFile,
          imageViewModel,
          postsViewModel,
          collectionViewModel,
          timestamp = 123456789L)
    }
    composeTestRule.onNodeWithContentDescription("Captured Image").assertIsDisplayed()
  }

  @Test
  fun testNoImageAvailableTextWhenImageFileIsNull() {
    composeTestRule.setContent {
      ImageReviewScreen(
          navigationActions = mockNavigationActions,
          imageFile = null,
          imageViewModel,
          postsViewModel,
          collectionViewModel,
          timestamp = 123456789L)
    }
    composeTestRule.onNodeWithText("No image available").assertIsDisplayed()
  }

  @Test
  fun testDiscardButtonNavigatesBack() {
    composeTestRule.setContent {
      ImageReviewScreen(
          mockNavigationActions,
          fakeFile,
          imageViewModel,
          postsViewModel,
          collectionViewModel,
          timestamp = 123456789L)
    }

    composeTestRule.onNodeWithTag("cancel_button").performClick()
    verify(mockNavigationActions).navigateTo(Screen.TAKE_IMAGE)
  }

  @Test
  fun testBackgroundImageIsDisplayed() {
    composeTestRule.setContent {
      ImageReviewScreen(
          mockNavigationActions,
          fakeFile,
          imageViewModel,
          postsViewModel,
          collectionViewModel,
          timestamp = 123456789L)
    }
    composeTestRule.onNodeWithTag("background_image").assertIsDisplayed()
  }

    @Test
    fun testTitleIsDisplayed() {
        composeTestRule.setContent {
            ImageReviewScreen(
                navigationActions = mockNavigationActions,
                imageFile = fakeFile,
                imageViewModel,
                postsViewModel,
                collectionViewModel,
                timestamp = 123456789L
            )
        }

        // Verify that the title "Post Your Picture" is displayed
        composeTestRule.onNodeWithTag("post_picture_title").assertIsDisplayed()
    }
}


