package com.github.lookupgroup27.lookup.ui.planetselection

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import com.github.lookupgroup27.lookup.model.map.planets.PlanetsRepository
import com.github.lookupgroup27.lookup.ui.navigation.NavigationActions
import com.github.lookupgroup27.lookup.ui.navigation.Screen
import io.mockk.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class PlanetSelectionScreenTest {

  @get:Rule val composeTestRule = createComposeRule()

  private lateinit var viewModel: PlanetSelectionViewModel
  private lateinit var navigationActions: NavigationActions
  private lateinit var planetsRepository: PlanetsRepository

  @Before
  fun setUp() {

    planetsRepository = PlanetsRepository(mockk(), mockk(), "")

    // Mock the ViewModel with PlanetsRepository
    viewModel = spyk(PlanetSelectionViewModel(planetsRepository))

    // Mock NavigationActions
    navigationActions = mockk(relaxed = true)
  }

  @Test
  fun testBackButtonNavigatesToMenu() {
    composeTestRule.setContent {
      PlanetSelectionScreen(viewModel = viewModel, navigationActions = navigationActions)
    }

    composeTestRule.onNodeWithTag("go_back_button").performClick()

    verify { navigationActions.navigateTo(Screen.MENU) }
  }

  @Test
  fun testPlanetSelectionUpdatesPlanetName() {
    composeTestRule.setContent {
      PlanetSelectionScreen(viewModel = viewModel, navigationActions = navigationActions)
    }

    // Select Mars
    composeTestRule.onNodeWithContentDescription("Mars button").performClick()

    // Check if the planet name is updated
    composeTestRule.onNodeWithTag("planet_name").assertTextEquals("Mars")
  }

  @Test
  fun testInitialPlanetDisplayedCorrectly() {
    composeTestRule.setContent {
      PlanetSelectionScreen(viewModel = viewModel, navigationActions = navigationActions)
    }

    // Check if the initially selected planet (Moon) is displayed
    composeTestRule.onNodeWithTag("planet_name").assertTextEquals("Moon")
  }

  @Test
  fun testPlanetSurfaceViewUpdatesOnPlanetChange() {
    composeTestRule.setContent {
      PlanetSelectionScreen(viewModel = viewModel, navigationActions = navigationActions)
    }

    // Select Jupiter
    composeTestRule.onNodeWithContentDescription("Jupiter button").performClick()

    // Verify if the planet name is updated to Jupiter
    composeTestRule.onNodeWithTag("planet_name").assertTextEquals("Jupiter")
  }
}
