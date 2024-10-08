import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.github.lookupgroup27.lookup.R
import androidx.compose.material3.Button
import androidx.navigation.compose.rememberNavController

/**
 * LandingScreen displays the main landing page of the app.
 * It includes a background image, logo, and a button with a home icon.
 * The home button navigates to the "Menu" screen and the background is clickable to navigate to the "Map" screen.
 *
 * @param navController The NavController to handle navigation between screens.
 */

@Composable
fun LandingScreen(navController: NavController) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        // Background Image
        Image(
            painter = painterResource(id = R.drawable.landing_screen_bckgrnd),
            contentDescription = "Background",
            modifier = Modifier
                .fillMaxSize()
                .clickable { navController.navigate("Map") },
            contentScale = ContentScale.Crop
        )

        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Logo Image
            Image(
                painter = painterResource(id = R.drawable.look_up_logo),
                contentDescription = "Look Up Logo",
                modifier = Modifier.size(200.dp),
                contentScale = ContentScale.Fit
            )

            // Home Button with Home Icon
            Button(
                onClick = { navController.navigate("Menu") },
                modifier = Modifier
                    .padding(16.dp)
                    .size(64.dp),
                shape = MaterialTheme.shapes.small,
                colors = ButtonDefaults.buttonColors(containerColor = Color.Gray)
            ) {
                Icon(
                    imageVector = Icons.Default.Home,
                    contentDescription = "Home Icon",
                    tint = Color.Black,
                    modifier = Modifier.size(32.dp)
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewLandingScreen() {
    LandingScreen(navController = rememberNavController())
}
