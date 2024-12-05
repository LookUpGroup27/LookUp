package com.github.lookupgroup27.lookup.ui.register.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.github.lookupgroup27.lookup.R

/**
 * A composable function representing the base layout for authentication screens.
 *
 * This function provides a consistent layout structure with a top app bar, an app logo, a screen
 * title, and a content area for custom content specific to each screen.
 *
 * @param title The title to display at the top of the screen.
 * @param onBackClicked Callback invoked when the back button is clicked.
 * @param content The custom content to display within the screen.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AuthScreen(
    title: String,
    onBackClicked: () -> Unit,
    content: @Composable ColumnScope.() -> Unit
) {
  Scaffold(
      modifier = Modifier.fillMaxSize(),
      containerColor = Color.Black,
      topBar = {
        TopAppBar(
            title = {},
            navigationIcon = {
              IconButton(
                  onClick = { onBackClicked() }, modifier = Modifier.testTag("back_button")) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Go Back",
                        tint = Color.White)
                  }
            },
            colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Black))
      },
      content = { padding ->
        Column(
            modifier =
                Modifier.fillMaxSize()
                    .padding(padding)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 24.dp, vertical = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            content = {
              // App Logo
              Image(
                  painter = painterResource(id = R.drawable.app_logo),
                  contentDescription = "App Logo",
                  modifier = Modifier.size(150.dp).padding(bottom = 16.dp).testTag("app_logo"))

              // Screen Title
              Text(
                  text = title,
                  style =
                      MaterialTheme.typography.headlineMedium.copy(
                          fontWeight = FontWeight.Bold, color = Color.White),
                  modifier = Modifier.padding(bottom = 24.dp).testTag("screen_title"))

              // Custom Content
              content()
            })
      })
}
