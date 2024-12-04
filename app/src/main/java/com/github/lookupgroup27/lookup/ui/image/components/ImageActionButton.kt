package com.github.lookupgroup27.lookup.ui.image.components

import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun ActionButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    color: Color = MaterialTheme.colorScheme.primary
) {
  Button(
      onClick = onClick,
      colors = ButtonDefaults.buttonColors(containerColor = color),
      modifier = modifier.width(200.dp),
  ) {
    Text(text = text)
  }
}
