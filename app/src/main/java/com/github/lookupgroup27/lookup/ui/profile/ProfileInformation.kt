// File: ProfileInformationScreen.kt
package com.github.lookupgroup27.lookup.ui.profile

import android.annotation.SuppressLint
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.github.lookupgroup27.lookup.model.profile.UserProfile
import com.github.lookupgroup27.lookup.ui.navigation.NavigationActions
import com.github.lookupgroup27.lookup.ui.navigation.Screen
import com.google.firebase.auth.FirebaseAuth

@SuppressLint("StateFlowValueCalledInComposition")
@Composable
fun ProfileInformationScreen(
    profileViewModel: ProfileViewModel,
    navigationActions: NavigationActions
) {

  val scrollState = rememberScrollState()

  // Observe states from ViewModel
  val profile by profileViewModel.userProfile.collectAsState()
  val profileUpdateStatus by profileViewModel.profileUpdateStatus.collectAsState()
  val errorMessage by profileViewModel.error.collectAsState()
  val usernameError by profileViewModel.usernameError.collectAsState()

  // Local UI state
  var username by remember { mutableStateOf(profile?.username ?: "") }
  var bio by remember { mutableStateOf(profile?.bio ?: "") }
  var email by remember {
    mutableStateOf(profile?.email ?: FirebaseAuth.getInstance().currentUser?.email ?: "")
  }

  // Synchronize local UI state with ViewModel's userProfile
  LaunchedEffect(profile) {
    username = profile?.username ?: ""
    bio = profile?.bio ?: ""
    email = profile?.email ?: FirebaseAuth.getInstance().currentUser?.email ?: ""
  }

  // Reset error states when the screen is entered
  LaunchedEffect(Unit) { profileViewModel.resetProfileUpdateStatus() }

  // Handle navigation based on profileUpdateStatus
  LaunchedEffect(profileUpdateStatus, errorMessage) {
    if (profileUpdateStatus == true) {
      // Update was successful, navigate to Profile screen
      navigationActions.navigateTo(Screen.PROFILE)
      profileViewModel.resetProfileUpdateStatus()
    } else if (profileUpdateStatus == false && errorMessage != null) {
      // Handle general errors if needed (e.g., show a Snackbar)
      // For this example, we're focusing on username errors
      profileViewModel.resetProfileUpdateStatus()
    }
  }

  Scaffold(
      // You can add other scaffold parameters like topBar or bottomBar here if needed
      ) { innerPadding ->
        Column(
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.Start,
            modifier =
                Modifier.padding(8.dp)
                    .fillMaxSize()
                    .padding(innerPadding)
                    .verticalScroll(scrollState)
                    .testTag("editProfileScreen")) {
              Spacer(modifier = Modifier.height(16.dp))
              Icon(
                  imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                  contentDescription = "Back",
                  modifier =
                      Modifier.padding(start = 16.dp)
                          .size(30.dp)
                          .clickable { navigationActions.goBack() }
                          .testTag("goBackButton"))
              Spacer(modifier = Modifier.height(16.dp))
              Text(
                  text = "Your Personal Information",
                  style = MaterialTheme.typography.headlineMedium,
                  modifier =
                      Modifier.padding(start = 16.dp).fillMaxWidth().testTag("editProfileTitle"))

              Column(
                  verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.Top),
                  horizontalAlignment = Alignment.CenterHorizontally,
                  modifier = Modifier.padding(20.dp).fillMaxWidth()) {
                    Spacer(modifier = Modifier.height(16.dp))

                    // Username Field
                    OutlinedTextField(
                        value = username,
                        onValueChange = { new_name ->
                          username = new_name
                          if (usernameError != null) {
                            // Reset username error when user starts typing
                            profileViewModel.resetProfileUpdateStatus()
                          }
                        },
                        label = { Text("Username") },
                        placeholder = { Text("Enter username") },
                        isError = usernameError != null,
                        shape = RoundedCornerShape(16.dp),
                        modifier =
                            Modifier.width(312.dp).height(60.dp).testTag("editProfileUsername"))

                    // Display username error if exists
                    if (usernameError != null) {
                      Text(
                          text = usernameError ?: "",
                          color = MaterialTheme.colorScheme.error,
                          style = MaterialTheme.typography.bodySmall,
                          modifier = Modifier.padding(start = 16.dp).fillMaxWidth())
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // E-mail Field
                    OutlinedTextField(
                        value = email,
                        onValueChange = {
                          if (FirebaseAuth.getInstance().currentUser?.email.isNullOrEmpty()) {
                            email = it
                          }
                        },
                        label = { Text("E-mail") },
                        placeholder = { Text("Enter your e-mail") },
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier.width(312.dp).height(60.dp).testTag("editProfileEmail"))

                    Spacer(modifier = Modifier.height(8.dp))

                    // Bio Field
                    OutlinedTextField(
                        value = bio,
                        onValueChange = { new_description -> bio = new_description },
                        label = { Text("Bio") },
                        placeholder = { Text("Enter a bio") },
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier.width(312.dp).height(80.dp).testTag("editProfileBio"))

                    Spacer(modifier = Modifier.height(30.dp))

                    // Save Button
                    Button(
                        onClick = {
                          val newProfile: UserProfile =
                              profile?.copy(username = username, bio = bio, email = email)
                                  ?: UserProfile(username = username, bio = bio, email = email)
                          profileViewModel.updateUserProfile(newProfile)
                        },
                        enabled = username.isNotEmpty() && bio.isNotEmpty() && email.isNotEmpty(),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF30315B)),
                        modifier =
                            Modifier.width(131.dp).height(40.dp).testTag("profileSaveButton")) {
                          Text(text = "Save", color = Color.White)
                        }

                    Spacer(modifier = Modifier.height(30.dp))

                    // Logout Button
                    Button(
                        onClick = {
                          profileViewModel.logoutUser()
                          navigationActions.navigateTo(Screen.LANDING)
                        },
                        enabled = true,
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF410002)),
                        modifier = Modifier.width(131.dp).height(40.dp).testTag("profileLogout")) {
                          Text(text = "Sign out", color = Color.White)
                        }

                    Spacer(modifier = Modifier.height(30.dp))

                    // Delete Button
                    Button(
                        onClick = {
                          profile?.let {
                            profileViewModel.deleteUserProfile(it)
                            profileViewModel.logoutUser()
                          }
                          navigationActions.navigateTo(Screen.MENU)
                        },
                        enabled = username.isNotEmpty() && bio.isNotEmpty() && email.isNotEmpty(),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF410002)),
                        modifier = Modifier.width(131.dp).height(40.dp).testTag("profileDelete")) {
                          Text(text = "Delete", color = Color.White)
                        }
                  }
            }
      }
}
