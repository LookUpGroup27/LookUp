package com.github.lookupgroup27.lookup.ui.register

import android.annotation.SuppressLint
import android.content.pm.ActivityInfo
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.github.lookupgroup27.lookup.ui.navigation.NavigationActions
import com.github.lookupgroup27.lookup.ui.navigation.Screen
import com.github.lookupgroup27.lookup.ui.register.components.AuthScreen
import com.github.lookupgroup27.lookup.ui.register.components.CustomOutlinedTextField

/**
 * Composable function representing the registration screen.
 *
 * This screen allows users to create a new account by providing their username, email, password,
 * and confirming their password. It provides real-time validation feedback and handles the
 * registration process asynchronously.
 *
 * @param navigationActions The navigation actions used to navigate between screens.
 * @param viewModel The [RegisterViewModel] managing the registration logic.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreen(
    navigationActions: NavigationActions,
    viewModel: RegisterViewModel = viewModel(factory = RegisterViewModel.Factory)
) {
  val context = LocalContext.current

    // Lock the screen orientation to portrait mode.
    DisposableEffect(Unit) {
        val activity = context as? ComponentActivity
        activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        onDispose { activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED }
    }

  val uiState by viewModel.uiState.collectAsState()

  Box(modifier = Modifier.fillMaxSize()) {
    AuthScreen(
        title = "Create Your Account",
        onBackClicked = {
          viewModel.clearFields()
          navigationActions.navigateTo(Screen.AUTH)
        }) {

          // Username Input Field
          CustomOutlinedTextField(
              value = uiState.username,
              onValueChange = viewModel::onUsernameChanged,
              label = "Username",
              errorMessage = uiState.usernameError,
              testTag = "username_field")

          Spacer(modifier = Modifier.height(16.dp))

          // Email Input Field
          CustomOutlinedTextField(
              value = uiState.email,
              onValueChange = viewModel::onEmailChanged,
              label = "Email",
              errorMessage = uiState.emailError,
              testTag = "email_field")

          Spacer(modifier = Modifier.height(16.dp))

          // Password Input Field
          CustomOutlinedTextField(
              value = uiState.password,
              onValueChange = viewModel::onPasswordChanged,
              label = "Password",
              isPassword = true,
              errorMessage = uiState.passwordError,
              testTag = "password_field")

          Spacer(modifier = Modifier.height(16.dp))

          // Confirm Password Input Field
          CustomOutlinedTextField(
              value = uiState.confirmPassword,
              onValueChange = viewModel::onConfirmPasswordChanged,
              label = "Confirm Password",
              isPassword = true,
              errorMessage = uiState.confirmPasswordError,
              testTag = "confirm_password_field")

          Spacer(modifier = Modifier.height(24.dp))

          // Register Button
          Button(
              onClick = {
                viewModel.registerUser {
                  viewModel.clearFields()
                  navigationActions.navigateTo(Screen.AUTH)
                  Toast.makeText(context, "Registration successful!", Toast.LENGTH_LONG).show()
                }
              },
              modifier = Modifier.fillMaxWidth().testTag("register_button"),
              colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1A1A2E))) {
                Text("Register", color = Color.White)
              }

          // General Error Message
          uiState.generalError?.let { error ->
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = error,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodyMedium)
          }
        }

    // Loading Indicator Overlay
    if (uiState.isLoading) {
      Box(
          modifier = Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.5f)),
          contentAlignment = Alignment.Center) {
            CircularProgressIndicator(color = Color.White)
          }
    }
  }
}
