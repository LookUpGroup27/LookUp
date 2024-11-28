package com.github.lookupgroup27.lookup.model.map.renderables

import android.content.Context
import android.opengl.GLES20
import android.opengl.Matrix
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
 * @property context The Android context used for resource access.
 * @property name The name of the planet (e.g., "Earth"). Defaults to "Planet".
 * @property position The planet's position in 3D space, represented as a float array [x, y, z].
 * @property textureId The resource ID of the texture applied to the planet's surface.
 * @property numBands The number of latitude bands used for tessellating the sphere. Higher values
 *   create smoother spheres.
 * @property stepsPerBand The number of longitude steps per latitude band. Higher values improve
 *   rendering fidelity.
 */
class Planet(
    private val context: Context,
    private val name: String? = "Planet",
    private val position: FloatArray = floatArrayOf(0.0f, 0.0f, 0.0f),
    private val textureId: Int,
    numBands: Int = DEFAULT_NUM_BANDS,
    stepsPerBand: Int = DEFAULT_STEPS_PER_BAND
) : SphereRenderer(numBands, stepsPerBand) {

  private var textureHandle: Int = 0
  private var scale: Float = 0.3f

  /**
   * Updates the scale of the planet, allowing customization of its size in the rendered scene.
   *
   * @param newScale The new scale factor.
   */
  fun setScale(newScale: Float) {
    scale = newScale
  }

  /**
   * Initializes the planet's geometry, shaders, and texture. This method prepares the planet for
   * rendering.
   */
  fun initialize() {
    initializeBuffers() // Initialize sphere geometry buffers
    initializeShaders() // Initialize shaders for rendering

    // Load the planet's texture
    val textureManager = TextureManager(context)
    textureHandle = textureManager.loadTexture(textureId)
  }

  /**
   * Renders the planet using the provided Model-View-Projection (MVP) matrix.
   *
   * The planet's position and scale are applied to the MVP matrix to generate the final
   * transformation for rendering.
   *
   * @param mvpMatrix A 4x4 matrix that combines the model, view, and projection transformations.
   */
  fun render(mvpMatrix: FloatArray) {
    // Create and apply the model matrix
    val modelMatrix = FloatArray(16)
    Matrix.setIdentityM(modelMatrix, 0)

    // Apply translation based on the planet's position
    Matrix.translateM(modelMatrix, 0, position[0], position[1], position[2])

    // Apply scaling
    Matrix.scaleM(modelMatrix, 0, scale, scale, scale)

    // Combine the model matrix with the MVP matrix
    val scaledMvpMatrix = FloatArray(16)
    Matrix.multiplyMM(scaledMvpMatrix, 0, mvpMatrix, 0, modelMatrix, 0)

    // Bind shader attributes
    bindShaderAttributes(scaledMvpMatrix)

    // Bind and apply texture
    val textureUniformHandle = GLES20.glGetUniformLocation(shaderProgram.programId, "uTexture")
    GLES20.glActiveTexture(GLES20.GL_TEXTURE0)
    GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureHandle)
    GLES20.glUniform1i(textureUniformHandle, 0)

    // Render the sphere
    drawSphere()

    // Unbind shader attributes
    unbindShaderAttributes()
  }
}
