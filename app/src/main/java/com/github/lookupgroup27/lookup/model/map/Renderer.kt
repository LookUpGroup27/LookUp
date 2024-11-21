package com.github.lookupgroup27.lookup.model.map

import android.opengl.GLES20
import android.opengl.GLSurfaceView
import com.github.lookupgroup27.lookup.model.map.renderables.Object
import com.github.lookupgroup27.lookup.model.map.renderables.Star
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

/**
 * Provides the OpenGL rendering logic for the GLSurfaceView. This class is responsible for drawing
 * the shapes on the screen. It is called by the GLSurfaceView when it is time to redraw the screen.
 */
class Renderer : GLSurfaceView.Renderer {

  private lateinit var shapes: List<Object>

  /** The camera used to draw the shapes on the screen. */
  val camera = Camera()

  override fun onSurfaceCreated(unused: GL10, config: EGLConfig) {
    // Set the background frame color
    GLES20.glClearColor(0.0f, 0.0f, 0.0f, 1.0f)

    GLES20.glEnable(GLES20.GL_DEPTH_TEST)

    // Create the shapes (Make sure you always create the shapes after the OpenGL context is
    // created)
    shapes =
        listOf(
            Star(0.0f, 0f, -1f, floatArrayOf(1.0f, 1.0f, 1.0f)),
            Star(0.24f, 0f, -0.97f, floatArrayOf(1.0f, 1.0f, 1.0f)),
            Star(1f, 0f, 0f, color = floatArrayOf(1.0f, 0.0f, 0.0f)),
            Star(0f, 1f, 0f, color = floatArrayOf(0.0f, 1.0f, 0.0f)),
            Star(0f, 0f, 1f, color = floatArrayOf(0.0f, 0.0f, 1.0f)))
  }

  override fun onDrawFrame(unused: GL10) {
    // Redraw background color
    GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT)
    GLES20.glClear(GLES20.GL_DEPTH_BUFFER_BIT)

    // Draw the shapes
    for (shape in shapes) shape.draw(camera)
  }

  override fun onSurfaceChanged(unused: GL10, width: Int, height: Int) {
    // Specify the size of the rendering window
    GLES20.glViewport(0, 0, width, height)

    val ratio: Float = width.toFloat() / height.toFloat()
    // Defines a projection matrix in terms of a field of view angle, an aspect ratio, and z clip
    // planes
    // It helps projects in correct aspect ratio the objects in the scene
    camera.updateProjectionMatrix(ratio)
  }
}
