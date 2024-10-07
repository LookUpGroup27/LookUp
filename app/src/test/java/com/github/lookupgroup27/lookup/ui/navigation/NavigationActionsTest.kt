package com.github.lookupgroup27.lookup.ui.navigation

import androidx.navigation.NavDestination
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
  }

  @Test
  fun navigateToCallsController() {
    // Test navigating to top-level destinations
    navigationActions.navigateTo(TopLevelDestinations.MAP)
    verify(navHostController).navigate(eq(Route.MAP), any<NavOptionsBuilder.() -> Unit>())

    navigationActions.navigateTo(TopLevelDestinations.CALENDAR)
    verify(navHostController).navigate(eq(Route.CALENDAR), any<NavOptionsBuilder.() -> Unit>())

    // Test navigating to specific screens
    navigationActions.navigateTo(Screen.SKY_TRACKER)
    verify(navHostController).navigate(Screen.SKY_TRACKER)

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
}
