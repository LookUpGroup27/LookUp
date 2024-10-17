package com.github.lookupgroup27.lookup.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Menu
import androidx.compose.material.icons.outlined.Place
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController

object Route {
  const val AUTH = "Auth"
  const val LANDING = "Landing"
  const val MAP = "Map"
  const val CALENDAR = "Calendar"
  const val SKY_TRACKER = "SkyTracker"
  const val QUIZ = "Quiz"
  const val QUIZ_PLAY = "QuizPlay"
  const val PROFILE = "Profile"
  const val MENU = "Menu"
  const val COLLECTION = "Collection"
}

object Screen {
  const val AUTH = "Auth Screen"
  const val LANDING = "Landing Screen"
  const val MAP = "Map Screen"
  const val CALENDAR = "Calendar Screen"
  const val SKY_TRACKER = "Sky Tracker Screen"
  const val QUIZ = "Quiz Screen"
  const val QUIZ_PLAY = "Quiz Play Screen"
  const val PROFILE = "Profile Screen"
  const val MENU = "Menu Screen"
  const val COLLECTION = "Collection Screen"
}

data class TopLevelDestination(val route: String, val icon: ImageVector, val textId: String)

object TopLevelDestinations {
  val MENU = TopLevelDestination(route = Route.MENU, icon = Icons.Outlined.Menu, textId = "Menu")
  val MAP = TopLevelDestination(route = Route.MAP, icon = Icons.Outlined.Place, textId = "Map")
}

val LIST_TOP_LEVEL_DESTINATION =
    listOf(
        TopLevelDestinations.MAP,
        TopLevelDestinations.MENU,
    )

open class NavigationActions(
    private val navController: NavHostController,
) {
  /**
   * Navigate to the specified [TopLevelDestination].
   *
   * @param destination The top-level destination to navigate to. Clear the back stack when
   *   navigating to a new destination.
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
