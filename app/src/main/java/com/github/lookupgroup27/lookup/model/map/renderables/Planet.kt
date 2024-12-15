package com.github.lookupgroup27.lookup.model.map.renderables

import android.content.Context
import android.opengl.GLES20
import android.opengl.Matrix
import com.github.lookupgroup27.lookup.model.map.Camera
import com.github.lookupgroup27.lookup.ui.map.renderables.Label
import com.github.lookupgroup27.lookup.util.opengl.Position
import com.github.lookupgroup27.lookup.util.opengl.TextureManager

/**
 * Represents a 3D planet rendered in an OpenGL environment. This class handles the geometry,
 * textures, and shaders required to render a spherical planet with customizable appearance and
 * position.
 *
 * Features include:
 * - Geometry generation using sphere tessellation.
 * - Support for custom textures, colors, and scaling.
 * - Integration with OpenGL shaders for rendering.
 *
 * @param numBands The number of latitude bands used for tessellating the sphere. Higher values
 *   create smoother spheres.
 * @param stepsPerBand The number of longitude steps per latitude band. Higher values improve
 *   rendering fidelity.
 * @property context The Android context used for resource access.
 * @property name The name of the planet (e.g., "Earth"). Defaults to "Planet".
 * @property position The planet's position in 3D space, represented as a float array [x, y, z].
 * @property textureId The resource ID of the texture applied to the planet's surface.
 */
open class Planet(
    private val context: Context,
    private val name: String = "Planet",
    private val position: FloatArray = floatArrayOf(0.0f, 0.0f, -2.0f),
    protected var textureId: Int,
    numBands: Int = SphereRenderer.DEFAULT_NUM_BANDS,
    stepsPerBand: Int = SphereRenderer.DEFAULT_STEPS_PER_BAND,
    private val vertexShaderCode: String = "",
    private val fragmentShaderCode: String = ""
) : Object(vertexShaderCode, fragmentShaderCode) {

  private val sphereRenderer = SphereRenderer(context, numBands, stepsPerBand)
  // Make textureHandle protected so it can be accessed by subclasses
  protected var textureHandle: Int = 0

  private var scale: Float = 0.3f

  private var textureManager: TextureManager
  private val label =
      Label(context, name, Position(position[0], position[1], position[2]), 0.1f, scale)

  /** Initializes the planet's geometry, shaders, and texture. */
  init {
    sphereRenderer.initializeBuffers()
    sphereRenderer.initializeShaders()

    // Initialize TextureManager and load initial texture
    textureManager = TextureManager(context)
    loadTexture()
  }

  /** Loads or reloads the texture for the planet. */
  protected fun loadTexture() {
    // Release the existing texture if it exists
    if (textureHandle != 0) {
      val textureHandleArray = intArrayOf(textureHandle)
      GLES20.glDeleteTextures(1, textureHandleArray, 0)
    }

    // Load new texture
    textureHandle = textureManager.loadTexture(textureId)
  }

  /**
   * Updates the scale of the planet, allowing customization of its size in the rendered scene.
   *
   * @param newScale The new scale factor.
   */
  fun setScale(newScale: Float) {
    scale = newScale
  }

  /**
   * Renders the planet using the provided camera.
   *
   * @param camera The camera used for rendering the scene.
   */
  override fun draw(camera: Camera) {
    label.draw(camera)
    val modelMatrix = FloatArray(16)
    Matrix.setIdentityM(modelMatrix, 0)
    val viewMatrix = FloatArray(16)
    val projMatrix = FloatArray(16)

    // Copy camera matrices to avoid modification
    System.arraycopy(camera.viewMatrix, 0, viewMatrix, 0, 16)
    System.arraycopy(camera.projMatrix, 0, projMatrix, 0, 16)

    // Apply object transformations
    Matrix.translateM(modelMatrix, 0, position[0], position[1], position[2])
    Matrix.scaleM(modelMatrix, 0, scale, scale, scale)

    // Combine model, view, and projection matrices in correct order
    val viewModelMatrix = FloatArray(16)
    Matrix.multiplyMM(viewModelMatrix, 0, viewMatrix, 0, modelMatrix, 0)

    val mvpMatrix = FloatArray(16)
    Matrix.multiplyMM(mvpMatrix, 0, projMatrix, 0, viewModelMatrix, 0)

    // Pass final MVP matrix to the renderer
    sphereRenderer.bindShaderAttributes(mvpMatrix)

    // Bind and apply texture
    val textureUniformHandle =
        GLES20.glGetUniformLocation(sphereRenderer.shaderProgram.programId, "uTexture")
    GLES20.glActiveTexture(GLES20.GL_TEXTURE0)
    GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureHandle)
    GLES20.glUniform1i(textureUniformHandle, 0)

    // Render the sphere
    sphereRenderer.drawSphere()
    sphereRenderer.unbindShaderAttributes()
  }
}
