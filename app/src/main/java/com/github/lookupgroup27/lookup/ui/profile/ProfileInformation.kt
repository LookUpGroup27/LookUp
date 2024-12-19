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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.github.lookupgroup27.lookup.model.profile.UserProfile
import com.github.lookupgroup27.lookup.ui.navigation.NavigationActions
import com.github.lookupgroup27.lookup.ui.navigation.Screen
import com.google.firebase.auth.FirebaseAuth

@SuppressLint("StateFlowValueCalledInComposition")
@Composable
fun ProfileInformationScreen(
    profileViewModel: ProfileViewModel = viewModel(),
    navigationActions: NavigationActions
) {
    val scrollState = rememberScrollState()

    // Fetch user profile on composition
    LaunchedEffect(Unit) {
        profileViewModel.fetchUserProfile()
    }

    val profile by profileViewModel.userProfile.collectAsState()
    val user = FirebaseAuth.getInstance().currentUser // Get the current signed-in user
    val userEmail = user?.email ?: ""
    var username by remember { mutableStateOf(profile?.username ?: "") }
    var bio by remember { mutableStateOf(profile?.bio ?: "") }
    var email by remember { mutableStateOf(userEmail) }

    // Collect error states
    val generalError by profileViewModel.error.collectAsState()
    val usernameError by profileViewModel.usernameError.collectAsState()
    val profileUpdateStatus by profileViewModel.profileUpdateStatus.collectAsState()

    // Handle navigation based on profileUpdateStatus
    LaunchedEffect(profileUpdateStatus) {
        if (profileUpdateStatus == true) {
            navigationActions.navigateTo(Screen.PROFILE)
            profileViewModel.resetProfileUpdateStatus() // Reset after navigation
        }
    }

    Column(
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.Start,
        modifier =
        Modifier
            .padding(8.dp)
            .fillMaxSize()
            .verticalScroll(scrollState)
            .testTag("editProfileScreen")
    ) {
        Spacer(modifier = Modifier.height(16.dp))
        Icon(
            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
            contentDescription = "Back",
            modifier =
            Modifier
                .padding(start = 16.dp)
                .size(30.dp)
                .clickable { navigationActions.goBack() }
                .testTag("goBackButton")
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Your Personal Information",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier
                .padding(start = 16.dp)
                .fillMaxWidth()
                .testTag("editProfileTitle")
        )

        Column(
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .padding(20.dp)
                .fillMaxWidth()
        ) {
            Spacer(modifier = Modifier.height(16.dp))
            OutlinedTextField(
                value = username,
                onValueChange = { newName -> username = newName },
                label = { Text("Username") },
                placeholder = { Text("Enter username") },
                isError = usernameError != null,
                shape = RoundedCornerShape(16.dp),
                modifier =
                Modifier
                    .fillMaxWidth()
                    .height(60.dp)
                    .testTag("editProfileUsername")
            )
            if (usernameError != null) {
                Text(
                    text = usernameError ?: "",
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier
                        .padding(start = 16.dp)
                        .align(Alignment.Start)
                        .testTag("usernameError")
                )
            }

            OutlinedTextField(
                value = email,
                onValueChange = {
                    if (userEmail.isEmpty()) {
                        email = it
                    }
                },
                label = { Text("E-mail") },
                placeholder = { Text("Enter your e-mail") },
                isError = false, // Assuming email is not editable if logged in via Google
                shape = RoundedCornerShape(16.dp),
                modifier =
                Modifier
                    .fillMaxWidth()
                    .height(60.dp)
                    .testTag("editProfileEmail")
            )

            OutlinedTextField(
                value = bio,
                onValueChange = { newDescription -> bio = newDescription },
                label = { Text("Bio") },
                placeholder = { Text("Enter a bio") },
                isError = false,
                shape = RoundedCornerShape(16.dp),
                modifier =
                Modifier
                    .fillMaxWidth()
                    .height(80.dp)
                    .testTag("editProfileBio")
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Display general error if any
            if (generalError != null) {
                Text(
                    text = generalError ?: "",
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier
                        .padding(horizontal = 16.dp)
                        .testTag("generalError")
                )
                Spacer(modifier = Modifier.height(8.dp))
            }

            Button(
                onClick = {
                    val newProfile: UserProfile =
                        profile?.copy(username = username, bio = bio, email = email)
                            ?: UserProfile(username = username, bio = bio, email = email)
                    profileViewModel.updateUserProfile(newProfile)
                    // Navigation is handled by observing profileUpdateStatus
                },
                enabled = username.isNotEmpty() && bio.isNotEmpty() && email.isNotEmpty(),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF30315B)),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(40.dp)
                    .testTag("profileSaveButton")
            ) {
                Text(text = "Save", color = Color.White)
            }

            Spacer(modifier = Modifier.height(30.dp))

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
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(40.dp)
                    .testTag("profileDelete")
            ) {
                Text(text = "Delete", color = Color.White)
            }
        }
    }
}