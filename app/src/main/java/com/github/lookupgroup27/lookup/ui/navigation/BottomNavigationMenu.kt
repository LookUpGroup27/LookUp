package com.github.lookupgroup27.lookup.ui.navigation

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.github.lookupgroup27.lookup.util.NetworkUtils

@Composable
fun BottomNavigationMenu(
    onTabSelect: (TopLevelDestination) -> Unit,
    tabList: List<TopLevelDestination>,
    isUserLoggedIn: Boolean,
    selectedItem: String
) {
  val context = LocalContext.current
  val isOnline = remember { mutableStateOf(NetworkUtils.isNetworkAvailable(context)) }
  NavigationBar(
      modifier = Modifier.fillMaxWidth().height(60.dp).testTag("bottomNavigationMenu"),
      containerColor = Color(0xFF0D1023),
      content = {
        tabList.forEach { tab ->
          NavigationBarItem(
              icon = {
                // Load the correct icon based on the type (ImageVector or Painter)
                when {
                  tab.iconVector != null ->
                      Icon(
                          imageVector = tab.iconVector,
                          contentDescription = null,
                          tint = Color.White)
                  tab.iconResource != null ->
                      Image(
                          painter = painterResource(id = tab.iconResource),
                          contentDescription = null,
                          modifier = Modifier.size(34.dp),
                          colorFilter = ColorFilter.tint(Color.White))
                }
              },
              label = { Text(text = tab.textId, color = Color.White) },
              selected = tab.route == selectedItem,
              onClick = {
                when {
                  tab.route == Route.FEED && !isUserLoggedIn -> {
                    Toast.makeText(
                            context, "You need to log in to access the feed.", Toast.LENGTH_LONG)
                        .show()
                  }
                  tab.route == Route.SKY_MAP && !isOnline.value -> {
                    Toast.makeText(
                            context, "You're not connected to the internet.", Toast.LENGTH_LONG)
                        .show()
                  }
                  selectedItem != tab.route -> {
                    onTabSelect(tab)
                  }
                }
              },
              modifier = Modifier.clip(shape = RoundedCornerShape(50.dp)).testTag(tab.textId))
        }
      },
  )
}
