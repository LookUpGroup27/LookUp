package com.github.lookupgroup27.lookup.ui.login

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.github.lookupgroup27.lookup.ui.navigation.NavigationActions
import com.github.lookupgroup27.lookup.ui.navigation.Screen
import com.github.lookupgroup27.lookup.ui.register.components.AuthScreen
import com.github.lookupgroup27.lookup.ui.register.components.CustomOutlinedTextField

@OptIn(ExperimentalMaterial3Api::class)
/**
 * Displays the login screen where users can enter their credentials to log in.
 *
 * @param viewModel The [LoginViewModel] that handles the screen state and logic.
 * @param navigationActions Navigation actions to switch between screens.
 */
@Composable
fun LoginScreen(viewModel: LoginViewModel, navigationActions: NavigationActions) {
  val context = LocalContext.current
  val uiState by viewModel.uiState.collectAsState()

  AuthScreen(
      title = "Log In to Your Account",
      onBackClicked = {
        viewModel.clearFields()
        navigationActions.navigateTo(Screen.AUTH)
      }) {
        CustomOutlinedTextField(
            value = uiState.email,
            onValueChange = { viewModel.onEmailChanged(it) },
            label = "Email",
            testTag = "email_field")

        Spacer(modifier = Modifier.height(16.dp))

        CustomOutlinedTextField(
            value = uiState.password,
            onValueChange = { viewModel.onPasswordChanged(it) },
            label = "Password",
            isPassword = true,
            testTag = "password_field")

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = {
              viewModel.loginUser(
                  onSuccess = {
                    Toast.makeText(context, "Login successful!", Toast.LENGTH_LONG).show()
                    viewModel.clearFields()
                    navigationActions.navigateTo(Screen.PROFILE)
                  },
                  onError = { error -> Toast.makeText(context, error, Toast.LENGTH_LONG).show() })
            },
            modifier = Modifier.fillMaxWidth().testTag("login_button"),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1A1A2E)),
            enabled = uiState.email.isNotBlank() && uiState.password.isNotBlank()) {
              Text(
                  "Login",
                  color =
                      if (uiState.email.isNotBlank() && uiState.password.isNotBlank()) Color.White
                      else Color.Gray)
            }
        Spacer(modifier = Modifier.height(16.dp))

        TextButton(
            onClick = { navigationActions.navigateTo(Screen.PASSWORDRESET) },
            modifier =
                Modifier.align(Alignment.CenterHorizontally).testTag("forgot_password_button")) {
              Text(
                  text = "Forgot Password?",
                  style =
                      MaterialTheme.typography.bodyLarge.copy(
                          color = MaterialTheme.colorScheme.primary))
            }
      }
}
