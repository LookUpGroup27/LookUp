package com.github.lookupgroup27.lookup.ui.profile

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.github.lookupgroup27.lookup.R
import com.github.lookupgroup27.lookup.ui.profile.components.ChangeAvatarButton
import org.junit.Rule
import org.junit.Test

class ChangeAvatarButtonTest {

  @get:Rule val composeTestRule = createComposeRule()

  @Test
  fun changeAvatarButton_isDisplayed_whenAvatarIsNotDefaultOrNull() {
    // Set up the test
    composeTestRule.setContent {
      ChangeAvatarButton(
          selectedAvatar = R.drawable.avatar1, isAvatarDefaultOrNull = false, onButtonClick = {})
    }

    // Verify the button is displayed
    composeTestRule.onNodeWithText("Change Avatar").assertIsDisplayed()
  }

  @Test
  fun changeAvatarButton_isNotDisplayed_whenAvatarIsDefaultOrNull() {
    // Set up the test
    composeTestRule.setContent {
      ChangeAvatarButton(
          selectedAvatar = R.drawable.default_profile_icon,
          isAvatarDefaultOrNull = true,
          onButtonClick = {})
    }

    // Verify the button does not exist
    composeTestRule.onNodeWithText("Change Avatar").assertDoesNotExist()
  }

  @Test
  fun changeAvatarButton_triggersOnClick_whenClicked() {
    var isClicked = false

    // Set up the test
    composeTestRule.setContent {
      ChangeAvatarButton(
          selectedAvatar = R.drawable.avatar1,
          isAvatarDefaultOrNull = false,
          onButtonClick = { isClicked = true })
    }

    // Perform a click on the button
    composeTestRule.onNodeWithText("Change Avatar").performClick()

    // Verify the callback was invoked
    assert(isClicked)
  }
}
