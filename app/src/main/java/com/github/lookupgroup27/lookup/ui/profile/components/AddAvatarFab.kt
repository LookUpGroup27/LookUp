package com.github.lookupgroup27.lookup.ui.profile.components

import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.github.lookupgroup27.lookup.R
import com.github.lookupgroup27.lookup.ui.theme.DarkPurple

/**
 * A composable function that displays a Floating Action Button (FAB) for adding a new avatar.
 *
 * @param onFabClick A callback to be invoked when the FAB is clicked. Typically used to navigate to
 *   the avatar selection screen or to trigger avatar addition logic.
 */
@Composable
fun AvatarFab(onFabClick: () -> Unit) {
  FloatingActionButton(
      onClick = onFabClick,
      containerColor = DarkPurple,
      modifier = Modifier.size(36.dp).offset(x = (-8).dp, y = (-8).dp)) {
        Icon(
            imageVector = Icons.Filled.Add,
            contentDescription = stringResource(R.string.profile_add_avatar),
            tint = Color.White)
      }
}
