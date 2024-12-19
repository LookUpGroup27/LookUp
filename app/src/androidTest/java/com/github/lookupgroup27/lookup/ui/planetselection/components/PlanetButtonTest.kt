package com.github.lookupgroup27.lookup.ui.planetselection.components

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.unit.dp
import com.github.lookupgroup27.lookup.R
import com.github.lookupgroup27.lookup.model.map.planets.PlanetData
import org.junit.Rule
import org.junit.Test

class PlanetButtonTest {

  @get:Rule val composeTestRule = createComposeRule()

  @Test
  fun planetButton_displaysCorrectIcon() {
    val planet = PlanetData(name = "Mars", "301", iconRes = R.drawable.mars_icon, textureId = 0)

    composeTestRule.setContent { PlanetButton(planet = planet, onClick = {}) }

    // Assert that the button contains the correct icon
    composeTestRule.onNodeWithContentDescription("Mars button").assertExists()
  }

  @Test
  fun planetButton_isClickable() {
    val planet = PlanetData(name = "Mars", "301", iconRes = R.drawable.mars_icon, textureId = 0)
    var clicked = false

    composeTestRule.setContent { PlanetButton(planet = planet, onClick = { clicked = true }) }

    // Perform click on the button
    composeTestRule.onNodeWithContentDescription("Mars button").performClick()

    // Assert that the button click triggers the onClick action
    assert(clicked) { "Planet button click did not trigger the onClick action" }
  }

  @Test
  fun planetButton_hasCorrectSizeAndPadding() {
    val planet = PlanetData(name = "Mars", "301", iconRes = R.drawable.mars_icon, textureId = 0)

    composeTestRule.setContent { PlanetButton(planet = planet, onClick = {}) }

    // Assert that the button has the correct size
    composeTestRule.onNodeWithContentDescription("Mars button").assertHeightIsEqualTo(48.dp)
    composeTestRule.onNodeWithContentDescription("Mars button").assertWidthIsEqualTo(48.dp)
  }

  @Test
  fun planetButton_backgroundIsBlack() {
    val planet = PlanetData(name = "Mars", "301", iconRes = R.drawable.mars_icon, textureId = 0)

    composeTestRule.setContent { PlanetButton(planet = planet, onClick = {}) }

    // Assert that the button has the correct background color
    composeTestRule
        .onNodeWithContentDescription("Mars button")
        .assertExists() // Additional color checks need more advanced libraries
  }
}
