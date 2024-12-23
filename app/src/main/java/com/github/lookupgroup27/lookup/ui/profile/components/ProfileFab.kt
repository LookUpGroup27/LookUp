package com.github.lookupgroup27.lookup.ui.profile.components

import android.util.Log
import androidx.compose.runtime.Composable
import com.github.lookupgroup27.lookup.R

/**
 * A composable function that displays a Floating Action Button (FAB) for selecting or adding an
 * avatar.
 *
 * This FAB is displayed only if no avatar is selected or the selected avatar is the default profile
 * icon.
 *
 * @param selectedAvatar The currently selected avatar resource ID. If null or the default avatar,
 *   the FAB will be displayed.
 * @param onFabClick A callback to be invoked when the FAB is clicked. Typically used to navigate to
 *   the avatar selection screen.
 */
@Composable
fun ProfileFab(selectedAvatar: Int?, isAvatarDefaultOrNull: Boolean, onFabClick: () -> Unit) {
  val isAvatarInvalid = selectedAvatar == null && selectedAvatar != R.drawable.default_profile_icon

  if (isAvatarInvalid) {
    Log.d("ChangeAvatarButton", "Invalid  selectedAvatar: $selectedAvatar")
  }
  if (isAvatarDefaultOrNull) {
    // Add debug log for invalid case
    Log.d("ProfileFab", "Invalid or default selectedAvatar: $selectedAvatar")
    AvatarFab(onFabClick)
  }
}
