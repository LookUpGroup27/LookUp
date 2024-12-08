package components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource

/**
 * A composable function that displays a full-screen background image with an optional test tag for
 * UI testing purposes.
 *
 * This function is typically used to set a background for a screen with an image resource.
 *
 * @param painterResId The resource ID of the image to be used as the background.
 * @param contentDescription A description of the image, primarily for accessibility purposes. Pass
 *   a meaningful string that describes the image's purpose or content.
 * @param testTag An optional string used to identify this composable in UI tests. Defaults to null.
 */
@Composable
fun BackgroundImage(painterResId: Int, contentDescription: String, testTag: String? = null) {
  Image(
      painter = painterResource(id = painterResId),
      contentDescription = contentDescription,
      contentScale = ContentScale.Crop,
      modifier = Modifier.fillMaxSize().let { if (testTag != null) it.testTag(testTag) else it })
}
