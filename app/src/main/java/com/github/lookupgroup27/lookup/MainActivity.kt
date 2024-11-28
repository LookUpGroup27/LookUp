package com.github.lookupgroup27.lookup

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.github.lookupgroup27.lookup.ui.authentication.SignInScreen
import com.github.lookupgroup27.lookup.ui.calendar.CalendarScreen
import com.github.lookupgroup27.lookup.ui.calendar.CalendarViewModel
import com.github.lookupgroup27.lookup.ui.feed.FeedScreen
import com.github.lookupgroup27.lookup.ui.googlemap.GoogleMapScreen
import com.github.lookupgroup27.lookup.ui.image.CameraCapture
import com.github.lookupgroup27.lookup.ui.image.EditImageScreen
import com.github.lookupgroup27.lookup.ui.image.EditImageViewModel
import com.github.lookupgroup27.lookup.ui.image.ImageReviewScreen
import com.github.lookupgroup27.lookup.ui.map.MapScreen
import com.github.lookupgroup27.lookup.ui.map.MapViewModel
import com.github.lookupgroup27.lookup.ui.navigation.NavigationActions
import com.github.lookupgroup27.lookup.ui.navigation.Route
import com.github.lookupgroup27.lookup.ui.navigation.Screen
import com.github.lookupgroup27.lookup.ui.overview.LandingScreen
import com.github.lookupgroup27.lookup.ui.overview.MenuScreen
import com.github.lookupgroup27.lookup.ui.post.PostsViewModel
import com.github.lookupgroup27.lookup.ui.profile.CollectionScreen
import com.github.lookupgroup27.lookup.ui.profile.CollectionViewModel
import com.github.lookupgroup27.lookup.ui.profile.ProfileInformationScreen
import com.github.lookupgroup27.lookup.ui.profile.ProfileScreen
import com.github.lookupgroup27.lookup.ui.profile.ProfileViewModel
import com.github.lookupgroup27.lookup.ui.quiz.QuizPlayScreen
import com.github.lookupgroup27.lookup.ui.quiz.QuizScreen
import com.github.lookupgroup27.lookup.ui.quiz.QuizViewModel
import com.github.lookupgroup27.lookup.ui.register.RegisterScreen
import com.github.lookupgroup27.lookup.ui.register.RegisterViewModel
import com.github.lookupgroup27.lookup.ui.theme.LookUpTheme
import com.google.firebase.auth.FirebaseAuth
import java.io.File

class MainActivity : ComponentActivity() {

  private lateinit var auth: FirebaseAuth

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    auth = FirebaseAuth.getInstance()
    // auth.currentUser?.let { auth.signOut() }

    setContent { LookUpTheme { Surface(modifier = Modifier.fillMaxSize()) { LookUpApp() } } }
  }
}

@Composable
fun LookUpApp() {
  val navController = rememberNavController()
  val navigationActions = NavigationActions(navController)
  val calendarViewModel: CalendarViewModel = viewModel(factory = CalendarViewModel.Factory)
  val quizViewModel: QuizViewModel =
      viewModel(factory = QuizViewModel.provideFactory(context = LocalContext.current))

  val profileViewModel: ProfileViewModel = viewModel(factory = ProfileViewModel.Factory)
  val collectionViewModel: CollectionViewModel = viewModel(factory = CollectionViewModel.Factory)
  val postsViewModel: PostsViewModel = viewModel(factory = PostsViewModel.Factory)
  val registerViewModel: RegisterViewModel = viewModel(factory = RegisterViewModel.Factory)
  val editImageViewModel: EditImageViewModel = viewModel(factory = EditImageViewModel.Factory)
  val mapViewModel: MapViewModel = viewModel()

  NavHost(navController = navController, startDestination = Route.LANDING) {
    navigation(
        startDestination = Screen.AUTH,
        route = Route.AUTH,
    ) {
      composable(Screen.AUTH) { SignInScreen(navigationActions) }
      composable(Screen.REGISTER) { RegisterScreen(registerViewModel, navigationActions) }
    }
    navigation(startDestination = Screen.MAP, route = Route.MAP) {
      composable(Screen.MAP) { MapScreen(navigationActions, mapViewModel) }
    }
    navigation(
        startDestination = Screen.LANDING,
        route = Route.LANDING,
    ) {
      composable(Screen.LANDING) { LandingScreen(navigationActions) }
      composable(Screen.MENU) { MenuScreen(navigationActions) }
    }

    navigation(
        startDestination = Screen.MENU,
        route = Route.MENU,
    ) {
      composable(Screen.MENU) { MenuScreen(navigationActions) }
      composable(Screen.PROFILE) { ProfileScreen(navigationActions) }
      composable(Screen.CALENDAR) { CalendarScreen(calendarViewModel, navigationActions) }
      composable(Screen.GOOGLE_MAP) {
        GoogleMapScreen(navigationActions, postsViewModel, profileViewModel)
      }
      composable(Screen.QUIZ) { QuizScreen(quizViewModel, navigationActions) }
    }

    navigation(startDestination = Screen.QUIZ, route = Route.QUIZ) {
      composable(Screen.QUIZ) { QuizScreen(quizViewModel, navigationActions) }
      composable(Screen.QUIZ_PLAY) { QuizPlayScreen(quizViewModel, navigationActions) }
    }

    navigation(startDestination = Screen.PROFILE, route = Route.PROFILE) {
      composable(Screen.COLLECTION) { CollectionScreen(navigationActions, collectionViewModel) }
      composable(Screen.PROFILE) { ProfileScreen(navigationActions) }
      composable(Screen.PROFILE_INFORMATION) {
        ProfileInformationScreen(profileViewModel, navigationActions)
      }
      composable(
          route = "${Route.EDIT_IMAGE}/{imageUrl}",
          arguments = listOf(navArgument("imageUrl") { type = NavType.StringType })) {
              backStackEntry ->
            val imageUrl = backStackEntry.arguments?.getString("imageUrl")
            imageUrl?.let {
              EditImageScreen(
                  imageUrl = it,
                  navigationActions = navigationActions,
                  collectionViewModel = collectionViewModel,
                  editImageViewModel = editImageViewModel,
                  postsViewModel = postsViewModel)
            }
          }
    }

    navigation(startDestination = Screen.TAKE_IMAGE, route = Route.TAKE_IMAGE) {
      composable(Screen.TAKE_IMAGE) { CameraCapture(navigationActions) }
      composable(
          route = "${Route.IMAGE_REVIEW}/{imageFile}",
          arguments = listOf(navArgument("imageFile") { type = NavType.StringType })) {
              backStackEntry ->
            val imageFile = backStackEntry.arguments?.getString("imageFile")?.let { File(it) }
            ImageReviewScreen(
                navigationActions = navigationActions,
                imageFile = imageFile,
                postsViewModel = postsViewModel)
          }
    }

    navigation(startDestination = Screen.FEED, route = Route.FEED) {
      composable(Screen.FEED) { FeedScreen(postsViewModel, navigationActions, profileViewModel) }
    }
    navigation(startDestination = Screen.REGISTER, route = Route.REGISTER) {
      composable(Screen.REGISTER) { RegisterScreen(registerViewModel, navigationActions) }
      composable(Screen.AUTH) { SignInScreen(navigationActions) }
    }
  }
}
