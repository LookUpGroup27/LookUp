package com.github.lookupgroup27.lookup.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.DateRange
import androidx.compose.material.icons.outlined.Place
import androidx.compose.material.icons.outlined.PlayArrow
import androidx.compose.material.icons.outlined.Star
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController

object Route {
    const val MAP = "Map"
    const val CALENDAR = "Calendar"
    const val SKY_TRACKER = "SkyTracker"
    const val QUIZ = "Quiz"
    const val PROFILE = "Profile"
}

object Screen {
    const val MAP = "Map Screen"
    const val CALENDAR = "Calendar Screen"
    const val SKY_TRACKER = "Sky Tracker Screen"
    const val QUIZ = "Quiz Screen"
    const val PROFILE = "Profile Screen"
}

data class TopLevelDestination(val route: String, val icon: ImageVector, val textId: String)

object TopLevelDestinations {
    val MAP = TopLevelDestination(route = Route.MAP, icon = Icons.Outlined.Place, textId = "Map")
    val CALENDAR = TopLevelDestination(route = Route.CALENDAR, icon = Icons.Outlined.DateRange, textId = "Calendar")
    val SKY_TRACKER = TopLevelDestination(route = Route.SKY_TRACKER, icon = Icons.Outlined.Star, textId = "Sky Tracker")
    val QUIZ = TopLevelDestination(route = Route.QUIZ, icon = Icons.Outlined.PlayArrow, textId = "Quiz")
}

val LIST_TOP_LEVEL_DESTINATION = listOf(
    TopLevelDestinations.MAP,
    TopLevelDestinations.CALENDAR,
    TopLevelDestinations.SKY_TRACKER,
    TopLevelDestinations.QUIZ
)

open class NavigationActions(
    private val navController: NavHostController,
) {
    /**
     * Navigate to the specified [TopLevelDestination].
     *
     * @param destination The top-level destination to navigate to.
     * Clear the back stack when navigating to a new destination.
     */
    open fun navigateTo(destination: TopLevelDestination) {
        navController.navigate(destination.route) {
            // Pop up to the start destination of the graph to avoid stacking destinations.
            popUpTo(navController.graph.findStartDestination().id) {
                saveState = true
                inclusive = true
            }

            // Avoid multiple copies of the same destination.
            launchSingleTop = true

            // Restore state when reselecting a previously selected item.
            restoreState = true
        }
    }

    /**
     * Navigate to the specified screen.
     *
     * @param screen The screen to navigate to.
     */
    open fun navigateTo(screen: String) {
        navController.navigate(screen)
    }

    /** Navigate back to the previous screen. */
    open fun goBack() {
        navController.popBackStack()
    }

    /**
     * Get the current route of the navigation controller.
     *
     * @return The current route.
     */
    open fun currentRoute(): String {
        return navController.currentDestination?.route ?: ""
    }
}
