package com.github.lookupgroup27.lookup.ui.passwordreset

import android.content.pm.ActivityInfo
import androidx.activity.ComponentActivity
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.github.lookupgroup27.lookup.R
import com.github.lookupgroup27.lookup.ui.navigation.NavigationActions
import com.github.lookupgroup27.lookup.ui.navigation.Screen

/**
 * Composable function for the Password Reset Screen.
 *
 * This screen allows users to reset their password by providing their email address. It displays
 * the app logo, a text input field for the email, and a button to trigger the reset. The screen
 * also shows loading indicators, error messages, or success messages based on the current state of
 * the UI.
 *
 * @param viewModel The [PasswordResetViewModel] that manages the UI state and business logic.
 * @param navigationActions Handles navigation actions for transitioning between screens.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PasswordResetScreen(viewModel: PasswordResetViewModel, navigationActions: NavigationActions) {
    val context = LocalContext.current

    // Lock the screen orientation to portrait mode.
    DisposableEffect(Unit) {
        val activity = context as? ComponentActivity
        activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        onDispose { activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED }
    }

  // Observes the UI state from the ViewModel
  val uiState by viewModel.uiState.collectAsState()

  // Resets the UI state when the screen is first launched
  LaunchedEffect(Unit) { viewModel.resetUiState() }

  Scaffold(
      modifier = Modifier.fillMaxSize(),
      containerColor = Color.Black,
      topBar = {
        // Top App Bar with a back button to navigate back to the authentication screen
        TopAppBar(
            title = {},
            navigationIcon = {
              IconButton(
                  onClick = {
                    viewModel.clearFields() // Clears any input fields
                    navigationActions.navigateTo(Screen.AUTH) // Navigates back to the auth screen
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
        // Main content of the screen
        Column(
            modifier =
                Modifier.fillMaxSize()
                    .padding(padding)
                    .verticalScroll(rememberScrollState()) // Enables vertical scrolling
                    .padding(horizontal = 24.dp, vertical = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center) {
              // Displays the app logo
              Image(
                  painter = painterResource(id = R.drawable.app_logo),
                  contentDescription = "App Logo",
                  modifier = Modifier.size(150.dp).padding(bottom = 16.dp).testTag("app_logo"))

              // Screen title
              Text(
                  text = "Reset Your Password",
                  style =
                      MaterialTheme.typography.headlineMedium.copy(
                          fontWeight = FontWeight.Bold, color = Color.White),
                  modifier = Modifier.padding(bottom = 24.dp).testTag("screen_title"))

              // Email input field
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

              // Button to send the password reset email
              Button(
                  onClick = { viewModel.resetPassword() },
                  modifier = Modifier.fillMaxWidth().testTag("reset_button"),
                  colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1A1A2E))) {
                    Text("Send Reset Email", color = Color.White)
                  }

              Spacer(modifier = Modifier.height(16.dp))

              // Displays loading, error, or success messages based on the UI state
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
