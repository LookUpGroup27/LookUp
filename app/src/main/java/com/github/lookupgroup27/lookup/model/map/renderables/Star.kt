package com.github.lookupgroup27.lookup.model.map.renderables

import android.content.Context
import android.opengl.Matrix
import com.github.lookupgroup27.lookup.model.map.Camera

/**
 * Represents a star in 3D space using the CircleRenderer for rendering.
 *
 * @param context The Android context for resource management
 * @param position The x, y, z coordinates of the star
 * @param color The color of the star, specified as an RGBA float array
 * @param size The size of the star (scale factor for its radius)
 * @param segments The number of segments used to approximate the circle (default is 32)
 */
class Star(
    val context: Context,
    val position: FloatArray,
    val color: FloatArray,
    val size: Float = 0.3f,
    val segments: Int = CircleRenderer.DEFAULT_SEGMENTS,
    val vertexShaderCode: String,
    val fragmentShaderCode: String
) : Object(vertexShaderCode, fragmentShaderCode) {

  private val circleRenderer = CircleRenderer(segments)

  init {
    // Initialize shaders and buffers once, no textures needed
    circleRenderer.initializeShaders()
    circleRenderer.initializeBuffers()
  }

  override fun draw(camera: Camera) {
    // Model-View-Projection (MVP) Matrix
    val mvpMatrix = FloatArray(16)
    val modelMatrix = FloatArray(16)
    val viewMatrix = FloatArray(16)
    val projMatrix = FloatArray(16)

    // Copy camera matrices to avoid modification
    System.arraycopy(camera.viewMatrix, 0, viewMatrix, 0, 16)
    System.arraycopy(camera.projMatrix, 0, projMatrix, 0, 16)

    Matrix.setIdentityM(modelMatrix, 0)
    Matrix.translateM(modelMatrix, 0, position[0], position[1], position[2])
    Matrix.scaleM(modelMatrix, 0, size, size, size)

    // Multiply matrices in correct order: Projection * View * Model
    Matrix.multiplyMM(mvpMatrix, 0, viewMatrix, 0, modelMatrix, 0)
    Matrix.multiplyMM(mvpMatrix, 0, projMatrix, 0, mvpMatrix, 0)

    // Bind shader attributes and draw the circle
    circleRenderer.bindShaderAttributes(mvpMatrix)
    circleRenderer.drawCircle()
    circleRenderer.unbindShaderAttributes()
  }
}
