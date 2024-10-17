package com.github.lookupgroup27.lookup.ui.profile

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

/**
 * A simple placeholder Composable function for the Collection screen. This will prevent errors when
 * navigating to the "Your Collection" section. It will be implemented with real functionality
 * later.
 */
@Composable
fun CollectionScreen() {
  Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
    // Placeholder text for the Collection screen
    Text(text = "Your Collection")
  }
}
