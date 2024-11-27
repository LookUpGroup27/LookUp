package com.github.lookupgroup27.lookup.model.map

import android.content.Context
import android.opengl.GLES20
import android.opengl.GLSurfaceView
import android.opengl.Matrix
import com.github.lookupgroup27.lookup.R
import com.github.lookupgroup27.lookup.model.map.renderables.Planet
import com.github.lookupgroup27.lookup.model.map.skybox.SkyBox
import com.github.lookupgroup27.lookup.util.opengl.TextureManager
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

/**
 * Provides the OpenGL rendering logic for the GLSurfaceView. This class is responsible for drawing
 * the shapes on the screen. It is called by the GLSurfaceView when it is time to redraw the screen.
 */
class Renderer(private val context: Context) : GLSurfaceView.Renderer {

  private lateinit var textureManager: TextureManager
  private lateinit var skyBox: SkyBox
  private lateinit var planet: Planet

  private var skyBoxTextureHandle: Int = -1 // Handle for the skybox texture

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

    // Initialize TextureManager
    textureManager = TextureManager(context)

    // Load the skybox texture (replace with your texture resource ID)
    skyBoxTextureHandle = textureManager.loadTexture(R.drawable.skybox_texture)

    // Initialize the SkyBox
    skyBox = SkyBox()

    planet = Planet(context, textureId = R.drawable.planet_texture)
    planet.initialize()

    // Initialize the camera's matrices
    Matrix.setIdentityM(camera.viewMatrix, 0)
    Matrix.setIdentityM(projectionMatrix, 0)
  }

  override fun onDrawFrame(unused: GL10) {

    // Clear the screen
    GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT or GLES20.GL_DEPTH_BUFFER_BIT)

    // Calculate the MVP matrix (Model-View-Projection matrix) for the objects
    Matrix.multiplyMM(mvpMatrix, 0, projectionMatrix, 0, camera.viewMatrix, 0)

    GLES20.glDepthMask(false) // Disable depth writing

    // Bind the texture and render the SkyBox
    textureManager.bindTexture(skyBoxTextureHandle)

    // Use this MVP matrix to render the skybox
    skyBox.draw(camera)

    GLES20.glDepthMask(true) // Re-enable depth writing for other objects
    planet.render(mvpMatrix)
  }

  override fun onSurfaceChanged(unused: GL10, width: Int, height: Int) {
    // Specify the size of the rendering window
    GLES20.glViewport(0, 0, width, height)

    val ratio: Float = width.toFloat() / height.toFloat()

    camera.updateProjectionMatrix(ratio)

    // Update the view matrix
    Matrix.setLookAtM(
        camera.viewMatrix,
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
