package com.github.lookupgroup27.lookup.ui.feed
/*
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.lookupgroup27.lookup.R
import com.github.lookupgroup27.lookup.ui.FeedScreen
import com.github.lookupgroup27.lookup.ui.Post
import com.github.lookupgroup27.lookup.ui.PostItem
import com.github.lookupgroup27.lookup.ui.navigation.LIST_TOP_LEVEL_DESTINATION
import com.github.lookupgroup27.lookup.ui.navigation.NavigationActions
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito
import org.mockito.Mockito.mock
import org.mockito.kotlin.never

@RunWith(AndroidJUnit4::class)
class FeedScreenTest {

  @get:Rule val composeTestRule = createComposeRule()

  private lateinit var mockNavigationActions: NavigationActions

  @Before
  fun setup() {
    // Initialize the mock object
    mockNavigationActions = mock(NavigationActions::class.java)
  }

  @Test
  fun testFeedScreenUsernames() {
    composeTestRule.setContent { FeedScreen(navigationActions = mockNavigationActions) }

    // Verify that each username is displayed with the correct test tag
    composeTestRule.onNodeWithTag("UsernameTag_User1").performScrollTo().assertIsDisplayed()
    composeTestRule.onNodeWithTag("UsernameTag_User2").performScrollTo().assertIsDisplayed()
  }

  @Test
  fun testPostItemDisplaysUsernameAndImage() {
    val post = Post(username = "User1", imageResId = R.drawable.image1)

    composeTestRule.setContent { PostItem(post = post) }

    // Verify that the username is displayed
    composeTestRule.onNodeWithTag("UsernameTag_User1").assertIsDisplayed()

    // Verify that the image with the content description is displayed
    composeTestRule.onNodeWithContentDescription("Post Image for User1").assertIsDisplayed()
  }

  @Test
  fun testBottomNavigationTabsExist() {
    // Launch the FeedScreen
    composeTestRule.setContent { FeedScreen(navigationActions = mockNavigationActions) }

    // Verify that each bottom navigation tab is displayed
    LIST_TOP_LEVEL_DESTINATION.forEach { destination ->
      composeTestRule.onNodeWithText(destination.textId).assertExists()
    }
  }

  @Test
  fun testBottomNavigationTabClick() {
    composeTestRule.setContent { FeedScreen(navigationActions = mockNavigationActions) }

    // Perform click on the "Feed" tab and verify navigation action
    composeTestRule.onNodeWithText("Feed").performClick()

    val feedDestination = LIST_TOP_LEVEL_DESTINATION.first { it.textId == "Feed" }
    Mockito.verify(mockNavigationActions, never()).navigateTo(feedDestination)
  }

  @Test
  fun testFeedScreenIsScrollable() {
    // Launch the FeedScreen with more posts to enable scrolling
    composeTestRule.setContent { FeedScreen(navigationActions = mockNavigationActions) }

    // Perform scroll to the second post
    composeTestRule.onNodeWithText("User2").performScrollTo().assertExists()
  }
}
*/
