package com.github.lookupgroup27.lookup.ui.profile

import android.content.pm.ActivityInfo
import android.content.res.Configuration
import androidx.activity.ComponentActivity
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
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.github.lookupgroup27.lookup.R
import com.github.lookupgroup27.lookup.ui.navigation.BottomNavigationMenu
import com.github.lookupgroup27.lookup.ui.navigation.LIST_TOP_LEVEL_DESTINATION
import com.github.lookupgroup27.lookup.ui.navigation.NavigationActions
import com.github.lookupgroup27.lookup.ui.navigation.Screen
import com.github.lookupgroup27.lookup.ui.profile.components.ChangeAvatarButton
import com.github.lookupgroup27.lookup.ui.profile.components.ProfileButton
import com.github.lookupgroup27.lookup.ui.profile.components.ProfileFab
import com.github.lookupgroup27.lookup.ui.profile.profilepic.AvatarViewModel
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import components.BackgroundImage

@Composable
fun ProfileScreen(navigationActions: NavigationActions, avatarViewModel: AvatarViewModel) {
    val context = LocalContext.current

    // Lock the screen orientation to portrait mode.
    DisposableEffect(Unit) {
        val activity = context as? ComponentActivity
        activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        onDispose { activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED }
    }

  val configuration = LocalConfiguration.current
  val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE
  val user = FirebaseAuth.getInstance().currentUser
  val isUserLoggedIn = user != null

  val userId = Firebase.auth.currentUser?.uid
  LaunchedEffect(userId) { userId?.let { avatarViewModel.fetchSelectedAvatar(it) } }

  // Collect the selected avatar state
  val selectedAvatar by avatarViewModel.selectedAvatar.collectAsState()
  val isAvatarDefaultOrNull =
      selectedAvatar == null || selectedAvatar == R.drawable.default_profile_icon

  Scaffold(
      bottomBar = {
        BottomNavigationMenu(
            onTabSelect = { destination -> navigationActions.navigateTo(destination) },
            tabList = LIST_TOP_LEVEL_DESTINATION,
            isUserLoggedIn = isUserLoggedIn,
            selectedItem = navigationActions.currentRoute())
      }) { innerPadding ->
        Box(modifier = Modifier.fillMaxSize().padding(innerPadding).testTag("profile_screen")) {
          // Background Image
          BackgroundImage(
              painterResId = R.drawable.background_blurred,
              contentDescription = stringResource(R.string.background_description))

          // Scrollable Profile Content
          Column(
              modifier =
                  Modifier.fillMaxSize()
                      .padding(horizontal = if (isLandscape) 16.dp else 0.dp)
                      .padding(top = if (isLandscape) 24.dp else 172.dp)
                      .verticalScroll(rememberScrollState()), // Make the column scrollable
              horizontalAlignment = Alignment.CenterHorizontally,
              verticalArrangement = Arrangement.Top) {
                // Profile Icon Box for FAB placement
                Box(contentAlignment = Alignment.BottomEnd) {
                  // Profile Icon
                  val avatarRes = selectedAvatar ?: R.drawable.default_profile_icon
                  Icon(
                      painter = painterResource(id = avatarRes),
                      contentDescription =
                          stringResource(R.string.profile_profile_icon_description),
                      modifier = Modifier.size(150.dp).padding(bottom = 8.dp),
                      tint =
                          if (avatarRes == R.drawable.default_profile_icon) Color.White
                          else Color.Unspecified)

                  // Show FAB when no avatar is selected
                  ProfileFab(selectedAvatar, isAvatarDefaultOrNull) {
                    navigationActions.navigateTo(Screen.AVATAR_SELECTION)
                  }
                }

                ChangeAvatarButton(selectedAvatar, isAvatarDefaultOrNull) {
                  navigationActions.navigateTo(Screen.AVATAR_SELECTION)
                }

                Spacer(modifier = Modifier.height(32.dp))

                // Personal Info Button
                ProfileButton(
                    text = stringResource(R.string.profile_personal_info_button),
                    onClick = { navigationActions.navigateTo(Screen.PROFILE_INFORMATION) })

                Spacer(modifier = Modifier.height(8.dp))

                // Collection Button
                ProfileButton(
                    text = stringResource(R.string.profile_collection_button),
                    onClick = { navigationActions.navigateTo(Screen.COLLECTION) })

                Spacer(modifier = Modifier.height(16.dp)) // Extra space
          }
        }
      }
}
