package com.github.lookupgroup27.lookup.ui.login

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.github.lookupgroup27.lookup.R
import com.github.lookupgroup27.lookup.ui.navigation.NavigationActions
import com.github.lookupgroup27.lookup.ui.navigation.Screen
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
                    navigationActions.navigateTo(Screen.AUTH)
                  },
                  modifier = Modifier.testTag("back_button_login")) {
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
                  modifier =
                      Modifier.size(150.dp).padding(bottom = 16.dp).testTag("app_logo_login"))

              Text(
                  text = "Log In to Your Account",
                  style =
                      MaterialTheme.typography.headlineMedium.copy(
                          fontWeight = FontWeight.Bold, color = Color.White),
                  modifier = Modifier.padding(bottom = 24.dp).testTag("screen_title_login"))

              // Email input field using CustomOutlinedTextField.
              CustomOutlinedTextField(
                  value = uiState.email,
                  onValueChange = { viewModel.onEmailChanged(it) },
                  label = "Email",
                  testTag = "email_field_login")

              Spacer(modifier = Modifier.height(16.dp))

              // Password input field using CustomOutlinedTextField with isPassword = true.
              CustomOutlinedTextField(
                  value = uiState.password,
                  onValueChange = { viewModel.onPasswordChanged(it) },
                  label = "Password",
                  isPassword = true,
                  testTag = "password_field_login")

              Spacer(modifier = Modifier.height(24.dp))

              Button(
                  onClick = {
                    viewModel.loginUser(
                        onSuccess = {
                          Toast.makeText(context, "Login successful!", Toast.LENGTH_LONG).show()
                          viewModel.clearFields()
                          navigationActions.navigateTo(Screen.PROFILE)
                        },
                        onError = { error ->
                          Toast.makeText(context, error, Toast.LENGTH_LONG).show()
                        })
                  },
                  modifier = Modifier.fillMaxWidth().testTag("login_button_login"),
                  colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1A1A2E)),
                  enabled = uiState.email.isNotBlank() && uiState.password.isNotBlank()) {
                    Text(
                        "Login",
                        color =
                            if (uiState.email.isNotBlank() && uiState.password.isNotBlank())
                                Color.White
                            else Color.Gray)
                  }
            }
      })
}
