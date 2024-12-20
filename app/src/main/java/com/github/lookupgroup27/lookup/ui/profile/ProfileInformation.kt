package com.github.lookupgroup27.lookup.ui.profile

import android.annotation.SuppressLint
import android.content.pm.ActivityInfo
import androidx.activity.ComponentActivity
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.blur
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.github.lookupgroup27.lookup.R
import com.github.lookupgroup27.lookup.model.profile.*
import com.github.lookupgroup27.lookup.ui.navigation.*
import com.github.lookupgroup27.lookup.ui.theme.LightPurple
import com.github.lookupgroup27.lookup.ui.theme.StarLightWhite
import com.google.firebase.auth.FirebaseAuth

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("StateFlowValueCalledInComposition")
@Composable
fun ProfileInformationScreen(
    profileViewModel: ProfileViewModel,
    navigationActions: NavigationActions
) {

  val context = LocalContext.current

  // Lock the screen orientation to portrait mode.
  DisposableEffect(Unit) {
    val activity = context as? ComponentActivity
    activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
    onDispose { activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED }
  }

  val scrollState = rememberScrollState()

  profileViewModel.fetchUserProfile()
  val profile = profileViewModel.userProfile.value
  val user = FirebaseAuth.getInstance().currentUser
  val userEmail = user?.email ?: ""
  var username by remember { mutableStateOf(profile?.username ?: "") }
  var bio by remember { mutableStateOf(profile?.bio ?: "") }
  var email by remember { mutableStateOf(userEmail) }
  val fieldColors =
      OutlinedTextFieldDefaults.colors(
          focusedTextColor = StarLightWhite, // Text color when focused
          unfocusedTextColor = StarLightWhite, // Text color when not focused
          disabledTextColor = StarLightWhite, // Text color when disabled
          focusedContainerColor = Color.Black.copy(alpha = 0.2f), // Darker background when focused
          unfocusedContainerColor =
              Color.Black.copy(alpha = 0.2f), // Darker background when not focused
          focusedBorderColor = StarLightWhite, // Border color when focused
          unfocusedBorderColor = StarLightWhite, // Border color when not focused
      )
  var showDeleteConfirmation by remember { mutableStateOf(false) }

  Box(modifier = Modifier.fillMaxSize().testTag("background_box")) {
    // Background Image
    Image(
        painter = painterResource(id = R.drawable.landscape_background),
        contentDescription = "Background",
        contentScale = ContentScale.Crop,
        modifier = Modifier.fillMaxSize().blur(20.dp).testTag("background_image"))

    // Content
    Column(
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.Start,
        modifier =
            Modifier.padding(8.dp)
                .fillMaxSize()
                .verticalScroll(scrollState)
                .testTag("editProfileScreen")) {
          Spacer(modifier = Modifier.height(16.dp))

          // Back Button
          Icon(
              imageVector = Icons.AutoMirrored.Filled.ArrowBack,
              contentDescription = "Back",
              tint = Color.White,
              modifier =
                  Modifier.padding(start = 16.dp)
                      .size(30.dp)
                      .clickable { navigationActions.goBack() }
                      .testTag("goBackButton"))

          Spacer(modifier = Modifier.height(16.dp))

          // Title
          Text(
              text = "Your Personal Information",
              style = MaterialTheme.typography.headlineMedium,
              color = Color.White,
              modifier = Modifier.padding(start = 16.dp).fillMaxWidth().testTag("editProfileTitle"))

          // Form Fields
          Column(
              verticalArrangement = Arrangement.spacedBy(0.dp, Alignment.Top),
              horizontalAlignment = Alignment.CenterHorizontally,
              modifier = Modifier.padding(20.dp).fillMaxWidth()) {
                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = username,
                    onValueChange = { new_name -> username = new_name },
                    label = { Text(text = "Username", color = StarLightWhite) },
                    placeholder = { Text("Enter username") },
                    shape = RoundedCornerShape(16.dp),
                    colors = fieldColors,
                    modifier =
                        Modifier.padding(0.dp)
                            .width(312.dp)
                            .height(60.dp)
                            .testTag("editProfileUsername"))

                Spacer(modifier = Modifier.height(30.dp))

                OutlinedTextField(
                    value = email,
                    onValueChange = {
                      if (userEmail.isEmpty()) {
                        email = it
                      }
                    },
                    label = { Text(text = "E-mail", color = StarLightWhite) },
                    placeholder = { Text("Enter your e-mail") },
                    shape = RoundedCornerShape(16.dp),
                    colors = fieldColors,
                    modifier =
                        Modifier.padding(0.dp)
                            .width(312.dp)
                            .height(60.dp)
                            .testTag("editProfileEmail"))

                Spacer(modifier = Modifier.height(30.dp))

                OutlinedTextField(
                    value = bio,
                    onValueChange = { new_description -> bio = new_description },
                    label = { Text(text = "Bio", color = StarLightWhite) },
                    placeholder = { Text("Enter a bio") },
                    shape = RoundedCornerShape(16.dp),
                    colors = fieldColors,
                    modifier =
                        Modifier.padding(0.dp)
                            .width(312.dp)
                            .height(80.dp)
                            .testTag("editProfileBio"))

                Spacer(modifier = Modifier.height(72.dp))

                // Buttons
                Button(
                    onClick = {
                      val newProfile: UserProfile =
                          profile?.copy(username = username, bio = bio, email = email)
                              ?: UserProfile(username = username, bio = bio, email = email)
                      profileViewModel.updateUserProfile(newProfile)
                      navigationActions.navigateTo(Screen.PROFILE)
                    },
                    enabled = username.isNotEmpty() && bio.isNotEmpty() && email.isNotEmpty(),
                    colors =
                        ButtonDefaults.buttonColors(
                            containerColor = LightPurple,
                            disabledContainerColor = LightPurple.copy(alpha = 0.5f)),
                    border = BorderStroke(0.2.dp, StarLightWhite),
                    modifier = Modifier.width(131.dp).height(40.dp).testTag("profileSaveButton")) {
                      Text(text = "Save", color = Color.White)
                    }

                Spacer(modifier = Modifier.height(30.dp))

                Button(
                    onClick = {
                      profileViewModel.logoutUser()
                      navigationActions.navigateTo(Screen.LANDING)
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF410002)),
                    border = BorderStroke(0.2.dp, StarLightWhite),
                    modifier = Modifier.width(131.dp).height(40.dp).testTag("profileLogout")) {
                      Text(text = "Sign out", color = Color.White)
                    }

                Spacer(modifier = Modifier.height(30.dp))

                Button(
                    onClick = { showDeleteConfirmation = true },
                    enabled = username.isNotEmpty() && bio.isNotEmpty() && email.isNotEmpty(),
                    colors =
                        ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF410002),
                            disabledContainerColor = Color(0xFF410002).copy(alpha = 0.5f)),
                    border = BorderStroke(0.2.dp, StarLightWhite),
                    modifier = Modifier.width(131.dp).height(40.dp).testTag("profileDelete")) {
                      Text(text = "Delete", color = Color.White)
                    }
              }
        }

    if (showDeleteConfirmation) {
      AlertDialog(
          onDismissRequest = { showDeleteConfirmation = false },
          title = { Text("Confirm Deletion") },
          text = { Text("Are you sure you want to delete your profile?") },
          confirmButton = {
            Button(
                onClick = {
                  profile?.let {
                    profileViewModel.deleteUserProfile(it)
                    profileViewModel.logoutUser()
                  }
                  navigationActions.navigateTo(Screen.MENU)
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF410002)),
                modifier = Modifier.testTag("confirmDeleteButton")) {
                  Text("Yes", color = Color.White)
                }
          },
          dismissButton = {
            Button(
                onClick = { showDeleteConfirmation = false },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF30315B)),
                modifier = Modifier.testTag("cancelDeleteButton")) {
                  Text("Cancel", color = Color.White)
                }
          },
          modifier = Modifier.testTag("deleteConfirmationDialog"))
    }
  }
}
