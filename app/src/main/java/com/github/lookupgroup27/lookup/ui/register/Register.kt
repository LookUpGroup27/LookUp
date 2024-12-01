package com.github.lookupgroup27.lookup.ui.register

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
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
@Composable
fun RegisterScreen(viewModel: RegisterViewModel, navigationActions: NavigationActions) {
  val context = LocalContext.current
  val uiState by viewModel.uiState.collectAsState()

  AuthScreen(
      title = "Create Your Account",
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

        Spacer(modifier = Modifier.height(16.dp))

        CustomOutlinedTextField(
            value = uiState.confirmPassword,
            onValueChange = { viewModel.onConfirmPasswordChanged(it) },
            label = "Confirm Password",
            isPassword = true,
            testTag = "confirm_password_field")

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = {
              viewModel.registerUser(
                  onSuccess = {
                    Toast.makeText(context, "Registration successful!", Toast.LENGTH_LONG).show()
                    viewModel.clearFields()
                    navigationActions.navigateTo(Screen.AUTH)
                  },
                  onError = { error -> Toast.makeText(context, error, Toast.LENGTH_LONG).show() })
            },
            modifier = Modifier.fillMaxWidth().testTag("register_button"),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1A1A2E))) {
              Text("Register", color = Color.White)
            }
      }
}
