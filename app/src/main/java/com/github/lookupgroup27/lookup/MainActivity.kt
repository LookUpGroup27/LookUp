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
import androidx.navigation.compose.*
import androidx.navigation.navArgument
import com.github.lookupgroup27.lookup.ui.authentication.SignInScreen
import com.github.lookupgroup27.lookup.ui.calendar.CalendarScreen
import com.github.lookupgroup27.lookup.ui.calendar.CalendarViewModel
import com.github.lookupgroup27.lookup.ui.feed.FeedScreen
import com.github.lookupgroup27.lookup.ui.fullscreen.FullScreenImageScreen
import com.github.lookupgroup27.lookup.ui.googlemap.GoogleMapScreen
import com.github.lookupgroup27.lookup.ui.googlemap.components.SelectedPostMarker
import com.github.lookupgroup27.lookup.ui.image.CameraCapture
import com.github.lookupgroup27.lookup.ui.image.EditImageScreen
import com.github.lookupgroup27.lookup.ui.image.EditImageViewModel
import com.github.lookupgroup27.lookup.ui.image.ImageReviewScreen
import com.github.lookupgroup27.lookup.ui.image.ImageViewModel
import com.github.lookupgroup27.lookup.ui.login.LoginScreen
import com.github.lookupgroup27.lookup.ui.login.LoginViewModel
import com.github.lookupgroup27.lookup.ui.map.MapScreen
import com.github.lookupgroup27.lookup.ui.map.MapViewModel
import com.github.lookupgroup27.lookup.ui.navigation.NavigationActions
import com.github.lookupgroup27.lookup.ui.navigation.Route
import com.github.lookupgroup27.lookup.ui.navigation.Screen
import com.github.lookupgroup27.lookup.ui.overview.LandingScreen
import com.github.lookupgroup27.lookup.ui.overview.MenuScreen
import com.github.lookupgroup27.lookup.ui.passwordreset.PasswordResetScreen
import com.github.lookupgroup27.lookup.ui.passwordreset.PasswordResetViewModel
import com.github.lookupgroup27.lookup.ui.planetselection.PlanetSelectionScreen
import com.github.lookupgroup27.lookup.ui.planetselection.PlanetSelectionViewModel
import com.github.lookupgroup27.lookup.ui.post.PostsViewModel
import com.github.lookupgroup27.lookup.ui.profile.CollectionScreen
import com.github.lookupgroup27.lookup.ui.profile.CollectionViewModel
import com.github.lookupgroup27.lookup.ui.profile.ProfileInformationScreen
import com.github.lookupgroup27.lookup.ui.profile.ProfileScreen
import com.github.lookupgroup27.lookup.ui.profile.ProfileViewModel
import com.github.lookupgroup27.lookup.ui.profile.profilepic.AvatarSelectionScreen
import com.github.lookupgroup27.lookup.ui.profile.profilepic.AvatarViewModel
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

    setContent { LookUpTheme { Surface(modifier = Modifier.fillMaxSize()) { LookUpApp() } } }
  }
}

@Composable
fun LookUpApp() {
  val context = LocalContext.current
  val navController = rememberNavController()
  val navigationActions = NavigationActions(navController)
  val calendarViewModel: CalendarViewModel = viewModel(factory = CalendarViewModel.Factory)
  val quizViewModel: QuizViewModel = viewModel(factory = QuizViewModel.provideFactory(context))
  val imageViewModel: ImageViewModel = viewModel(factory = ImageViewModel.Factory)
  val profileViewModel: ProfileViewModel = viewModel(factory = ProfileViewModel.Factory)
  val collectionViewModel: CollectionViewModel = viewModel(factory = CollectionViewModel.Factory)
  val postsViewModel: PostsViewModel = viewModel(factory = PostsViewModel.Factory)
  val registerViewModel: RegisterViewModel = viewModel(factory = RegisterViewModel.Factory)
  val editImageViewModel: EditImageViewModel = viewModel(factory = EditImageViewModel.Factory)
  val mapViewModel: MapViewModel = viewModel(factory = MapViewModel.createFactory(context))
  val passwordResetViewModel: PasswordResetViewModel =
      viewModel(factory = PasswordResetViewModel.Factory)
  val avatarViewModel: AvatarViewModel = viewModel(factory = AvatarViewModel.Factory)
  val loginViewModel: LoginViewModel = viewModel(factory = LoginViewModel.Factory)
  val planetSelectionViewModel: PlanetSelectionViewModel =
      viewModel(factory = PlanetSelectionViewModel.createFactory(context))

  NavHost(navController = navController, startDestination = Route.LANDING) {
    navigation(
        startDestination = Screen.AUTH,
        route = Route.AUTH,
    ) {
      composable(Screen.AUTH) { SignInScreen(navigationActions) }
      composable(Screen.PASSWORDRESET) {
        PasswordResetScreen(passwordResetViewModel, navigationActions)
      }
      composable(Screen.LOGIN) { LoginScreen(loginViewModel, navigationActions) }
      composable(Screen.REGISTER) { RegisterScreen(navigationActions, registerViewModel) }
    }

    navigation(startDestination = Screen.SKY_MAP, route = Route.SKY_MAP) {
      composable(Screen.SKY_MAP) { MapScreen(navigationActions, mapViewModel) }
    }

    navigation(
        startDestination = Screen.LANDING,
        route = Route.LANDING,
    ) {
      composable(Screen.LANDING) { LandingScreen(navigationActions) }
      composable(Screen.MENU) { MenuScreen(navigationActions, avatarViewModel) }
    }

    navigation(
        startDestination = Screen.MENU,
        route = Route.MENU,
    ) {
      composable(Screen.MENU) { MenuScreen(navigationActions, avatarViewModel) }
      composable(Screen.PROFILE) { ProfileScreen(navigationActions, avatarViewModel) }
      composable(Screen.CALENDAR) { CalendarScreen(calendarViewModel, navigationActions) }
      composable(
          route = "${Route.GOOGLE_MAP}/{postId}/{lat}/{lon}/{autoCenter}",
          arguments =
              listOf(
                  navArgument("postId") {
                    type = NavType.StringType
                    nullable = true
                  },
                  navArgument("lat") {
                    type = NavType.FloatType
                    defaultValue = 0f
                  },
                  navArgument("lon") {
                    type = NavType.FloatType
                    defaultValue = 0f
                  },
                  navArgument("autoCenter") {
                    type = NavType.BoolType
                    defaultValue = true
                  })) { backStackEntry ->
            val postId = backStackEntry.arguments?.getString("postId")
            val lat = backStackEntry.arguments?.getFloat("lat")?.toDouble() ?: 0.0
            val lon = backStackEntry.arguments?.getFloat("lon")?.toDouble() ?: 0.0
            val autoCenter = backStackEntry.arguments?.getBoolean("autoCenter") ?: true

            val selectedMarker =
                if (postId != null) {
                  SelectedPostMarker(postId, lat, lon)
                } else null

            GoogleMapScreen(
                navigationActions = navigationActions,
                postsViewModel = postsViewModel,
                profileViewModel = profileViewModel,
                selectedPostMarker = selectedMarker,
                initialAutoCenterEnabled = autoCenter)
          }

      composable(Screen.GOOGLE_MAP) {
        GoogleMapScreen(
            navigationActions = navigationActions,
            postsViewModel = postsViewModel,
            profileViewModel = profileViewModel)
      }
      composable(Screen.QUIZ) { QuizScreen(quizViewModel, navigationActions) }
      composable(Screen.PLANET_SELECTION) {
        PlanetSelectionScreen(planetSelectionViewModel, navigationActions)
      }
    }

    navigation(startDestination = Screen.QUIZ, route = Route.QUIZ) {
      composable(Screen.QUIZ) { QuizScreen(quizViewModel, navigationActions) }
      composable(Screen.QUIZ_PLAY) { QuizPlayScreen(quizViewModel, navigationActions) }
    }

    navigation(startDestination = Screen.PROFILE, route = Route.PROFILE) {
      composable(Screen.COLLECTION) { CollectionScreen(navigationActions, collectionViewModel) }
      composable(Screen.PROFILE) { ProfileScreen(navigationActions, avatarViewModel) }
      composable(Screen.PROFILE_INFORMATION) {
        ProfileInformationScreen(profileViewModel, navigationActions)
      }

      composable(Screen.AVATAR_SELECTION) {
        AvatarSelectionScreen(
            avatarViewModel = avatarViewModel,
            userId = FirebaseAuth.getInstance().currentUser?.uid ?: "",
            navigationActions = navigationActions)
      }

      composable(
          route =
              "${Route.EDIT_IMAGE}/{postUri}/{postAverageStar}/{postRatedByNb}/{postUid}/{postDescription}",
          arguments =
              listOf(
                  navArgument("postUri") { type = NavType.StringType },
                  navArgument("postAverageStar") { type = NavType.FloatType },
                  navArgument("postRatedByNb") { type = NavType.IntType },
                  navArgument("postUid") { type = NavType.StringType },
                  navArgument("postDescription") { type = NavType.StringType })) { backStackEntry ->
            val postUri = backStackEntry.arguments?.getString("postUri") ?: ""
            val postAverageStar = backStackEntry.arguments?.getFloat("postAverageStar") ?: 0.0f
            val postRatedByNb = backStackEntry.arguments?.getInt("postRatedByNb") ?: 0
            val postUid = backStackEntry.arguments?.getString("postUid") ?: ""
            val postDescription = backStackEntry.arguments?.getString("postDescription") ?: ""

            EditImageScreen(
                postUri = postUri,
                postAverageStar = postAverageStar.toDouble(),
                postRatedByNb = postRatedByNb,
                postUid = postUid,
                editImageViewModel = editImageViewModel,
                collectionViewModel = collectionViewModel,
                postsViewModel = postsViewModel,
                postDescription = postDescription,
                navigationActions = navigationActions)
          }
    }

    navigation(startDestination = Screen.TAKE_IMAGE, route = Route.TAKE_IMAGE) {
      composable(Screen.TAKE_IMAGE) { CameraCapture(navigationActions) }
      composable(
          route = "${Route.IMAGE_REVIEW}/{imageFile}/{timestamp}",
          arguments =
              listOf(
                  navArgument("imageFile") { type = NavType.StringType },
                  navArgument("timestamp") { type = NavType.LongType })) { backStackEntry ->
            val imageFile = backStackEntry.arguments?.getString("imageFile")?.let { File(it) }
            val timestamp = backStackEntry.arguments?.getLong("timestamp")
            ImageReviewScreen(
                navigationActions = navigationActions,
                imageFile = imageFile,
                imageViewModel = imageViewModel,
                postsViewModel = postsViewModel,
                collectionViewModel = collectionViewModel,
                timestamp = timestamp)
          }
    }

    navigation(startDestination = Screen.FEED, route = Route.FEED) {
      composable(Screen.FEED) { FeedScreen(postsViewModel, navigationActions, profileViewModel) }

      composable(
          route = "${Route.FULLSCREEN_IMAGE}/{imageUrl}/{username}/{description}",
          arguments =
              listOf(
                  navArgument("imageUrl") { type = NavType.StringType },
                  navArgument("username") { type = NavType.StringType },
                  navArgument("description") { type = NavType.StringType })) { backStackEntry ->
            val imageUrl = backStackEntry.arguments?.getString("imageUrl") ?: ""
            val username = backStackEntry.arguments?.getString("username") ?: ""
            val description = backStackEntry.arguments?.getString("description") ?: ""

            FullScreenImageScreen(
                imageUrl = imageUrl,
                onBack = { navController.popBackStack() },
                username = username,
                description = description)
          }
    }

    navigation(startDestination = Screen.PASSWORDRESET, route = Route.PASSWORDRESET) {
      composable(Screen.PASSWORDRESET) {
        PasswordResetScreen(passwordResetViewModel, navigationActions)
      }
    }

    navigation(startDestination = Screen.REGISTER, route = Route.REGISTER) {
      composable(Screen.REGISTER) { RegisterScreen(navigationActions, registerViewModel) }
      composable(Screen.AUTH) { SignInScreen(navigationActions) }
    }
  }
}
