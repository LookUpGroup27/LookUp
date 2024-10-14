package com.github.lookupgroup27.lookup

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.compose.rememberNavController
import com.github.lookupgroup27.lookup.model.calendar.CalendarViewModel
import com.github.lookupgroup27.lookup.ui.authentication.SignIn
import com.github.lookupgroup27.lookup.ui.map.MapScreen
import com.github.lookupgroup27.lookup.ui.navigation.NavigationActions
import com.github.lookupgroup27.lookup.ui.navigation.Route
import com.github.lookupgroup27.lookup.ui.navigation.Screen
import com.github.lookupgroup27.lookup.ui.overview.CalendarScreen
import com.github.lookupgroup27.lookup.ui.overview.LandingScreen
import com.github.lookupgroup27.lookup.ui.overview.MenuScreen
import com.github.lookupgroup27.lookup.ui.overview.QuizScreen
import com.github.lookupgroup27.lookup.ui.profile.ProfileScreen
import com.github.lookupgroup27.lookup.ui.skytracker.SkyTrackerScreen
import com.google.firebase.auth.FirebaseAuth

class MainActivity : ComponentActivity() {

  private lateinit var auth: FirebaseAuth

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    auth = FirebaseAuth.getInstance()
    auth.currentUser?.let { auth.signOut() }

    setContent { Surface(modifier = Modifier.fillMaxSize()) { LookUpApp() } }
  }
}

@Composable
fun LookUpApp() {
  val navController = rememberNavController()
  val navigationActions = NavigationActions(navController)
  val calendarViewModel: CalendarViewModel = viewModel()

  NavHost(navController = navController, startDestination = Route.LANDING) {
    navigation(
        startDestination = Screen.AUTH,
        route = Route.AUTH,
    ) {
      composable(Screen.AUTH) { SignIn(navigationActions) }
    }

    navigation(
        startDestination = Screen.LANDING,
        route = Route.LANDING,
    ) {
      composable(Screen.LANDING) { LandingScreen(navigationActions) }
      composable(Screen.MENU) { MenuScreen(navigationActions) }
      composable(Screen.MAP) { MapScreen(navigationActions) }
    }

    navigation(
        startDestination = Screen.MENU,
        route = Route.MENU,
    ) {
      composable(Screen.MENU) { MenuScreen(navigationActions) }
      composable(Screen.PROFILE) { ProfileScreen(navigationActions) }

      composable(Screen.CALENDAR) {
        CalendarScreen(calendarViewModel, navigationActions)
      }

      composable(Screen.SKY_TRACKER) { SkyTrackerScreen(navigationActions) }
      composable(Screen.QUIZ) { QuizScreen(navigationActions) }
    }
  }
}
