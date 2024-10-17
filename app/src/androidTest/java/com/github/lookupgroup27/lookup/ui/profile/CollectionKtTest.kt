package com.github.lookupgroup27.lookup.ui.profile

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.navigation.NavHostController
import androidx.test.core.app.ApplicationProvider
import com.github.lookupgroup27.lookup.ui.navigation.NavigationActions
import org.junit.Rule
import org.junit.Test

class CollectionKtTest {

  @get:Rule val composeTestRule = createComposeRule()

  @Test
  fun collectionScreen_displaysCorrectly() {
    // Set up the CollectionScreen in the test
    composeTestRule.setContent {
      val mockNavigationActions =
          NavigationActions(
              navController = NavHostController(ApplicationProvider.getApplicationContext()))
      CollectionScreen(navigationActions = mockNavigationActions)
    }

    // Check if the text "Collection Screen" is displayed
    composeTestRule.onNodeWithText("Collection Screen").assertIsDisplayed()

    // Check if the back button with the tag "go_back_button_collection" is displayed
    composeTestRule.onNodeWithTag("go_back_button_collection").assertIsDisplayed()
  }

  @Test
  fun collectionScreen_backButton_callsGoBack() {
    // Mock the goBack action
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

    // Perform click on the back button with the tag "go_back_button_collection"
    composeTestRule.onNodeWithTag("go_back_button_collection").performClick()

    // Verify that the navigation action was triggered
    assert(backButtonClicked)
  }
}
