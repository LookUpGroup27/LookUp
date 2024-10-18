package com.github.lookupgroup27.lookup.ui.e2e

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.*
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.lookupgroup27.lookup.ui.navigation.NavigationActions
import com.github.lookupgroup27.lookup.ui.navigation.Screen
import com.github.lookupgroup27.lookup.ui.overview.LandingScreen
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import androidx.navigation.compose.composable
import androidx.test.espresso.Espresso
import com.github.lookupgroup27.lookup.model.calendar.CalendarViewModel
import com.github.lookupgroup27.lookup.ui.authentication.SignInScreen
import com.github.lookupgroup27.lookup.ui.calendar.CalendarScreen
import com.github.lookupgroup27.lookup.ui.overview.MenuScreen
import com.github.lookupgroup27.lookup.ui.profile.ProfileScreen


@RunWith(AndroidJUnit4::class)
class AstronomyAppE2ETest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun testEndToEndNavigationFlow() {
        // Step 1: Start at the LandingScreen
        composeTestRule.setContent {
            val navController = rememberNavController()
            NavHost(navController = navController, startDestination = Screen.LANDING) {

                composable(Screen.LANDING) {
                    LandingScreen(NavigationActions(navController = navController))
                }
                composable(Screen.MENU) {
                    MenuScreen(NavigationActions(navController = navController)) // Add MenuScreen as well
                }
                composable(Screen.CALENDAR) {
                    CalendarScreen(viewModel(factory = CalendarViewModel.Factory), NavigationActions(navController = navController)) // Add CalendarScreen
                }
                composable(Screen.PROFILE) {
                    ProfileScreen(NavigationActions(navController = navController)) // Add ProfileScreen
                }
                composable(Screen.AUTH) {
                    SignInScreen(NavigationActions(navController = navController)) // Add ProfileScreen
                }

            }
        }

        // Assert that we're on the Landing screen and the "Home Icon" button is displayed
        composeTestRule.onNodeWithTag("Home Icon").assertIsDisplayed()

        // Step 2: Click the Home button to navigate to MenuScreen
        composeTestRule.onNodeWithTag("Home Icon").performClick()
        composeTestRule.waitForIdle()

        // Assert that we've navigated to the Menu screen
        composeTestRule.onNodeWithTag("menu_screen").assertIsDisplayed()

        // Step 3: Click on "Calendar" button to navigate to CalendarScreen
        composeTestRule.onNodeWithText("Calendar").performClick()

        // Assert that we're on the Calendar screen
        composeTestRule.onNodeWithTag("calendar_screen").assertIsDisplayed()

        // Step 4: Navigate back to the Menu screen
        Espresso.pressBack()
        composeTestRule.waitForIdle()

        // Assert that we're back on the Menu screen
        composeTestRule.onNodeWithTag("menu_screen").assertIsDisplayed()


        // Step 5: Click on the Profile Icon to navigate to the SignIn screen
        composeTestRule.onNodeWithTag("profile_button").performClick()
        composeTestRule.waitForIdle()


        composeTestRule.onNodeWithTag("auth_screen").assertIsDisplayed()

    }
}


