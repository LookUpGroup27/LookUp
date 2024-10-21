package com.github.lookupgroup27.lookup.ui.map

import android.opengl.GLSurfaceView
import androidx.compose.runtime.Composable
import androidx.compose.ui.viewinterop.AndroidView

@Composable
fun OpenGLScreen() {
    AndroidView(factory = { context ->
        GLSurfaceView(context).apply {
            setEGLContextClientVersion(2)
            setRenderer(MyGLRenderer())
            renderMode = GLSurfaceView.RENDERMODE_WHEN_DIRTY
        }
    })
}