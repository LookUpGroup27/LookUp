package com.github.lookupgroup27.lookup.opengl

import android.content.Context
import android.opengl.GLSurfaceView

class MyGLSurfaceView(context: Context) : GLSurfaceView(context) {

  private val renderer: MyGLRenderer

  init {
    // Create an OpenGL ES 2.0 context
    setEGLContextClientVersion(2)

    renderer = MyGLRenderer()

    // Set the Renderer for drawing on the GLSurfaceView
    setRenderer(renderer)

    renderMode = RENDERMODE_WHEN_DIRTY
  }

  fun turnLeft() {
    renderer.camera.turnLeft()
    requestRender()
  }

  fun turnRight() {
    renderer.camera.turnRight()
    requestRender()
  }

  fun turnUp() {
    renderer.camera.turnUp()
    requestRender()
  }

  fun turnDown() {
    renderer.camera.turnDown()
    requestRender()
  }

  fun tiltLeft() {
    renderer.camera.tiltLeft()
    requestRender()
  }

  fun tiltRight() {
    renderer.camera.tiltRight()
    requestRender()
  }
}
