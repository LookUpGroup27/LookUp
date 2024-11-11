package com.github.lookupgroup27.lookup.ui.profile

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.navigation.NavHostController
import androidx.test.core.app.ApplicationProvider
import com.github.lookupgroup27.lookup.ui.navigation.NavigationActions
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito.*

@OptIn(ExperimentalCoroutinesApi::class)
class CollectionScreenTest {

  @get:Rule val composeTestRule = createComposeRule()

  @Test
  fun backgroundBox_isDisplayed() {
    composeTestRule.setContent { CollectionScreen(navigationActions = mockNavigationActions()) }
    composeTestRule.onNodeWithTag("background_box").assertIsDisplayed()
  }

  @Test
  fun backgroundImage_isDisplayed() {
    composeTestRule.setContent { CollectionScreen(navigationActions = mockNavigationActions()) }
    composeTestRule.onNodeWithTag("background_image").assertIsDisplayed()
  }

  @Test
  fun goBackButton_isDisplayed() {
    composeTestRule.setContent { CollectionScreen(navigationActions = mockNavigationActions()) }
    composeTestRule.onNodeWithTag("go_back_button_collection").assertIsDisplayed()
  }

  @Test
  fun titleText_isDisplayed() {
    composeTestRule.setContent { CollectionScreen(navigationActions = mockNavigationActions()) }
    composeTestRule.onNodeWithTag("title_text").assertIsDisplayed()
  }

  @Test
  fun noImagesText_isDisplayed_whenImageUrlsAreEmpty() {
    composeTestRule.setContent { CollectionScreen(navigationActions = mockNavigationActions()) }
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

    composeTestRule.setContent { CollectionScreen(navigationActions = mockNavigationActions) }
    composeTestRule.onNodeWithTag("go_back_button_collection").performClick()
    assertTrue(backButtonClicked)
  }

  private fun mockNavigationActions() =
      NavigationActions(
          navController = NavHostController(ApplicationProvider.getApplicationContext()))
}
