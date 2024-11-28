package com.github.lookupgroup27.lookup.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.List
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
  const val GOOGLE_MAP = "Google Map"
  const val QUIZ = "Quiz"
  const val QUIZ_PLAY = "QuizPlay"
  const val PROFILE = "Profile"
  const val MENU = "Menu"
  const val COLLECTION = "Collection"
  const val PROFILE_INFORMATION = "ProfileInformation"
  const val TAKE_IMAGE = "TakeImage"
  const val IMAGE_REVIEW = "ImageReview"
  const val FEED = "Feed"
  const val LOGIN = "Login"
  const val EDIT_IMAGE = "EditImage"
}

object Screen {
  const val AUTH = "Auth Screen"
  const val LANDING = "Landing Screen"
  const val MAP = "Map Screen"
  const val CALENDAR = "Calendar Screen"
  const val GOOGLE_MAP = "Google Map Screen"
  const val QUIZ = "Quiz Screen"
  const val QUIZ_PLAY = "Quiz Play Screen"
  const val PROFILE = "Profile Screen"
  const val MENU = "Menu Screen"
  const val COLLECTION = "Collection Screen"
  const val PROFILE_INFORMATION = "Profile Information Screen"
  const val TAKE_IMAGE = "Take Image"
  const val IMAGE_REVIEW = "Image Review Screen"
  const val FEED = "Feed Screen"
  const val LOGIN = "Login Screen"
  const val EDIT_IMAGE = "Edit Image"
}

data class TopLevelDestination(val route: String, val icon: ImageVector, val textId: String)

object TopLevelDestinations {
  val MENU = TopLevelDestination(route = Route.MENU, icon = Icons.Outlined.Menu, textId = "Menu")
  val MAP = TopLevelDestination(route = Route.MAP, icon = Icons.Outlined.Place, textId = "Map")
  val FEED = TopLevelDestination(route = Route.FEED, icon = Icons.Outlined.List, textId = "Feed")
}

val LIST_TOP_LEVEL_DESTINATION =
    listOf(TopLevelDestinations.MENU, TopLevelDestinations.MAP, TopLevelDestinations.FEED)

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

  /**
   * Navigate to the a screen with a specific imageUri.
   *
   * @param image The URI of the captured image to review.
   */
  open fun navigateToWithImage(image: String, route: String) {
    navController.navigate("${route}/$image")
  }
}
