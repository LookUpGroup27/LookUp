package com.github.lookupgroup27.lookup.ui.profile

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.github.lookupgroup27.lookup.ui.navigation.NavigationActions
import org.junit.Rule
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify

class ProfileScreenTest {

  @get:Rule val composeTestRule = createComposeRule()

  private val mockNavigationActions: NavigationActions = mock()

  @Test
  fun profileScreen_displaysProfileTextAndBackButton() {
    composeTestRule.setContent { ProfileScreen(navigationActions = mockNavigationActions) }

    // Verify that the profile screen text is displayed
    composeTestRule.onNodeWithText("Profile Screen").assertIsDisplayed()

    // Verify that the back button is displayed
    composeTestRule.onNodeWithTag("go_back_button_profile").assertIsDisplayed()
  }

  @Test
  fun profileScreen_clickBackButton_navigatesBack() {
    composeTestRule.setContent { ProfileScreen(navigationActions = mockNavigationActions) }

    // Click on back button
    composeTestRule.onNodeWithTag("go_back_button_profile").performClick()

    // Verify that the navigation back action is triggered
    verify(mockNavigationActions).goBack()
  }
}
