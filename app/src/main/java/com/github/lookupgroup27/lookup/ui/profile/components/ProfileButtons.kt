package com.github.lookupgroup27.lookup.ui.profile.components

import android.content.res.Configuration
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.github.lookupgroup27.lookup.ui.theme.LightPurple
import com.github.lookupgroup27.lookup.ui.theme.StarLightWhite

@Composable
fun ProfileButton(text: String, onClick: () -> Unit) {
  Button(
      onClick = onClick,
      colors =
          ButtonDefaults.buttonColors(containerColor = LightPurple, contentColor = StarLightWhite),
      shape = RoundedCornerShape(174.dp),
      modifier =
          Modifier.width(
                  if (LocalConfiguration.current.orientation == Configuration.ORIENTATION_LANDSCAPE)
                      200.dp
                  else 262.dp)
              .height(80.dp)
              .padding(vertical = 8.dp)
              .border(
                  width = 1.dp,
                  color = Color.White.copy(alpha = 0.24f),
                  shape = RoundedCornerShape(64.dp))
              .shadow(elevation = 20.dp, shape = RoundedCornerShape(20.dp), clip = true)) {
        Text(
            text = text,
            fontSize = 21.sp,
            fontWeight = FontWeight.W800,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth())
      }
}
