package com.github.lookupgroup27.lookup.ui.register.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.github.lookupgroup27.lookup.R

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
              Image(
                  painter = painterResource(id = R.drawable.app_logo),
                  contentDescription = "App Logo",
                  modifier = Modifier.size(150.dp).padding(bottom = 16.dp).testTag("app_logo"))

              Text(
                  text = title,
                  style =
                      MaterialTheme.typography.headlineMedium.copy(
                          fontWeight = FontWeight.Bold, color = Color.White),
                  modifier = Modifier.padding(bottom = 24.dp).testTag("screen_title"))

              content() // Custom content provided for each screen.
            })
      })
}
