package com.github.lookupgroup27.lookup.ui.googlemap

// toDo: try to correctly write the test for this class

/*import android.location.Location
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import com.github.lookupgroup27.lookup.ui.map.MapScreen
import com.github.lookupgroup27.lookup.ui.navigation.LIST_TOP_LEVEL_DESTINATION
import com.github.lookupgroup27.lookup.ui.navigation.NavigationActions
import org.junit.*
import org.mockito.kotlin.*

class GoogleMapKtTest {

    @get:Rule val composeTestRule = createComposeRule()

    private val mockNavigationActions: NavigationActions = mock()

    @Test
    fun mapScreen_displaysBackgroundImage() {
        composeTestRule.setContent { GoogleMapScreen(navigationActions = mockNavigationActions) }

        // Verify the background image is displayed
        composeTestRule.onNodeWithTag("googleMapScreen").assertIsDisplayed()
    }

    @Test
    fun menuScreen_displaysBottomNavigationMenu() {
        composeTestRule.setContent { GoogleMapScreen(navigationActions = mockNavigationActions) }

        // Check that the bottom navigation menu is displayed
        composeTestRule.onNodeWithTag("bottomNavigationMenu").assertIsDisplayed()
    }

    @Test
    fun testMarkerIsDisplayedWhenLocationPermissionGranted() {
        // Mock location
        val mockLocation = Location("provider").apply {
            latitude = 37.4219983
            longitude = -122.084
        }

        // Set the Compose content with mocked location and permission
        composeTestRule.setContent {
            GoogleMapScreen(navigationActions = mockNavigationActions)
        }

        // Check if the marker is displayed
        composeTestRule.onNodeWithText("You are here").assertIsDisplayed()
    }
}*/

/*import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import com.github.lookupgroup27.lookup.ui.navigation.NavigationActions
import com.github.lookupgroup27.lookup.ui.navigation.Screen
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`

class GoogleMapScreenTest {

    private lateinit var navigationActions: NavigationActions

    @get:Rule
    val composeTestRule = createComposeRule()

    @Before
    fun setUp() {
        // Mock NavigationActions
        navigationActions = mock(NavigationActions::class.java)
        // Setup to return the map route as current
        `when`(navigationActions.currentRoute()).thenReturn(Screen.SKY_TRACKER)
    }

    @Test
    fun mapScreenDisplaysCorrectly() {
        // Set the Compose content to GoogleMapScreen
        composeTestRule.setContent {
            GoogleMapScreen(navigationActions = navigationActions)
        }

        // Verify that the GoogleMapScreen is displayed
        composeTestRule.onNodeWithTag("googleMapScreen").assertIsDisplayed()

        // Ensure the bottom navigation is set up correctly
        composeTestRule.onNodeWithTag("bottomNavigationMenu").assertIsDisplayed()
    }
}
*/
