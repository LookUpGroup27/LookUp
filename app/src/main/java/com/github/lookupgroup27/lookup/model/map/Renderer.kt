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

  /**
   * Called when the surface is created or recreated. Initializes OpenGL settings, loads textures,
   * and sets up the scene objects.
   *
   * @param unused the GL10 interface, not used
   * @param config the EGLConfig of the created surface
   */
  override fun onSurfaceCreated(unused: GL10, config: EGLConfig) {
    GLES20.glClearColor(0.0f, 0.0f, 0.0f, 1.0f) // Set the background color
    GLES20.glEnable(GLES20.GL_DEPTH_TEST) // Enable depth testing

    textureManager = TextureManager(context) // Initialize texture manager
    skyBoxTextureHandle =
        textureManager.loadTexture(R.drawable.skybox_texture) // Load skybox texture
    skyBox = SkyBox() // Initialize the skybox
    intializeObjects() // Initialize other renderable objects

    // Initialize camera matrices
    Matrix.setIdentityM(camera.viewMatrix, 0)
    Matrix.setIdentityM(projectionMatrix, 0)
  }

  /**
   * Called to redraw the frame. Clears the screen, updates the camera view, and renders objects in
   * the scene.
   *
   * @param unused the GL10 interface, not used
   */
  override fun onDrawFrame(unused: GL10) {
    GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT or GLES20.GL_DEPTH_BUFFER_BIT) // Clear the screen

    Matrix.multiplyMM(
        mvpMatrix, 0, projectionMatrix, 0, camera.viewMatrix, 0) // Calculate MVP matrix

    GLES20.glDepthMask(false) // Disable depth writing for the skybox
    textureManager.bindTexture(skyBoxTextureHandle) // Bind skybox texture
    skyBox.draw(camera) // Render skybox
    GLES20.glDepthMask(true) // Re-enable depth writing

    drawObjects() // Render other objects
  }

  /**
   * Called when the surface dimensions change. Adjusts the viewport and updates the projection
   * matrix based on the new aspect ratio.
   *
   * @param unused the GL10 interface, not used
   * @param width the new width of the surface
   * @param height the new height of the surface
   */
  override fun onSurfaceChanged(unused: GL10, width: Int, height: Int) {
    GLES20.glViewport(0, 0, width, height) // Set viewport dimensions
    val ratio: Float = width.toFloat() / height.toFloat() // Calculate aspect ratio
    camera.updateProjectionMatrix(ratio) // Update camera projection matrix

    Matrix.setLookAtM(
        camera.viewMatrix,
        0,
        0f,
        0f,
        3f, // Eye position
        0f,
        0f,
        0f, // Look-at position
        0f,
        1f,
        0f // Up vector
        )
  }

  /** Initializes additional objects in the scene. Currently includes a planet. */
  private fun intializeObjects() {
    planet = Planet(context, textureId = R.drawable.planet_texture) // Create planet
    planet.initialize() // Initialize planet
  }

  /** Renders additional objects in the scene using the current MVP matrix. */
  private fun drawObjects() {
    planet.render(mvpMatrix) // Render the planet
  }
}
