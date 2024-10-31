package com.github.lookupgroup27.lookup.ui.authentication

import android.content.Intent
import android.content.res.Configuration
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.github.lookupgroup27.lookup.R
import com.github.lookupgroup27.lookup.ui.navigation.NavigationActions
import com.github.lookupgroup27.lookup.ui.navigation.Screen
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.Firebase
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.auth
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SignInScreen(navigationActions: NavigationActions) {
  val context = LocalContext.current
  val configuration = LocalConfiguration.current

  // Check for orientation
  val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE

  // Adaptive spacing and sizes for landscape
  val logoSize = if (isLandscape) 150.dp else 250.dp
  val verticalSpacing = if (isLandscape) 8.dp else 16.dp
  val buttonSpacing = if (isLandscape) 24.dp else 48.dp

  val launcher =
      rememberFirebaseAuthLauncher(
          onAuthComplete = { result ->
            Log.d("SignInScreen", "User signed in: ${result.user?.displayName}")
            Toast.makeText(context, "Login successful!", Toast.LENGTH_LONG).show()
            navigationActions.navigateTo(Screen.PROFILE)
          },
          onAuthError = {
            Log.e("SignInScreen", "Failed to sign in: ${it.statusCode}")
            Toast.makeText(context, "Login Failed!", Toast.LENGTH_LONG).show()
          })
  val token = stringResource(R.string.default_web_client_id)

  Scaffold(
      modifier = Modifier.fillMaxSize().testTag("auth_screen"),
      containerColor = Color.Black,
      topBar = {
        TopAppBar(
            title = { Text("") },
            colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Black))
      },
      content = { padding ->
        Column(
            modifier =
                Modifier.fillMaxSize()
                    .padding(padding)
                    .verticalScroll(rememberScrollState()), // Enable vertical scrolling
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top // Top arrangement to align elements correctly
            ) {
              // Move the Go Back button inside the scrollable container
              IconButton(
                  onClick = { navigationActions.goBack() },
                  modifier =
                      Modifier.padding(16.dp)
                          .align(Alignment.Start)
                          .testTag("go_back_button_signin")) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Go Back",
                        tint = Color.White)
                  }

              Spacer(modifier = Modifier.height(verticalSpacing))

              Image(
                  painter = painterResource(id = R.drawable.app_logo),
                  contentDescription = "App Logo",
                  modifier = Modifier.size(logoSize))

              Spacer(modifier = Modifier.height(verticalSpacing))

              Text(
                  modifier = Modifier.testTag("loginTitle"),
                  text = "Welcome to the Cosmos",
                  style =
                      MaterialTheme.typography.headlineMedium.copy(
                          fontSize = 42.sp, lineHeight = 50.sp, letterSpacing = 1.5.sp),
                  fontWeight = FontWeight.SemiBold,
                  textAlign = TextAlign.Center,
                  color = Color(0xFF8A9BB7))

              Spacer(modifier = Modifier.height(buttonSpacing))

              GoogleSignInButton(
                  onSignInClick = {
                    val gso =
                        GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                            .requestIdToken(token)
                            .requestEmail()
                            .build()
                    val googleSignInClient = GoogleSignIn.getClient(context, gso)
                    launcher.launch(googleSignInClient.signInIntent)
                  })
            }
      })
}

@Composable
fun GoogleSignInButton(onSignInClick: () -> Unit) {
  val configuration = LocalConfiguration.current
  val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE

  // Set dimensions based on orientation
  val buttonHeight = if (isLandscape) 40.dp else 48.dp
  val buttonWidthModifier =
      if (isLandscape) Modifier.fillMaxWidth(0.7f) else Modifier.fillMaxWidth()

  Button(
      onClick = onSignInClick,
      colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1A1A2E)),
      shape = RoundedCornerShape(50),
      border = BorderStroke(1.dp, Color(0xFF9DACE6)),
      modifier =
          Modifier.padding(8.dp)
              .height(buttonHeight)
              .then(buttonWidthModifier)
              .testTag("loginButton")) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxWidth()) {
              Image(
                  painter = painterResource(id = R.drawable.google_logo),
                  contentDescription = "Google Logo",
                  modifier =
                      Modifier.size(24.dp) // Smaller logo for better alignment in landscape
                          .padding(end = 8.dp))

              Text(
                  text = "Sign in with Google",
                  color = Color.White,
                  fontSize = 14.sp, // Slightly smaller font in landscape
                  fontWeight = FontWeight.Medium)
            }
      }
}

@Composable
fun rememberFirebaseAuthLauncher(
    onAuthComplete: (AuthResult) -> Unit,
    onAuthError: (ApiException) -> Unit
): ManagedActivityResultLauncher<Intent, ActivityResult> {
  val scope = rememberCoroutineScope()
  return rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) {
      result ->
    val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
    try {
      val account = task.getResult(ApiException::class.java)!!
      val credential = GoogleAuthProvider.getCredential(account.idToken!!, null)
      scope.launch {
        val authResult = Firebase.auth.signInWithCredential(credential).await()
        onAuthComplete(authResult)
      }
    } catch (e: ApiException) {
      onAuthError(e)
    }
  }
}
