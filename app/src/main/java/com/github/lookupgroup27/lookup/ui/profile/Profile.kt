package com.github.lookupgroup27.lookup.ui.profile

import android.content.res.Configuration
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.github.lookupgroup27.lookup.R
import com.github.lookupgroup27.lookup.ui.navigation.BottomNavigationMenu
import com.github.lookupgroup27.lookup.ui.navigation.LIST_TOP_LEVEL_DESTINATION
import com.github.lookupgroup27.lookup.ui.navigation.NavigationActions
import com.github.lookupgroup27.lookup.ui.navigation.Screen
import com.github.lookupgroup27.lookup.ui.profile.components.ProfileButton
import com.github.lookupgroup27.lookup.ui.profile.profilepic.AvatarViewModel
import com.github.lookupgroup27.lookup.ui.theme.DarkPurple
import com.google.firebase.Firebase
import com.google.firebase.auth.auth

@Composable
fun ProfileScreen(navigationActions: NavigationActions, avatarViewModel: AvatarViewModel) {
  val configuration = LocalConfiguration.current
  val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE

  val userId = Firebase.auth.currentUser?.uid
  LaunchedEffect(userId) { userId?.let { avatarViewModel.fetchSelectedAvatar(it) } }

  // Collect the selected avatar state
  val selectedAvatar by avatarViewModel.selectedAvatar.collectAsState()

  Scaffold(
      bottomBar = {
        BottomNavigationMenu(
            onTabSelect = { destination -> navigationActions.navigateTo(destination) },
            tabList = LIST_TOP_LEVEL_DESTINATION,
            selectedItem = navigationActions.currentRoute())
      }) { innerPadding ->
        Box(modifier = Modifier.fillMaxSize().padding(innerPadding)) {
          // Background Image
          Image(
              painter = painterResource(id = R.drawable.background_blurred),
              contentDescription = "Background",
              contentScale = ContentScale.Crop,
              modifier = Modifier.fillMaxSize())

          // Scrollable Profile Content
          Column(
              modifier =
                  Modifier.fillMaxSize()
                      .padding(horizontal = if (isLandscape) 16.dp else 0.dp)
                      .padding(top = if (isLandscape) 24.dp else 172.dp)
                      .verticalScroll(rememberScrollState()),
              horizontalAlignment = Alignment.CenterHorizontally,
              verticalArrangement = Arrangement.Top) {
                // Profile Icon
                val avatarRes = selectedAvatar ?: R.drawable.default_profile_icon
                Icon(
                    painter = painterResource(id = avatarRes),
                    contentDescription = "Profile Icon",
                    modifier = Modifier.size(150.dp),
                    tint =
                        if (avatarRes == R.drawable.default_profile_icon) Color.White
                        else Color.Unspecified)

                Button(
                    onClick = { navigationActions.navigateTo(Screen.AVATAR_SELECTION) },
                    colors = ButtonDefaults.buttonColors(DarkPurple)
                    // modifier = Modifier.align(Alignment.Center)
                    ) {
                      Text("Change Avatar")
                    }

                Spacer(modifier = Modifier.height(32.dp))

                // Personal Info Button
                ProfileButton(
                    text = "Personal Info     >",
                    onClick = { navigationActions.navigateTo(Screen.PROFILE_INFORMATION) })

                Spacer(modifier = Modifier.height(8.dp))

                // Collection Button
                ProfileButton(
                    text = "Your Collection   >",
                    onClick = { navigationActions.navigateTo(Screen.COLLECTION) })

                // Extra space at the bottom in case more buttons are added in the future
                Spacer(modifier = Modifier.height(16.dp))
              }
        }
      }
}
