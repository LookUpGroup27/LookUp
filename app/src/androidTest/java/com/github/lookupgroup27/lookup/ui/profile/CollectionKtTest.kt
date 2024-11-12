package com.github.lookupgroup27.lookup.ui.profile

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.navigation.NavHostController
import androidx.test.core.app.ApplicationProvider
import com.github.lookupgroup27.lookup.model.collection.CollectionRepository
import com.github.lookupgroup27.lookup.ui.navigation.NavigationActions
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

class MockCollectionRepository : CollectionRepository {
  override fun init(onSuccess: () -> Unit) {
    onSuccess()
  }

  override suspend fun getUserImageUrls(): List<String> {
    return listOf("https://example.com/image1.jpg", "https://example.com/image2.jpg")
  }
}

@OptIn(ExperimentalCoroutinesApi::class)
class CollectionScreenTest {

  @get:Rule val composeTestRule = createComposeRule()

  private fun createMockViewModel(): CollectionViewModel {
    val repository = MockCollectionRepository()
    return CollectionViewModel(repository)
  }

  @Test
  fun backgroundBox_isDisplayed() {
    composeTestRule.setContent {
      CollectionScreen(
          navigationActions = mockNavigationActions(), viewModel = createMockViewModel())
    }
    composeTestRule.onNodeWithTag("background_box").assertIsDisplayed()
  }

  @Test
  fun backgroundImage_isDisplayed() {
    composeTestRule.setContent {
      CollectionScreen(
          navigationActions = mockNavigationActions(), viewModel = createMockViewModel())
    }
    composeTestRule.onNodeWithTag("background_image").assertIsDisplayed()
  }

  @Test
  fun goBackButton_isDisplayed() {
    composeTestRule.setContent {
      CollectionScreen(
          navigationActions = mockNavigationActions(), viewModel = createMockViewModel())
    }
    composeTestRule.onNodeWithTag("go_back_button_collection").assertIsDisplayed()
  }

  @Test
  fun titleText_isDisplayed() {
    composeTestRule.setContent {
      CollectionScreen(
          navigationActions = mockNavigationActions(), viewModel = createMockViewModel())
    }
    composeTestRule.onNodeWithTag("title_text").assertIsDisplayed()
  }

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

  @Test
  fun goBackButton_callsGoBack() {
    var backButtonClicked = false
    val mockNavigationActions =
        object :
            NavigationActions(
                navController = NavHostController(ApplicationProvider.getApplicationContext())) {
          override fun goBack() {
            backButtonClicked = true
          }
        }

    composeTestRule.setContent {
      CollectionScreen(navigationActions = mockNavigationActions, viewModel = createMockViewModel())
    }
    composeTestRule.onNodeWithTag("go_back_button_collection").performClick()
    assertTrue(backButtonClicked)
  }

  @Test
  fun imageRow_isDisplayed_whenImageUrlsAreNotEmpty() {
    composeTestRule.setContent {
      CollectionScreen(
          navigationActions = mockNavigationActions(), viewModel = createMockViewModel())
    }

    composeTestRule.onNodeWithTag("image_row_0").assertIsDisplayed()
  }

  @Test
  fun imageBox_isDisplayed_forEachImage() {
    composeTestRule.setContent {
      CollectionScreen(
          navigationActions = mockNavigationActions(), viewModel = createMockViewModel())
    }

    composeTestRule.onNodeWithTag("image_box_0_0").assertIsDisplayed()
    composeTestRule.onNodeWithTag("image_box_0_1").assertIsDisplayed()
  }

  private fun mockNavigationActions() =
      NavigationActions(
          navController = NavHostController(ApplicationProvider.getApplicationContext()))
}
