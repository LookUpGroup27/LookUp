package com.github.lookupgroup27.lookup.ui.planetselection.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color.Companion.Black
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.github.lookupgroup27.lookup.model.map.planets.PlanetData

/**
 * Composable function for a planet button.
 *
 * This button displays an icon representing a planet.
 *
 * @param planet The planet data to display.
 * @param onClick The action to perform when the button is clicked.
 */
@Composable
fun PlanetButton(planet: PlanetData, onClick: () -> Unit) {
  Box(
      modifier =
          Modifier.size(64.dp)
              .padding(8.dp)
              .background(Black, shape = MaterialTheme.shapes.medium)
              .clickable { onClick() },
      contentAlignment = Alignment.Center) {
        Image(
            painter = painterResource(id = planet.iconRes),
            contentDescription = "${planet.name} button",
            modifier = Modifier.size(48.dp),
            contentScale = ContentScale.Fit)
      }
}
