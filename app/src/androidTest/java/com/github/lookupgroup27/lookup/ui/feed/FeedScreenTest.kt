import android.Manifest
import android.content.Context
import android.location.Location
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.test.assertTextContains
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.test.core.app.ApplicationProvider
import androidx.test.rule.GrantPermissionRule
import com.github.lookupgroup27.lookup.model.location.LocationProvider
import com.github.lookupgroup27.lookup.model.post.Post
import com.github.lookupgroup27.lookup.model.post.PostsRepository
import com.github.lookupgroup27.lookup.model.post.PostsViewModel
import com.github.lookupgroup27.lookup.ui.FeedScreen
import com.github.lookupgroup27.lookup.ui.navigation.NavigationActions
import com.github.lookupgroup27.lookup.ui.navigation.Screen
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`

class FeedScreenTest {

  @get:Rule val composeTestRule = createComposeRule()

  @get:Rule
  val permissionRule: GrantPermissionRule =
      GrantPermissionRule.grant(Manifest.permission.ACCESS_FINE_LOCATION)

  private lateinit var postsRepository: PostsRepository
  private lateinit var postsViewModel: PostsViewModel
  private lateinit var navigationActions: NavigationActions
  private lateinit var locationProvider: LocationProvider

  private val testPosts =
      listOf(
          Post(
              uid = "1",
              uri = "http://example.com/1.jpg",
              username = "User1",
              latitude = 37.7749,
              longitude = -122.4194), // San Francisco
          Post(
              uid = "2",
              uri = "http://example.com/2.jpg",
              username = "User2",
              latitude = 34.0522,
              longitude = -118.2437), // Los Angeles
          Post(
              uid = "3",
              uri = "http://example.com/3.jpg",
              username = "User3",
              latitude = 40.7128,
              longitude = -74.0060), // New York
          Post(
              uid = "4",
              uri = "http://example.com/4.jpg",
              username = "User4",
              latitude = 36.1699,
              longitude = -115.1398) // Las Vegas
          )

  @Before
  fun setUp() {
    val context = ApplicationProvider.getApplicationContext<Context>()

    // Mock the repository and other dependencies
    postsRepository = mock(PostsRepository::class.java)
    postsViewModel = PostsViewModel(postsRepository)
    navigationActions = mock(NavigationActions::class.java)

    // Define navigation action behavior
    `when`(navigationActions.currentRoute()).thenReturn(Screen.FEED)

    locationProvider = LocationProvider(context, mutableStateOf(null))

    composeTestRule.setContent {
      FeedScreen(
          postsViewModel = postsViewModel,
          navigationActions = navigationActions,
          initialNearbyPosts = testPosts,
          initialLoadingState = false)
    }
  }

  @Test
  fun testFeedScreenDisplaysNearbyPosts() {

    // Assert each post item is displayed
    composeTestRule.onNodeWithTag("PostItem_1").assertExists()
    composeTestRule.onNodeWithTag("UsernameTag_User1").assertTextContains("User1")

    composeTestRule.onNodeWithTag("PostItem_2").assertExists()
    composeTestRule.onNodeWithTag("UsernameTag_User2").assertTextContains("User2")
  }

  @Test
  fun testNearestPostsUpdateOnLocationChange() {
    // Simulate location change to New York (should pick User3 and other nearby posts)
    val newLocation =
        Location("provider").apply {
          latitude = 40.7128
          longitude = -74.0060
        }

    composeTestRule.runOnIdle { locationProvider.currentLocation.value = newLocation }

    composeTestRule.waitForIdle()

    // Verify that the post item for User3 is displayed and contains the correct username text
    composeTestRule.onNodeWithTag("PostItem_3").assertExists()
    composeTestRule.onNodeWithTag("UsernameTag_User3").assertExists().assertTextContains("User3")
  }
}
