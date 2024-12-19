package com.github.lookupgroup27.lookup.ui.planetselection.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.github.lookupgroup27.lookup.model.map.planets.PlanetData

/**
 * Composable function for a row of planet selection buttons.
 *
 * This row displays a list of planet buttons that the user can click to select a planet.
 *
 * @param planets The list of planets to display.
 * @param onPlanetSelected The action to perform when a planet is selected.
 */
@Composable
fun PlanetSelectionRow(planets: List<PlanetData>, onPlanetSelected: (PlanetData) -> Unit) {
  LazyRow(
      modifier = Modifier.fillMaxWidth().padding(top = 16.dp),
      horizontalArrangement = Arrangement.Center) {
        items(planets) { planet ->
          PlanetButton(planet = planet, onClick = { onPlanetSelected(planet) })
        }
      }
}
