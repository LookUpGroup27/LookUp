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
      (1..16).map { avatarIndex ->
        val resourceName = "avatar$avatarIndex"
        val resourceId = R.drawable::class.java.getField(resourceName).getInt(null)
        resourceId
      }

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
                        .clickable {
                          avatarViewModel.saveSelectedAvatar(userId, avatar)
                          avatarViewModel.updateUserProfileWithAvatar(userId, avatar)
                        }) {
                  Image(
                      painter = painterResource(id = avatar),
                      contentDescription = "Avatar",
                      modifier = Modifier.fillMaxSize())
                }
          }
        }
    Button(
        onClick = {
          userId.let { id ->
            avatarViewModel.verifyOrCreateProfile(
                id,
                onSuccess = { selectedAvatar?.let { avatarViewModel.saveSelectedAvatar(id, it) } },
                onFailure = { error -> })
          }
          navigationActions.goBack()
        },
        modifier = Modifier.fillMaxWidth()) {
          Text("Confirm Selection")
        }
  }
}
