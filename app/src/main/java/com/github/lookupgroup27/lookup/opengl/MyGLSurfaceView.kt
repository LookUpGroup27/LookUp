package com.github.lookupgroup27.lookup.opengl

import android.content.Context
import android.opengl.GLSurfaceView

class MyGLSurfaceView(context: Context, renderer: MyGLRenderer) : GLSurfaceView(context) {

  init {
    // Create an OpenGL ES 2.0 context
    setEGLContextClientVersion(2)

    // Set the provided renderer for drawing on the GLSurfaceView
    setRenderer(renderer)
  }
}
