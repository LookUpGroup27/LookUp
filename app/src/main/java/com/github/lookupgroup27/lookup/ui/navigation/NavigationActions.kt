package com.github.lookupgroup27.lookup.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.List
import androidx.compose.material.icons.outlined.Menu
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import com.github.lookupgroup27.lookup.R

object Route {
  const val AUTH = "Auth"
  const val LANDING = "Landing"
  const val SKY_MAP = "SkyMap"
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
  const val PASSWORDRESET = "PasswordReset"
  const val AVATAR_SELECTION = "AvatarSelection"
  const val LOGIN = "Login"
  const val REGISTER = "Register"
  const val EDIT_IMAGE = "EditImage"
  const val PLANET_SELECTION = "PlanetSelection"
}

object Screen {
  const val AUTH = "Auth Screen"
  const val LANDING = "Landing Screen"
  const val SKY_MAP = "Sky Map Screen"
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
  const val PASSWORDRESET = "Password Reset Screen"
  const val AVATAR_SELECTION = "Avatar Selection Screen"
  const val LOGIN = "Login Screen"
  const val REGISTER = "Register Screen"
  const val EDIT_IMAGE = "Edit Image"
  const val PLANET_SELECTION = "Planet Selection Screen"
}

data class TopLevelDestination(
    val route: String,
    val iconVector: ImageVector? = null, // For ImageVector icons
    val iconResource: Int? = null, // For PainterResource drawable icons
    val textId: String
)

object TopLevelDestinations {
  val MENU =
      TopLevelDestination(route = Route.MENU, iconVector = Icons.Outlined.Menu, textId = "Menu")
  val SKY_MAP =
      TopLevelDestination(
          route = Route.SKY_MAP, iconResource = R.drawable.skymap_icon, textId = "Sky Map")
  val FEED =
      TopLevelDestination(route = Route.FEED, iconVector = Icons.Outlined.List, textId = "Feed")
}

val LIST_TOP_LEVEL_DESTINATION =
    listOf(TopLevelDestinations.MENU, TopLevelDestinations.SKY_MAP, TopLevelDestinations.FEED)

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
   * Navigate to a screen with a specific imageUri and timestamp.
   *
   * @param image The URI of the captured image to review.
   * @param route The route to navigate to.
   * @param timestamp The timestamp of when the image was captured.
   */
  open fun navigateToWithImage(image: String, route: String, timestamp: Long) {
    navController.navigate("${route}/$image/$timestamp")
  }

  /**
   * Navigate to the map screen with the specified post ID, latitude, and longitude.
   *
   * @param postId The ID of the post to navigate to.
   * @param lat The latitude of the post.
   * @param lon The longitude of the post.
   */
  open fun navigateToMapWithPost(
      postId: String,
      lat: Double,
      lon: Double,
      autoCenter: Boolean = false
  ) {
    navController.navigate("${Route.GOOGLE_MAP}/$postId/$lat/$lon/$autoCenter") {
      popUpTo(navController.graph.findStartDestination().id) { saveState = true }
      launchSingleTop = true
      restoreState = true
    }
  }

  /** Navigate to a screen with a specific post information. */
  open fun navigateToWithPostInfo(
      encodedUri: String,
      postAverageStar: Float,
      postRatedByNb: Int,
      postUid: String,
      postDescription: String,
      route: String
  ) {
    navController.navigate(
        "${route}/$encodedUri/$postAverageStar/$postRatedByNb/$postUid/$postDescription")
  }
}
