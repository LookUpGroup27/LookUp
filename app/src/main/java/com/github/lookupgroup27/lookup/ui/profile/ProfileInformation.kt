package com.github.lookupgroup27.lookup.ui.profile

import android.annotation.SuppressLint
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.github.lookupgroup27.lookup.model.profile.ProfileViewModel
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

    profileViewModel.fetchUserProfile()
    val profile = profileViewModel.userProfile.value
    val user = FirebaseAuth.getInstance().currentUser // Get the current signed-in user
    val userEmail = user?.email ?: ""
    var username by remember { mutableStateOf(profile?.username ?: "") }
    var bio by remember { mutableStateOf(profile?.bio ?: "") }
    var email by remember { mutableStateOf(userEmail) }
    val context = LocalContext.current

    Column(
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.Start,
        modifier = Modifier.padding(8.dp).width(412.dp).height(892.dp).testTag("editProfileScreen")) {
        Spacer(modifier = Modifier.height(16.dp))
        Icon(
            imageVector = Icons.Default.ArrowBack,
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
            modifier = Modifier.padding(start = 16.dp).fillMaxWidth().testTag("editProfileTitle"))

        Column(
            verticalArrangement = Arrangement.spacedBy(0.dp, Alignment.Top),
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(20.dp).fillMaxWidth()) {
            Spacer(modifier = Modifier.height(16.dp))
            OutlinedTextField(
                value = username,
                onValueChange = { new_name -> username = new_name },
                label = { Text("Username") },
                placeholder = { Text("Enter username") },
                shape = RoundedCornerShape(16.dp),
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
                label = { Text("E-mail") },
                placeholder = { Text("Enter your e-mail") },
                shape = RoundedCornerShape(16.dp),
                modifier =
                Modifier.padding(0.dp)
                    .width(312.dp)
                    .height(60.dp)
                    .testTag("editProfileEmail"))
            Spacer(modifier = Modifier.height(30.dp))
            OutlinedTextField(
                value = bio,
                onValueChange = { new_description -> bio = new_description },
                label = { Text("Bio") },
                placeholder = { Text("Enter a bio") },
                shape = RoundedCornerShape(16.dp),
                modifier =
                Modifier.padding(0.dp).width(312.dp).height(80.dp).testTag("editProfileBio"))
            Spacer(modifier = Modifier.height(72.dp))
            Spacer(modifier = Modifier.height(30.dp))

            Button(
                onClick = {
                    var newProfile: UserProfile
                    if (profile != null) {
                        newProfile = profile.copy(username = username, bio = bio, email = email)
                    } else {
                        newProfile = UserProfile(username = username, bio = bio, email = email)
                    }

                    profileViewModel.updateUserProfile(newProfile)
                },
                enabled = username.isNotEmpty() && bio.isNotEmpty() && email.isNotEmpty(),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF30315B)),
                modifier = Modifier.width(131.dp).height(40.dp).testTag("profileSave")) {
                Text(text = "Save", color = Color.White)
            }

            Spacer(modifier = Modifier.height(30.dp))
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
        }
    }
}