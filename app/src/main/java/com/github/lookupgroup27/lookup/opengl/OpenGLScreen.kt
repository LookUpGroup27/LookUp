package com.github.lookupgroup27.lookup.opengl

import androidx.compose.runtime.Composable
import androidx.compose.ui.viewinterop.AndroidView
import com.github.lookupgroup27.lookup.opengl.dim3.MyGLSurfaceView

@Composable
fun OpenGLScreen() {
  AndroidView(factory = { context -> MyGLSurfaceView(context) })
}
