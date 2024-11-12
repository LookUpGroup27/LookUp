package com.github.lookupgroup27.lookup.ui.feed

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.github.lookupgroup27.lookup.R
import com.github.lookupgroup27.lookup.ui.navigation.BottomNavigationMenu
import com.github.lookupgroup27.lookup.ui.navigation.LIST_TOP_LEVEL_DESTINATION
import com.github.lookupgroup27.lookup.ui.navigation.NavigationActions
import com.github.lookupgroup27.lookup.ui.navigation.Route

// Define a data class for the posts
data class Post(val username: String, val imageResId: Int)

@Composable
fun FeedScreen(navigationActions: NavigationActions) {
  // Create a list of posts with resource IDs of the images
  val posts =
      listOf(
          Post(username = "User1", imageResId = R.drawable.image1),
          Post(username = "User2", imageResId = R.drawable.image2))

  Scaffold(
      bottomBar = {
        BottomNavigationMenu(
            onTabSelect = { destination -> navigationActions.navigateTo(destination) },
            tabList = LIST_TOP_LEVEL_DESTINATION,
            selectedItem = Route.FEED)
      },
      modifier = Modifier.fillMaxWidth().testTag("feed_screen")) { innerPadding ->
        LazyColumn(
            contentPadding = innerPadding,
            modifier = Modifier.fillMaxSize().padding(horizontal = 8.dp),
            verticalArrangement = Arrangement.spacedBy(30.dp)) {
              items(posts) { post -> PostItem(post = post) }
            }
      }
}

@Composable
fun PostItem(post: Post) {
  Column(modifier = Modifier.fillMaxWidth().padding(8.dp)) {
    // Username row
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp, vertical = 4.dp)) {
          Text(
              text = post.username,
              style =
                  MaterialTheme.typography.bodyMedium.copy(
                      fontWeight = FontWeight.Bold, color = Color.Black),
              modifier = Modifier.padding(start = 4.dp).testTag("UsernameTag_${post.username}"))
        }

    // Image section
    Image(
        painter = painterResource(id = post.imageResId),
        contentDescription = "Post Image for ${post.username}",
        modifier = Modifier.fillMaxWidth().height(300.dp),
        contentScale = ContentScale.Crop)
  }
}
