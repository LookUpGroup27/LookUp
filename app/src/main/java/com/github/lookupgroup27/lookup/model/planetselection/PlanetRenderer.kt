package com.github.lookupgroup27.lookup.model.planetselection

import android.content.Context
import android.opengl.GLES20
import android.opengl.GLSurfaceView
import android.opengl.Matrix
import android.util.Log
import com.github.lookupgroup27.lookup.model.map.Camera
import com.github.lookupgroup27.lookup.model.map.planets.PlanetData
import com.github.lookupgroup27.lookup.model.map.renderables.Planet
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

/**
 * Handles the rendering of a rotating planet using OpenGL.
 *
 * @param context The application context.
 * @param planetData The planet data to render.
 */
class PlanetRenderer(private val context: Context, private var planetData: PlanetData) :
    GLSurfaceView.Renderer {

  private lateinit var planet: Planet

  val camera = Camera(fov = 100f) // Field of View can be adjusted

  private var pendingTextureId: Int? = null

  private var lastFrameTime: Long = System.currentTimeMillis()

  override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {
    try {
      GLES20.glClearColor(0f, 0f, 0f, 1f)
      GLES20.glEnable(GLES20.GL_DEPTH_TEST)
      initializePlanet()
    } catch (e: Exception) {
      Log.e("PlanetRenderer", "Error in onSurfaceCreated", e)
      // Optionally, you could reset to a default planet or show an error state
    }
  }

  override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {
    if (width == 0 || height == 0) {
      Log.e("PlanetRenderer", "Invalid viewport dimensions: $width x $height")
    }
    GLES20.glViewport(0, 0, width, height)
    camera.updateScreenRatio(width.toFloat() / height.toFloat()) // Update aspect ratio
  }

  override fun onDrawFrame(gl: GL10?) {
    // Clear the screen
    GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT or GLES20.GL_DEPTH_BUFFER_BIT)

    // Create a transformation matrix for the selection screen
    val selectionMatrix = FloatArray(16)
    Matrix.setIdentityM(selectionMatrix, 0) // Start with identity matrix
    Matrix.translateM(selectionMatrix, 0, 0f, -1.0f, -3.5f) // Center at bottom, move back on Z
    Matrix.scaleM(selectionMatrix, 0, 1.5f, 1.5f, 1.5f) // Uniform scale for visibility

    // Handle texture update on the GL thread
    pendingTextureId?.let {
      planet.updateTexture(it) // Update texture safely on the GL thread
      pendingTextureId = null // Reset the flag
    }

    // Calculate delta time
    val currentTime = System.currentTimeMillis()
    val deltaTime = (currentTime - lastFrameTime) / 1000f
    lastFrameTime = currentTime

    // Update rotation and draw the planet
    planet.updateRotation(deltaTime)

    // Apply the custom matrix to override planet position
    planet.draw(camera, selectionMatrix)
  }

  /**
   * Updates the planet data and reinitializes the planet.
   *
   * @param newPlanetData The new planet data to render.
   */
  fun updatePlanet(newPlanetData: PlanetData) {
    try {
      this.planetData = newPlanetData
      initializePlanet()
    } catch (e: Exception) {
      Log.e("PlanetRenderer", "Failed to update planet", e)
    }
  }

  private fun initializePlanet() {
    Log.d("PlanetRenderer", "Initializing Planet: ${planetData.name}")
    planet =
        Planet(
            context = context,
            name = planetData.name,
            position = floatArrayOf(0f, 0f, -2.5f),
            textureId = planetData.textureId)
    Log.d("PlanetRenderer", "Planet Initialized with Texture ID: ${planetData.textureId}")
  }
}
