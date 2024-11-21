package com.github.lookupgroup27.lookup.ui.passwordreset

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.github.lookupgroup27.lookup.R
import com.github.lookupgroup27.lookup.ui.navigation.NavigationActions

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PasswordResetScreen(viewModel: PasswordResetViewModel, navigationActions: NavigationActions) {
  val uiState by viewModel.uiState.collectAsState()

  LaunchedEffect(Unit) { viewModel.resetUiState() }

  Scaffold(
      modifier = Modifier.fillMaxSize(),
      containerColor = Color.Black,
      topBar = {
        TopAppBar(
            title = {},
            navigationIcon = {
              IconButton(
                  onClick = {
                    viewModel.clearFields()
                    navigationActions.goBack()
                  },
                  modifier = Modifier.testTag("back_button")) {
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
            verticalArrangement = Arrangement.Center) {
              Image(
                  painter = painterResource(id = R.drawable.app_logo),
                  contentDescription = "App Logo",
                  modifier = Modifier.size(150.dp).padding(bottom = 16.dp).testTag("app_logo"))

              Text(
                  text = "Reset Your Password",
                  style =
                      MaterialTheme.typography.headlineMedium.copy(
                          fontWeight = FontWeight.Bold, color = Color.White),
                  modifier = Modifier.padding(bottom = 24.dp).testTag("screen_title"))

              OutlinedTextField(
                  value = uiState.email,
                  onValueChange = { viewModel.onEmailChanged(it) },
                  label = { Text("Email", color = Color.White) },
                  modifier = Modifier.fillMaxWidth().testTag("email_field"),
                  textStyle = MaterialTheme.typography.bodyLarge.copy(color = Color.White),
                  keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Email),
                  colors =
                      TextFieldDefaults.outlinedTextFieldColors(
                          focusedLabelColor = Color.White,
                          unfocusedLabelColor = Color.Gray,
                          focusedBorderColor = Color.White,
                          unfocusedBorderColor = Color.Gray,
                          cursorColor = Color.White))

              Spacer(modifier = Modifier.height(24.dp))

              Button(
                  onClick = { viewModel.resetPassword() },
                  modifier = Modifier.fillMaxWidth().testTag("reset_button"),
                  colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1A1A2E))) {
                    Text("Send Reset Email", color = Color.White)
                  }

              Spacer(modifier = Modifier.height(16.dp))

              when {
                uiState.isLoading -> {
                  CircularProgressIndicator(
                      color = Color.White, modifier = Modifier.testTag("loading_indicator"))
                }
                uiState.errorMessage != null -> {
                  Text(
                      text = uiState.errorMessage!!,
                      color = Color.Red,
                      style = MaterialTheme.typography.bodyLarge,
                      modifier = Modifier.testTag("error_message"))
                }
                uiState.isSuccess -> {
                  Text(
                      text = "Password reset email sent successfully!",
                      color = Color.Green,
                      style = MaterialTheme.typography.bodyLarge,
                      modifier = Modifier.testTag("success_message"))
                }
              }
            }
      })
}
