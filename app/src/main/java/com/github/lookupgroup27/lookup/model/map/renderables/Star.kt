package com.github.lookupgroup27.lookup.model.map.renderables

import android.content.Context
import android.opengl.Matrix
import com.github.lookupgroup27.lookup.model.map.Camera

/**
 * Represents a star in 3D space using the CircleRenderer for rendering.
 *
 * @param context The Android context for resource management
 * @param position The x, y, z coordinates of the star
 * @param color The color of the star, specified as an RGBA array (range 0-255) (default is white)
 * @param size The size of the star (scale factor for its radius)
 * @param segments The number of segments used to approximate the circle (default is 32)
 */
class Star(
    val context: Context,
    val position: FloatArray,
    val color: IntArray = intArrayOf(255, 255, 255, 255),
    val size: Float = 0.5f,
    val segments: Int = CircleRenderer.DEFAULT_SEGMENTS
) : Object() {

  private val circleRenderer = CircleRenderer(context, segments, 1.0f, color.copyOf(4))

  init {
    // Initialize shaders and buffers once, no textures needed
    circleRenderer.initializeShaders()
    circleRenderer.initializeBuffers()
  }

  fun draw(camera: Camera) {
    // Model-View-Projection (MVP) Matrix
    val mvpMatrix = FloatArray(16)
    val modelMatrix = FloatArray(16)
    val billboardMatrix = FloatArray(16)

    // Reset matrices
    Matrix.setIdentityM(modelMatrix, 0)
    Matrix.setIdentityM(billboardMatrix, 0)

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

    // First translate to position
    Matrix.translateM(modelMatrix, 0, position[0], position[1], position[2])

    // Then apply billboard rotation
    val rotatedMatrix = FloatArray(16)
    Matrix.multiplyMM(rotatedMatrix, 0, modelMatrix, 0, billboardMatrix, 0)

    // Finally apply scale
    Matrix.scaleM(rotatedMatrix, 0, size, size, size)

    // Compute final MVP matrix
    val viewModelMatrix = FloatArray(16)
    Matrix.multiplyMM(viewModelMatrix, 0, camera.viewMatrix, 0, rotatedMatrix, 0)
    Matrix.multiplyMM(mvpMatrix, 0, camera.projMatrix, 0, viewModelMatrix, 0)

    // Bind shader attributes and draw the circle
    circleRenderer.bindShaderAttributes(mvpMatrix)
    circleRenderer.drawCircle()
    circleRenderer.unbindShaderAttributes()
  }
}
