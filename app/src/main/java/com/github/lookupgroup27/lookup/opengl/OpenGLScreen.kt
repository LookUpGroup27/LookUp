package com.github.lookupgroup27.lookup.opengl

import androidx.compose.runtime.Composable
import androidx.compose.ui.viewinterop.AndroidView

@Composable
fun OpenGLScreen() {
  AndroidView(factory = { context -> MyGLSurfaceView(context) })
}
