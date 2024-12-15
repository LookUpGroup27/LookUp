package com.github.lookupgroup27.lookup.model.map

import android.content.Context
import android.opengl.GLES20
import android.opengl.GLSurfaceView
import android.util.Log
import com.github.lookupgroup27.lookup.R
import com.github.lookupgroup27.lookup.model.loader.StarsLoader
import com.github.lookupgroup27.lookup.model.map.renderables.Moon
import com.github.lookupgroup27.lookup.model.map.renderables.Object
import com.github.lookupgroup27.lookup.model.map.renderables.Planet
import com.github.lookupgroup27.lookup.model.map.renderables.utils.RayUtils.calculateRay
import com.github.lookupgroup27.lookup.model.map.skybox.SkyBox
import com.github.lookupgroup27.lookup.model.stars.StarDataRepository
import com.github.lookupgroup27.lookup.util.opengl.TextureManager
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

/**
 * Provides the OpenGL rendering logic for the GLSurfaceView. This class is responsible for drawing
 * the shapes on the screen. It is called by the GLSurfaceView when it is time to redraw the screen.
 */
class MapRenderer(fov: Float) : GLSurfaceView.Renderer {

  private lateinit var skyBox: SkyBox
  private lateinit var planets: List<Planet>
  private lateinit var planet: Planet
  private lateinit var moon: Moon

  private lateinit var textureManager: TextureManager
  private lateinit var starsLoader: StarsLoader

  private var skyBoxTextureHandle: Int = -1 // Handle for the skybox texture

  private lateinit var context: Context
  private val renderableObjects = mutableListOf<Object>() // List of objects to render
  private val starDataRepository = StarDataRepository() // Repository for star data

  private val viewport = IntArray(4)

  /** The camera used to draw the shapes on the screen. */
  val camera = Camera(fov)

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

    // Cache the viewport dimensions
    viewport[2] = width
    viewport[3] = height

    val ratio: Float = width.toFloat() / height.toFloat() // Calculate aspect ratio
    camera.updateScreenRatio(ratio) // Update camera projection matrix
  }

  /** Initialize the objects in the scene. */
  private fun initializeObjects() {
    // Stars
    starsLoader = StarsLoader(starDataRepository)
    val stars = starsLoader.loadStars(context, "hyg_stars.csv")
    if (stars.isEmpty()) Log.d("MapRenderer", "No stars loaded for rendering.")

    planets =
        listOf(
            Planet(
                context,
                "Mercury",
                floatArrayOf(0.0f, 0.0f, -3.0f),
                R.drawable.mercury_texture,
                scale = 0.2f),
            Planet(
                context,
                "Venus",
                floatArrayOf(1.0f, 0.0f, -5.0f),
                R.drawable.venus_texture,
                scale = 0.3f),
            Planet(
                context,
                "Earth",
                floatArrayOf(2.0f, 0.0f, -7.0f),
                R.drawable.earth_texture,
                scale = 0.4f),
            Planet(
                context,
                "Mars",
                floatArrayOf(3.0f, 0.0f, -10.0f),
                R.drawable.mars_texture,
                scale = 0.35f),
            Planet(
                context,
                "Jupiter",
                floatArrayOf(-2.0f, 2.0f, -15.0f),
                R.drawable.jupiter_texture,
                scale = 0.8f),
            Planet(
                context,
                "Saturn",
                floatArrayOf(-3.0f, -1.0f, -20.0f),
                R.drawable.saturn_texture,
                scale = 0.7f),
            Planet(
                context,
                "Uranus",
                floatArrayOf(4.0f, 3.0f, -25.0f),
                R.drawable.uranus_texture,
                scale = 0.5f),
            Planet(
                context,
                "Neptune",
                floatArrayOf(-4.0f, 1.5f, -30.0f),
                R.drawable.neptune_texture,
                scale = 0.5f))

    // Add planets to the renderable objects list
    renderableObjects.addAll(planets)
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

  /**
   * Checks if a ray intersects any planet and returns the name of the intersected planet, if any.
   *
   * @param screenX The x-coordinate of the touch event on the screen.
   * @param screenY The y-coordinate of the touch event on the screen.
   * @return The name of the intersected planet, or null if no intersection occurred.
   */
  fun getIntersectedPlanetName(screenX: Float, screenY: Float): String? {
    if (viewport[2] == 0 || viewport[3] == 0) {
      Log.d("Viewport dimensions", "Viewport dimensions are invalid: ${viewport.joinToString()}")
      return null
    }

    val ray = calculateRay(screenX, screenY, camera, viewport)
    for (planet in planets) {
      if (planet.checkHit(ray.origin, ray.direction)) {
        return planet.name
      }
    }
    return null
  }
}
