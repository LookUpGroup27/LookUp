/**
 * Unit tests for the `CollectionScreen` composable.
 *
 * This class verifies the behavior and UI elements of the `CollectionScreen`, ensuring it displays
 * the correct components and responds to user interactions as expected.
 */
package com.github.lookupgroup27.lookup.ui.profile

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.navigation.NavHostController
import androidx.test.core.app.ApplicationProvider
import com.github.lookupgroup27.lookup.model.collection.CollectionRepository
import com.github.lookupgroup27.lookup.ui.navigation.NavigationActions
import com.github.lookupgroup27.lookup.ui.navigation.Screen
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

/** Mock implementation of `CollectionRepository` to provide test data. */
class MockCollectionRepository : CollectionRepository {
  override fun init(onSuccess: () -> Unit) {
    onSuccess()
  }

  override suspend fun getUserImageUrls(): List<String> {
    return listOf("https://example.com/image1.jpg", "https://example.com/image2.jpg")
  }
}

/** Unit tests for the `CollectionScreen` composable. */
@OptIn(ExperimentalCoroutinesApi::class)
class CollectionScreenTest {

  /**
   * Provides the test rule for Compose UI testing.
   *
   * The `createComposeRule` allows us to set and test Compose content.
   */
  @get:Rule val composeTestRule = createComposeRule()

  /**
   * Creates a mock `CollectionViewModel` using a `MockCollectionRepository`.
   *
   * @return a `CollectionViewModel` initialized with a mock repository.
   */
  private fun createMockViewModel(): CollectionViewModel {
    val repository = MockCollectionRepository()
    return CollectionViewModel(repository)
  }

  /** Tests that the background box is displayed in the `CollectionScreen`. */
  @Test
  fun backgroundBox_isDisplayed() {
    composeTestRule.setContent {
      CollectionScreen(
          navigationActions = mockNavigationActions(), viewModel = createMockViewModel())
    }
    composeTestRule.onNodeWithTag("background_box").assertIsDisplayed()
  }

  /** Tests that the background image is displayed in the `CollectionScreen`. */
  @Test
  fun backgroundImage_isDisplayed() {
    composeTestRule.setContent {
      CollectionScreen(
          navigationActions = mockNavigationActions(), viewModel = createMockViewModel())
    }
    composeTestRule.onNodeWithTag("background_image").assertIsDisplayed()
  }

  /** Tests that the go-back button is displayed in the `CollectionScreen`. */
  @Test
  fun goBackButton_isDisplayed() {
    composeTestRule.setContent {
      CollectionScreen(
          navigationActions = mockNavigationActions(), viewModel = createMockViewModel())
    }
    composeTestRule.onNodeWithTag("go_back_button_collection").assertIsDisplayed()
  }

  /** Tests that the title text is displayed in the `CollectionScreen`. */
  @Test
  fun titleText_isDisplayed() {
    composeTestRule.setContent {
      CollectionScreen(
          navigationActions = mockNavigationActions(), viewModel = createMockViewModel())
    }
    composeTestRule.onNodeWithTag("title_text").assertIsDisplayed()
  }

  /** Tests that the "no images" message is displayed when the image URL list is empty. */
  @Test
  fun noImagesText_isDisplayed_whenImageUrlsAreEmpty() {
    val emptyRepository =
        object : CollectionRepository {
          override fun init(onSuccess: () -> Unit) {
            onSuccess()
          }

          override suspend fun getUserImageUrls(): List<String> = emptyList()
        }

    val viewModel = CollectionViewModel(emptyRepository)

    composeTestRule.setContent {
      CollectionScreen(navigationActions = mockNavigationActions(), viewModel = viewModel)
    }
    composeTestRule.onNodeWithTag("no_images_text").assertIsDisplayed()
  }

  /** Tests that clicking the go-back button navigates to the profile screen. */
  @Test
  fun goBackButton_navigatesToProfile() {
    var navigatedToProfile = false
    val mockNavigationActions =
        object :
            NavigationActions(
                navController = NavHostController(ApplicationProvider.getApplicationContext())) {
          override fun navigateTo(screen: String) {
            if (screen == Screen.PROFILE) {
              navigatedToProfile = true
            }
          }
        }

    composeTestRule.setContent {
      CollectionScreen(navigationActions = mockNavigationActions, viewModel = createMockViewModel())
    }

    composeTestRule.onNodeWithTag("go_back_button_collection").performClick()
    assertTrue(navigatedToProfile)
  }

  /** Tests that image rows are displayed when the image URL list is not empty. */
  @Test
  fun imageRow_isDisplayed_whenImageUrlsAreNotEmpty() {
    composeTestRule.setContent {
      CollectionScreen(
          navigationActions = mockNavigationActions(), viewModel = createMockViewModel())
    }

    composeTestRule.onNodeWithTag("image_row_0").assertIsDisplayed()
  }

  /** Tests that each image box is displayed for the image URLs in the list. */
  @Test
  fun imageBox_isDisplayed_forEachImage() {
    composeTestRule.setContent {
      CollectionScreen(
          navigationActions = mockNavigationActions(), viewModel = createMockViewModel())
    }

    composeTestRule.onNodeWithTag("image_box_0_0").assertIsDisplayed()
    composeTestRule.onNodeWithTag("image_box_0_1").assertIsDisplayed()
  }

  /**
   * Creates mock navigation actions using a mock `NavHostController`.
   *
   * @return a mock `NavigationActions` instance.
   */
  private fun mockNavigationActions() =
      NavigationActions(
          navController = NavHostController(ApplicationProvider.getApplicationContext()))
}
