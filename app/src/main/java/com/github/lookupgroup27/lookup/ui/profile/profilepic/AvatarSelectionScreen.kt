package com.github.lookupgroup27.lookup.ui.profile.profilepic

import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.github.lookupgroup27.lookup.R
import com.github.lookupgroup27.lookup.ui.navigation.NavigationActions

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
  val selectedAvatar by avatarViewModel.selectedAvatar.collectAsState(initial = null)

  val avatars =
      listOf(
          R.drawable.avatar1,
          R.drawable.avatar2,
          R.drawable.avatar3,
          R.drawable.avatar4,
          R.drawable.avatar5,
          R.drawable.avatar6,
          R.drawable.avatar7,
          R.drawable.avatar8,
          R.drawable.avatar9,
          R.drawable.avatar10,
          R.drawable.avatar11,
          R.drawable.avatar12,
          R.drawable.avatar13,
          R.drawable.avatar14,
          R.drawable.avatar15,
          R.drawable.avatar16,
      )

  Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
    Text(text = "Choose Your Avatar", modifier = Modifier.padding(bottom = 16.dp))
    LazyVerticalGrid(
        columns = GridCells.Fixed(3),
        contentPadding = PaddingValues(8.dp),
        modifier = Modifier.weight(1f)) {
          items(avatars) { avatar ->
            Box(
                modifier =
                    Modifier.padding(8.dp)
                        .aspectRatio(1f)
                        .border(
                            width = if (selectedAvatar == avatar) 4.dp else 0.dp,
                            color = if (selectedAvatar == avatar) Color.Blue else Color.Transparent,
                            shape = CircleShape)
                        .clickable { avatarViewModel.saveSelectedAvatar(userId, avatar) }) {
                  Image(
                      painter = painterResource(id = avatar),
                      contentDescription = "Avatar",
                      modifier = Modifier.fillMaxSize())
                }
          }
        }
    Button(
        onClick = {
          navigationActions.goBack() // Navigate back to ProfileScreen
        },
        modifier = Modifier.fillMaxWidth()) {
          Text("Confirm Selection")
        }
  }
}
