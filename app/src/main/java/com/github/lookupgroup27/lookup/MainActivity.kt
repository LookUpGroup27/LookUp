package com.github.lookupgroup27.lookup

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.*
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.*
import androidx.navigation.navigation
import com.github.lookupgroup27.lookup.model.calendar.CalendarViewModel
import com.github.lookupgroup27.lookup.resources.C
import com.github.lookupgroup27.lookup.ui.map.MapScreen
import com.github.lookupgroup27.lookup.ui.navigation.*
import com.github.lookupgroup27.lookup.ui.overview.*
import com.github.lookupgroup27.lookup.ui.theme.SampleAppTheme

class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContent {
      SampleAppTheme {
        // A surface container using the 'background' color from the theme
        Surface(
            modifier = Modifier.fillMaxSize().semantics { testTag = C.Tag.main_screen_container },
            color = MaterialTheme.colorScheme.background) {
              LookUpApp()
            }
      }
    }
  }

  @Composable
  fun LookUpApp() {
    val navController = rememberNavController()
    val navigationActions = NavigationActions(navController)
    val calendarViewModel: CalendarViewModel = viewModel()

    NavHost(navController = navController, startDestination = Route.LANDING) {
      navigation(
          startDestination = Screen.LANDING,
          route = Route.LANDING,
      ) {
        composable(Screen.LANDING) { LandingScreen(navigationActions) }
      }
      navigation(startDestination = Screen.MENU, route = Route.MENU) {
        composable(Screen.MENU) { MenuScreen(navigationActions) }
        composable(Screen.QUIZ) { QuizScreen(navigationActions) }
        composable(Screen.CALENDAR) { CalendarScreen(calendarViewModel, navigationActions) }
      }
      navigation(startDestination = Screen.MAP, route = Route.MAP) {
        composable(Screen.MAP) { MapScreen(navigationActions) }
      }
    }
  }
}
