package com.github.lookupgroup27.lookup.ui.profile

import android.content.res.Configuration
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.compose.rememberNavController
import com.github.lookupgroup27.lookup.R
import com.github.lookupgroup27.lookup.ui.navigation.BottomNavigationMenu
import com.github.lookupgroup27.lookup.ui.navigation.LIST_TOP_LEVEL_DESTINATION
import com.github.lookupgroup27.lookup.ui.navigation.NavigationActions
import com.github.lookupgroup27.lookup.ui.navigation.Screen
import com.github.lookupgroup27.lookup.ui.theme.DarkPurple

@Composable
fun ProfileScreen(navigationActions: NavigationActions) {
  val configuration = LocalConfiguration.current
  val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE

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
                Icon(
                    painter = painterResource(id = R.drawable.profile_icon),
                    contentDescription = "Profile Icon",
                    modifier = Modifier.size(if (isLandscape) 100.dp else 150.dp),
                    tint = Color.Unspecified)

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

@Composable
fun ProfileButton(text: String, onClick: () -> Unit) {
  Button(
      onClick = onClick,
      colors = ButtonDefaults.buttonColors(containerColor = DarkPurple, contentColor = Color.White),
      shape = RoundedCornerShape(174.dp),
      modifier =
          Modifier.width(
                  if (LocalConfiguration.current.orientation == Configuration.ORIENTATION_LANDSCAPE)
                      200.dp
                  else 262.dp)
              .padding(vertical = 8.dp)
              .border(
                  width = 1.dp,
                  color = Color.White.copy(alpha = 0.24f),
                  shape = RoundedCornerShape(64.dp))
              .shadow(elevation = 20.dp, shape = RoundedCornerShape(20.dp), clip = true)) {
        Text(
            text = text,
            fontSize = 19.sp,
            fontWeight = FontWeight.W800,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth())
      }
}

@Preview(showBackground = true)
@Composable
fun ProfileScreenPreview() {
  val navController = rememberNavController()
  val navigationActions = NavigationActions(navController)
  ProfileScreen(navigationActions)
}
