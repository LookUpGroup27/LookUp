package com.github.lookupgroup27.lookup.model.map

import android.content.Context
import android.opengl.GLES20
import android.opengl.GLSurfaceView
import com.github.lookupgroup27.lookup.R
import com.github.lookupgroup27.lookup.model.loader.StarsLoader
import com.github.lookupgroup27.lookup.model.map.renderables.Moon
import com.github.lookupgroup27.lookup.model.map.renderables.Planet
import com.github.lookupgroup27.lookup.model.map.renderables.Star
import com.github.lookupgroup27.lookup.model.map.skybox.SkyBox
import com.github.lookupgroup27.lookup.model.stars.StarDataRepository
import com.github.lookupgroup27.lookup.util.opengl.TextureManager
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

/**
 * Provides the OpenGL rendering logic for the GLSurfaceView. This class is responsible for drawing
 * the shapes on the screen. It is called by the GLSurfaceView when it is time to redraw the screen.
 */
class MapRenderer : GLSurfaceView.Renderer {

  private lateinit var skyBox: SkyBox
  private lateinit var planet: Planet
  private lateinit var moon: Moon

  private lateinit var textureManager: TextureManager
  private lateinit var starsLoader: StarsLoader

  private var skyBoxTextureHandle: Int = -1 // Handle for the skybox texture

  private lateinit var context: Context
  private val renderableObjects = mutableListOf<Star>() // List of stars to render
  private val starDataRepository = StarDataRepository() // Repository for star data

  /** The camera used to draw the shapes on the screen. */
  val camera = Camera()

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

    // Initialize TextureManager
    textureManager = TextureManager(context)

    // Initialize the SkyBox
    skyBoxTextureHandle = textureManager.loadTexture(R.drawable.skybox_texture)
    skyBox = SkyBox(context)

    // Initialize the objects in the scene
    initializeObjects()
  }

  /**
   * Called to redraw the frame. Clears the screen, updates the camera view, and renders objects in
   * the scene.
   *
   * @param unused the GL10 interface, not used
   */
  override fun onDrawFrame(unused: GL10) {

    // Clear the screen
    GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT or GLES20.GL_DEPTH_BUFFER_BIT) // Clear the screen

    // Bind the texture and render the SkyBox
    GLES20.glDepthMask(false)
    textureManager.bindTexture(skyBoxTextureHandle)
    skyBox.draw(camera)
    GLES20.glDepthMask(true)

    // Draw the objects in the scene
    drawObjects()
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
  }

  /** Initialize the objects in the scene. */
  private fun initializeObjects() {
    // Stars
    starsLoader = StarsLoader(starDataRepository)
    val stars = starsLoader.loadStars(context, "hyg_stars.csv")
    if (stars.isEmpty()) {
      println("Warning: No stars loaded for rendering.")
    }
    renderableObjects.addAll(stars)

    // Planet
    planet = Planet(context, textureId = R.drawable.planet_texture) // Create planet
    // Moon
    moon = Moon(context) // Create moon
  }

  /** Draws the objects in the scene. */
  private fun drawObjects() {
    // Renderable Objects
    renderableObjects.forEach { o -> o.draw(camera) }

    // Planet
    // planet.draw(camera)
    // Moon
    moon.draw(camera)
  }

  /** Updates the context used by the renderer. */
  fun updateContext(context: Context) {
    this.context = context
  }
}
