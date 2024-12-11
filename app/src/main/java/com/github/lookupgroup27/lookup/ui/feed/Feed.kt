package com.github.lookupgroup27.lookup.ui.feed

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.github.lookupgroup27.lookup.R
import com.github.lookupgroup27.lookup.model.feed.ProximityAndTimePostFetcher
import com.github.lookupgroup27.lookup.model.location.LocationProviderSingleton
import com.github.lookupgroup27.lookup.model.post.Post
import com.github.lookupgroup27.lookup.model.profile.UserProfile
import com.github.lookupgroup27.lookup.ui.feed.components.PostItem
import com.github.lookupgroup27.lookup.ui.navigation.BottomNavigationMenu
import com.github.lookupgroup27.lookup.ui.navigation.LIST_TOP_LEVEL_DESTINATION
import com.github.lookupgroup27.lookup.ui.navigation.NavigationActions
import com.github.lookupgroup27.lookup.ui.navigation.Route
import com.github.lookupgroup27.lookup.ui.post.PostsViewModel
import com.github.lookupgroup27.lookup.ui.profile.ProfileViewModel
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.delay

private const val LOCATION_PERMISSION_REQUEST_CODE = 1001
private const val NUMBER_OF_STARS = 3

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("StateFlowValueCalledInComposition")
@Composable
fun FeedScreen(
    postsViewModel: PostsViewModel,
    navigationActions: NavigationActions,
    profileViewModel: ProfileViewModel,
    initialNearbyPosts: List<Post>? = null
) {
    profileViewModel.fetchUserProfile()
    val profile = profileViewModel.userProfile.value
    val user = FirebaseAuth.getInstance().currentUser
    val isUserLoggedIn = user != null
    val userEmail = user?.email ?: ""
    val username by remember { mutableStateOf(profile?.username ?: "") }
    val bio by remember { mutableStateOf(profile?.bio ?: "") }
    val email by remember { mutableStateOf(userEmail) }

    val context = LocalContext.current
    val locationProvider = LocationProviderSingleton.getInstance(context)
    val proximityAndTimePostFetcher = remember {
        ProximityAndTimePostFetcher(postsViewModel, context)
    }

    var locationPermissionGranted by remember { mutableStateOf(false) }
    val unfilteredPosts by (
            initialNearbyPosts?.let { mutableStateOf(it) }
                ?: proximityAndTimePostFetcher.nearbyPosts.collectAsState()
            )
    val nearbyPosts = unfilteredPosts.filter { it.username != userEmail }

    val postRatings = remember { mutableStateMapOf<String, List<Boolean>>() }

    LaunchedEffect(Unit) {
        locationPermissionGranted =
            ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) ==
                    PackageManager.PERMISSION_GRANTED

        if (!locationPermissionGranted) {
            Toast.makeText(
                context, context.getString(R.string.location_permission_required), Toast.LENGTH_LONG
            ).show()
        } else {
            // Wait until location is available, then fetch posts
            while (locationProvider.currentLocation.value == null) {
                delay(500)
            }
            proximityAndTimePostFetcher.fetchSortedPosts()
        }
    }

    LaunchedEffect(nearbyPosts, profile) {
        nearbyPosts.forEach { post ->
            if (!postRatings.containsKey(post.uid)) {
                val savedRating = profile?.ratings?.get(post.uid) ?: 0
                val initialRating = List(NUMBER_OF_STARS) { index -> index < savedRating }
                postRatings[post.uid] = initialRating.toMutableList()
            }
        }
    }

    // Background Box with image
    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        Image(
            painter = painterResource(R.drawable.background_blurred),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        // Add a subtle gradient overlay to make text and posts pop
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        listOf(Color.Black.copy(alpha = 0.6f), Color.Transparent, Color.Black.copy(alpha = 0.6f))
                    )
                )
        )

        Scaffold(
            containerColor = Color.Transparent,
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            text = stringResource(R.string.nearby_posts_title),
                            style = MaterialTheme.typography.titleMedium.copy(color = Color.White)
                        )
                    },
                    colors = TopAppBarDefaults.smallTopAppBarColors(containerColor = Color.Black.copy(alpha = 0.5f))
                )
            },
            bottomBar = {
                Box(Modifier.testTag(stringResource(R.string.bottom_navigation_menu))) {
                    BottomNavigationMenu(
                        onTabSelect = { destination -> navigationActions.navigateTo(destination) },
                        tabList = LIST_TOP_LEVEL_DESTINATION,
                        isUserLoggedIn = isUserLoggedIn,
                        selectedItem = Route.FEED
                    )
                }
            },
            modifier = Modifier.testTag(stringResource(R.string.feed_screen_test_tag))
        ) { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(horizontal = 8.dp)
            ) {
                if (nearbyPosts.isEmpty()) {
                    // Loading or empty state
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .testTag(stringResource(R.string.loading_indicator_test_tag)),
                        contentAlignment = Alignment.Center
                    ) {
                        if (!locationPermissionGranted) {
                            Text(
                                text = stringResource(R.string.location_permission_required),
                                style = MaterialTheme.typography.bodyLarge.copy(color = Color.White)
                            )
                        } else {
                            CircularProgressIndicator(color = Color.White)
                        }
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(vertical = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(30.dp)
                    ) {
                        items(nearbyPosts) { post ->
                            PostItem(
                                post = post,
                                starStates = postRatings[post.uid] ?: mutableListOf(false, false, false),
                                onRatingChanged = { newRating ->
                                    val oldPostRatings = postRatings[post.uid] ?: mutableListOf(false, false, false)
                                    val oldStarCounts = oldPostRatings.count { it }
                                    postRatings[post.uid] = newRating.toList()
                                    val starsCount = newRating.count { it }

                                    val updatedRatings = profile?.ratings?.toMutableMap()
                                    updatedRatings?.set(post.uid, starsCount)
                                    val newProfile: UserProfile = profile?.copy(
                                        username = username,
                                        bio = bio,
                                        email = email,
                                        ratings = updatedRatings ?: emptyMap()
                                    ) ?: UserProfile(
                                        username = username,
                                        bio = bio,
                                        email = email,
                                        ratings = updatedRatings ?: emptyMap()
                                    )
                                    profileViewModel.updateUserProfile(newProfile)

                                    val isReturningUser = post.ratedBy.contains(userEmail)
                                    val newStarsCount = if (isReturningUser) {
                                        post.starsCount - oldStarCounts + starsCount
                                    } else {
                                        post.starsCount + starsCount
                                    }
                                    val newUsersNumber = if (isReturningUser) post.usersNumber else post.usersNumber + 1
                                    val newAvg = newStarsCount.toDouble() / newUsersNumber

                                    postsViewModel.updatePost(
                                        post.copy(
                                            averageStars = newAvg,
                                            starsCount = newStarsCount,
                                            usersNumber = newUsersNumber,
                                            ratedBy = if (!isReturningUser) {
                                                post.ratedBy + userEmail
                                            } else {
                                                post.ratedBy
                                            }
                                        )
                                    )
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}
