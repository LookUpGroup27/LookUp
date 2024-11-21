package com.github.lookupgroup27.lookup.model.map

import android.opengl.GLES20
import android.opengl.GLSurfaceView
import android.opengl.Matrix
import com.github.lookupgroup27.lookup.model.map.skybox.SkyBox
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

/**
 * Provides the OpenGL rendering logic for the GLSurfaceView. This class is responsible for drawing
 * the shapes on the screen. It is called by the GLSurfaceView when it is time to redraw the screen.
 */
class Renderer : GLSurfaceView.Renderer {

  private lateinit var skyBox: SkyBox

  /** The camera used to draw the shapes on the screen. */
  val camera = Camera()

  // Temporary storage for the MVP matrix
  private val mvpMatrix = FloatArray(16)
  private val viewMatrix = FloatArray(16)
  private val projectionMatrix = FloatArray(16)

  override fun onSurfaceCreated(unused: GL10, config: EGLConfig) {
    // Set the background frame color
    GLES20.glClearColor(0.0f, 0.0f, 0.0f, 1.0f)

    GLES20.glEnable(GLES20.GL_DEPTH_TEST)

    // Initialize the SkyBox
    skyBox = SkyBox()
    skyBox.initialize()

    // Initialize the camera's matrices
    Matrix.setIdentityM(viewMatrix, 0)
    Matrix.setIdentityM(projectionMatrix, 0)
  }

  override fun onDrawFrame(unused: GL10) {

    // Clear the screen
    GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT or GLES20.GL_DEPTH_BUFFER_BIT)

    // Calculate the MVP matrix (Model-View-Projection matrix)
    Matrix.multiplyMM(mvpMatrix, 0, projectionMatrix, 0, viewMatrix, 0)

    // Render the SkyBox
    skyBox.render(mvpMatrix)
  }

  override fun onSurfaceChanged(unused: GL10, width: Int, height: Int) {
    // Specify the size of the rendering window
    GLES20.glViewport(0, 0, width, height)

    val ratio: Float = width.toFloat() / height.toFloat()

    camera.updateProjectionMatrix(ratio, projectionMatrix)

    // Update the view matrix
    Matrix.setLookAtM(
        viewMatrix,
        0,
        0f,
        0f,
        3f, // Eye position
        0f,
        0f,
        0f, // Center position
        0f,
        1f,
        0f // Up vector
        )
  }
}
