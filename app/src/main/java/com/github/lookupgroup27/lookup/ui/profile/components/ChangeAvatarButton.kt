package com.github.lookupgroup27.lookup.ui.profile.components

import android.util.Log
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.github.lookupgroup27.lookup.R
import com.github.lookupgroup27.lookup.ui.theme.DarkPurple

/**
 * A composable function that displays a "Change Avatar" button when a custom avatar is selected.
 *
 * @param selectedAvatar The currently selected avatar resource ID. If null or the default avatar,
 *   the button will not be displayed.
 * @param onButtonClick A callback to be invoked when the button is clicked. Typically used to
 *   navigate to the avatar selection screen.
 */
@Composable
fun ChangeAvatarButton(
    selectedAvatar: Int?,
    isAvatarDefaultOrNull: Boolean,
    onButtonClick: () -> Unit
) {
  val isAvatarInvalid =
      (selectedAvatar == null && selectedAvatar != R.drawable.default_profile_icon)

  if (isAvatarInvalid) {
    Log.d("ChangeAvatarButton", "Invalid  selectedAvatar: $selectedAvatar")
  }

  if (!isAvatarDefaultOrNull) {
    Button(
        onClick = onButtonClick,
        modifier = Modifier.padding(top = 16.dp),
        colors = ButtonDefaults.buttonColors(DarkPurple)) {
          Text(stringResource(R.string.profile_change_avatar))
        }
  }
}
