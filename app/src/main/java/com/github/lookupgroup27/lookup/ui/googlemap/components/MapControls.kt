package com.github.lookupgroup27.lookup.ui.googlemap.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.github.lookupgroup27.lookup.ui.theme.*

/**
 * Map controls for toggling auto centering
 *
 * @param autoCenteringEnabled Whether auto centering is enabled
 * @param onCenteringToggle The callback to be called when the auto centering is toggled
 */
@Composable
fun MapControls(autoCenteringEnabled: Boolean, onCenteringToggle: (Boolean) -> Unit) {
  Box(modifier = Modifier.fillMaxWidth().background(DarkBlue).padding(16.dp)) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
      Button(
          onClick = { onCenteringToggle(true) },
          colors =
              ButtonDefaults.buttonColors(
                  containerColor = if (autoCenteringEnabled) DarkPurple else DarkBlue)) {
            Text(text = "Auto Center On")
          }
      Button(
          onClick = { onCenteringToggle(false) },
          colors =
              ButtonDefaults.buttonColors(
                  containerColor = if (!autoCenteringEnabled) DarkPurple else DarkBlue)) {
            Text(text = "Auto Center Off")
          }
    }
  }
}
