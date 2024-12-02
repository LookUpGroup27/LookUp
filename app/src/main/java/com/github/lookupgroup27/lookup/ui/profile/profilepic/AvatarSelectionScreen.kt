package com.github.lookupgroup27.lookup.ui.profile.profilepic

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.github.lookupgroup27.lookup.R
import com.github.lookupgroup27.lookup.ui.navigation.NavigationActions
import com.github.lookupgroup27.lookup.ui.theme.DarkPurple

/**
 * Screen for selecting an avatar.
 *
 * @param avatarViewModel the view model for the avatar selection screen
 * @param userId the user ID of the current user
 * @param navigationActions the navigation actions
 */
@Composable
fun AvatarSelectionScreen(
    avatarViewModel: AvatarViewModel,
    userId: String,
    navigationActions: NavigationActions
) {
  val selectedAvatar by
      avatarViewModel.selectedAvatar.collectAsState(initial = null) // Confirmed avatar
  var pendingAvatar by remember { mutableStateOf(selectedAvatar) } // Track current selection
  val defaultAvatar = R.drawable.default_profile_icon

  // Sync pendingAvatar with selectedAvatar whenever selectedAvatar changes
  LaunchedEffect(selectedAvatar) { pendingAvatar = selectedAvatar }

  val avatars =
      (1..16).map { avatarIndex ->
        val resourceName = "avatar$avatarIndex"
        val resourceId = R.drawable::class.java.getField(resourceName).getInt(null)
        resourceId
      }

  Column(modifier = Modifier.fillMaxSize()) {
    // Back Arrow and Title
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically) {
          Icon(
              imageVector = Icons.AutoMirrored.Filled.ArrowBack,
              contentDescription = stringResource(R.string.back),
              modifier =
                  Modifier.padding(start = 16.dp).size(30.dp).clickable {
                    navigationActions.goBack()
                  })

          Spacer(modifier = Modifier.width(8.dp))

          Text(
              text = stringResource(R.string.choose_your_avatar),
              style =
                  MaterialTheme.typography.headlineMedium.copy(
                      color = DarkPurple, fontWeight = FontWeight.Bold),
              modifier = Modifier.align(Alignment.CenterVertically))
        }

    // Avatar Grid
    LazyVerticalGrid(
        columns = GridCells.Fixed(3),
        contentPadding = PaddingValues(8.dp),
        modifier = Modifier.weight(1f).padding(horizontal = 16.dp)) {
          items(avatars) { avatar ->
            Box(
                modifier =
                    Modifier.padding(8.dp)
                        .aspectRatio(1f)
                        .border(
                            width = if (pendingAvatar == avatar) 2.dp else 0.dp,
                            color =
                                if (pendingAvatar == avatar) MaterialTheme.colorScheme.primary
                                else Color.Transparent,
                            shape = CircleShape)
                        .clickable {
                          pendingAvatar = avatar // Update pending selection
                        }
                        .shadow(4.dp, CircleShape) // Subtle shadow for a card effect
                        .background(Color.White, shape = CircleShape) // Rounded background
                ) {
                  Image(
                      painter = painterResource(id = avatar),
                      contentDescription = stringResource(R.string.avatar),
                      modifier = Modifier.fillMaxSize().padding(8.dp) // Add internal padding
                      )
                }
          }
        }

    // Confirm Selection Button
    Button(
        onClick = {
          pendingAvatar?.let {
            avatarViewModel.saveSelectedAvatar(userId, it) // Save pending avatar
            avatarViewModel.updateUserProfileWithAvatar(userId, it) // Update profile
          }
          navigationActions.goBack() // Navigate back to ProfileScreen
        },
        enabled = (pendingAvatar != null), // Enable only if an avatar is selected
        modifier = Modifier.fillMaxWidth().padding(16.dp),
        colors =
            ButtonDefaults.buttonColors(
                containerColor = if (pendingAvatar == null) Color.Gray else DarkPurple),
        shape = RoundedCornerShape(16.dp)) {
          Text(stringResource(R.string.confirm_selection))
        }

    // Reset to Default Avatar Button
    Button(
        onClick = {
          avatarViewModel.saveSelectedAvatar(userId, null) // Reset to default
          avatarViewModel.updateUserProfileWithAvatar(userId, null)
          navigationActions.goBack() // Navigate back to ProfileScreen
        },
        enabled =
            (selectedAvatar != null &&
                selectedAvatar != defaultAvatar), // Enable only if confirmed avatar is not default
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
        colors =
            ButtonDefaults.buttonColors(
                containerColor =
                    if (selectedAvatar == null || selectedAvatar == defaultAvatar) Color.Gray
                    else DarkPurple),
        shape = RoundedCornerShape(16.dp)) {
          Text(stringResource(R.string.reset_to_default_avatar))
        }
  }
}
