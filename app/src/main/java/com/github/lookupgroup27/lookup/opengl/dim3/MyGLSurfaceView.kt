package com.github.lookupgroup27.lookup.opengl.dim3

import android.content.Context
import android.opengl.GLSurfaceView
import android.view.MotionEvent

private const val TOUCH_SCALE_FACTOR: Float = 80.0f / 320f

class MyGLSurfaceView(context: Context) : GLSurfaceView(context) {

  private val renderer: MyGLRenderer

  init {
    // Create an OpenGL ES 2.0 context
    setEGLContextClientVersion(2)

    renderer = MyGLRenderer()



    // Set the Renderer for drawing on the GLSurfaceView
    setRenderer(renderer)

//    renderMode = RENDERMODE_WHEN_DIRTY
  }
}
