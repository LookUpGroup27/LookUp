package com.github.lookupgroup27.lookup.ui.planetselection.components

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import com.github.lookupgroup27.lookup.R
import com.github.lookupgroup27.lookup.model.map.planets.PlanetData
import org.junit.Rule
import org.junit.Test

class PlanetSelectionRowTest {

  @get:Rule val composeTestRule = createComposeRule()

  @Test
  fun planetSelectionRow_displaysAllPlanets() {
    val planets =
        listOf(
            PlanetData(name = "Mercury", "499", iconRes = R.drawable.mercury_icon, textureId = 0),
            PlanetData(name = "Venus", "499", iconRes = R.drawable.venus_icon, textureId = 1),
            PlanetData(name = "Mars", "499", iconRes = R.drawable.mars_icon, textureId = 3))

    composeTestRule.setContent { PlanetSelectionRow(planets = planets, onPlanetSelected = {}) }

    // Assert that all planets are displayed
    planets.forEach { planet ->
      composeTestRule.onNodeWithContentDescription("${planet.name} button").assertExists()
    }
  }

  @Test
  fun planetSelectionRow_planetButtonIsClickable() {
    val planets =
        listOf(PlanetData(name = "Mars", "499", iconRes = R.drawable.mars_icon, textureId = 0))

    var selectedPlanet: PlanetData? = null

    composeTestRule.setContent {
      PlanetSelectionRow(planets = planets, onPlanetSelected = { selectedPlanet = it })
    }

    // Perform click on the Mars button
    composeTestRule.onNodeWithContentDescription("Mars button").performClick()

    // Assert that the onPlanetSelected lambda is triggered with the correct planet
    assert(selectedPlanet == planets.first()) {
      "Mars button click did not select the correct planet."
    }
  }
}
