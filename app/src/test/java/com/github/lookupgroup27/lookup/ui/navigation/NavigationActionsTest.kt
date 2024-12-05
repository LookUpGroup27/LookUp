package com.github.lookupgroup27.lookup.ui.navigation

import androidx.navigation.NavDestination
import androidx.navigation.NavGraph
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.NavOptionsBuilder
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`
import org.mockito.kotlin.any
import org.mockito.kotlin.argumentCaptor
import org.mockito.kotlin.eq

class NavigationActionsTest {

  private lateinit var navigationDestination: NavDestination
  private lateinit var navHostController: NavHostController
  private lateinit var navigationActions: NavigationActions

  @Before
  fun setUp() {
    navigationDestination = mock(NavDestination::class.java)
    navHostController = mock(NavHostController::class.java)
    navigationActions = NavigationActions(navHostController)

    // Mock NavGraph and findStartDestination behavior
    val navGraph = mock(NavGraph::class.java)
    `when`(navHostController.graph).thenReturn(navGraph)
    `when`(navGraph.findStartDestination()).thenReturn(navigationDestination)
  }

  @Test
  fun navigateToCallsController() {
    // Test navigating to top-level destinations
    navigationActions.navigateTo(TopLevelDestinations.MAP)
    verify(navHostController).navigate(eq(Route.MAP), any<NavOptionsBuilder.() -> Unit>())

    navigationActions.navigateTo(TopLevelDestinations.MENU)
    verify(navHostController).navigate(eq(Route.MENU), any<NavOptionsBuilder.() -> Unit>())

    // Test navigating to specific screens
    navigationActions.navigateTo(Screen.GOOGLE_MAP)
    verify(navHostController).navigate(Screen.GOOGLE_MAP)

    navigationActions.navigateTo(Screen.QUIZ)
    verify(navHostController).navigate(Screen.QUIZ)
  }

  @Test
  fun goBackCallsController() {
    // Test if goBack calls the correct method on NavHostController
    navigationActions.goBack()
    verify(navHostController).popBackStack()
  }

  @Test
  fun currentRouteWorksWithDestination() {
    // Mock the current destination and test the route
    `when`(navHostController.currentDestination).thenReturn(navigationDestination)
    `when`(navigationDestination.route).thenReturn(Route.PROFILE)

    assertThat(navigationActions.currentRoute(), `is`(Route.PROFILE))
  }

  @Test
  fun topLevelDestinationsListIsCorrect() {
    val expectedList =
        listOf(TopLevelDestinations.MENU, TopLevelDestinations.MAP, TopLevelDestinations.FEED)
    assertThat(LIST_TOP_LEVEL_DESTINATION, `is`(expectedList))
  }

  @Test
  fun navigateToTopLevelDestinationSetsCorrectOptions() {
    val optionsCaptor = argumentCaptor<NavOptionsBuilder.() -> Unit>()

    navigationActions.navigateTo(TopLevelDestinations.MAP)
    verify(navHostController).navigate(eq(Route.MAP), optionsCaptor.capture())

    val navOptionsBuilder = NavOptionsBuilder().apply(optionsCaptor.firstValue)
    assertThat(navOptionsBuilder.launchSingleTop, `is`(true))
    assertThat(navOptionsBuilder.restoreState, `is`(true))
  }

  @Test
  fun navigateToWithImageAndTimestampCallsControllerWithCorrectRoute() {
    // Arrange
    val imageUri = "sample_image_uri"
    val timestamp = 1620000000000L

    // Act
    navigationActions.navigateToWithImage(imageUri, Route.IMAGE_REVIEW, timestamp)

    // Assert
    verify(navHostController).navigate("${Route.IMAGE_REVIEW}/$imageUri/$timestamp")
  }
}
