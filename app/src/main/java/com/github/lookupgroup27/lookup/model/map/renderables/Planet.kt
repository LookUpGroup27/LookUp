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
 * @property scale The scaling factor applied to the planet's geometry. Defaults to 0.3.
 */
open class Planet(
    private val context: Context,
    val name: String = "Planet",
    val position: FloatArray = floatArrayOf(0.0f, 0.0f, -2.0f),
    var textureId: Int,
    numBands: Int = SphereRenderer.DEFAULT_NUM_BANDS,
    stepsPerBand: Int = SphereRenderer.DEFAULT_STEPS_PER_BAND,
    private val scale: Float = 0.02f,
    private val rotationSpeed: Float = 50f
) : Object() {

  private val sphereRenderer = SphereRenderer(context, numBands, stepsPerBand)

  // Make textureHandle protected so it can be accessed by subclasses
  protected var textureHandle: Int = 0

  private var textureManager: TextureManager
  private val label =
      Label(context, name, Position(position[0], position[1], position[2]), 0.1f, scale)

  // New properties for rotation
  private var rotationAngle: Float = 0f // Current rotation angle in degrees

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
   * Updates the rotation of the planet. This method should be called once per frame, and the
   * deltaTime parameter should be passed to calculate the incremental rotation based on the speed.
   *
   * @param deltaTime Time elapsed since the last frame, in seconds.
   */
  fun updateRotation(deltaTime: Float) {
    rotationAngle = (rotationAngle + rotationSpeed * deltaTime) % 360f
  }

  /**
   * Renders the planet using the provided camera.
   *
   * @param camera The camera used for rendering the scene.
   */
  fun draw(camera: Camera, transformMatrix: FloatArray? = null) {
    label.draw(camera)
    val modelMatrix = FloatArray(16)
    val billboardMatrix = FloatArray(16)
    val mvpMatrix = FloatArray(16)
    Matrix.setIdentityM(modelMatrix, 0)
    Matrix.setIdentityM(billboardMatrix, 0)

    // Apply the provided transform matrix or use the default planet position
    if (transformMatrix != null) {
      System.arraycopy(transformMatrix, 0, modelMatrix, 0, 16)
    } else {
      Matrix.translateM(modelMatrix, 0, position[0], position[1], position[2])
      Matrix.scaleM(modelMatrix, 0, scale, scale, scale)
    }

    // Extract camera look direction from view matrix
    val lookX = -camera.viewMatrix[2] // Third column of view matrix
    val lookY = -camera.viewMatrix[6] // is the look direction
    val lookZ = -camera.viewMatrix[10]

    // Create billboard rotation
    val upX = camera.viewMatrix[1] // Second column is up vector
    val upY = camera.viewMatrix[5]
    val upZ = camera.viewMatrix[9]

    // Calculate right vector (cross product)
    val rightX = upY * lookZ - upZ * lookY
    val rightY = upZ * lookX - upX * lookZ
    val rightZ = upX * lookY - upY * lookX

    // Set billboard matrix
    billboardMatrix[0] = rightX
    billboardMatrix[1] = rightY
    billboardMatrix[2] = rightZ
    billboardMatrix[3] = 0f

    billboardMatrix[4] = upX
    billboardMatrix[5] = upY
    billboardMatrix[6] = upZ
    billboardMatrix[7] = 0f

    billboardMatrix[8] = lookX
    billboardMatrix[9] = lookY
    billboardMatrix[10] = lookZ
    billboardMatrix[11] = 0f

    billboardMatrix[12] = 0f
    billboardMatrix[13] = 0f
    billboardMatrix[14] = 0f
    billboardMatrix[15] = 1f

    // Combine matrices: Projection * View * Model

    // Apply rotation transformation
    val rotatedMatrix = FloatArray(16)
    Matrix.rotateM(billboardMatrix, 0, rotationAngle, 0f, 1f, 0f)
    Matrix.multiplyMM(rotatedMatrix, 0, modelMatrix, 0, billboardMatrix, 0)

    // Combine model, view, and projection matrices in correct order
    val viewModelMatrix = FloatArray(16)
    Matrix.multiplyMM(viewModelMatrix, 0, camera.viewMatrix, 0, rotatedMatrix, 0)
    Matrix.multiplyMM(mvpMatrix, 0, camera.projMatrix, 0, viewModelMatrix, 0)

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

  fun updateTexture(newTextureId: Int) {
    textureId = newTextureId
    loadTexture() // Reload the texture using the new ID
  }

  /**
   * Checks if a ray intersects the planet's surface.
   *
   * This method calculates the intersection of a ray with the planet's bounding sphere. If the ray
   * intersects the sphere, the method returns true; otherwise, it returns false.
   *
   * @param rayOrigin The origin of the ray in 3D space.
   * @param rayDirection The direction of the ray in 3D space.
   * @return True if the ray intersects the planet's bounding sphere, false otherwise.
   */
  fun checkHit(rayOrigin: FloatArray, rayDirection: FloatArray): Boolean {

    // Calculate the distance from the ray to the sphere's center
    val sphereCenter = position
    val radius = scale // Assume scale is the radius of the sphere

    // Calculate the vector from the ray origin to the sphere's center
    val oc =
        floatArrayOf(
            rayOrigin[0] - sphereCenter[0],
            rayOrigin[1] - sphereCenter[1],
            rayOrigin[2] - sphereCenter[2])

    // Calculate coefficients for the quadratic equation
    val a = dot(rayDirection, rayDirection)
    val b = 2.0f * dot(oc, rayDirection)
    val c = dot(oc, oc) - radius * radius

    val discriminant = b * b - 4 * a * c
    return discriminant >= 0 // If the discriminant is non-negative, there is an intersection
  }

  private fun dot(a: FloatArray, b: FloatArray): Float {
    return a[0] * b[0] + a[1] * b[1] + a[2] * b[2]
  }
}
