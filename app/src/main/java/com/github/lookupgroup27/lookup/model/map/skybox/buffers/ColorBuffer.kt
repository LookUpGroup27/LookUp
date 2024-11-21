package com.github.lookupgroup27.lookup.model.map.skybox.buffers

import android.opengl.GLES20
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.IntBuffer

/**
 * Manages color data for OpenGL rendering.
 *
 * This class stores color information for vertices and provides functionality to bind and use the
 * color data in rendering.
 */
class ColorBuffer {

  private var colorBuffer: IntBuffer? = null
  private var numVertices: Int = 0

  /**
   * Resets the buffer with the specified number of vertices. Allocates memory to store the color
   * data.
   *
   * @param numVertices The number of vertices whose colors will be stored.
   */
  fun reset(numVertices: Int) {
    this.numVertices = numVertices

    // Allocate memory for the color buffer
    val byteBuffer = ByteBuffer.allocateDirect(numVertices * COLORS_PER_VERTEX * Int.SIZE_BYTES)
    byteBuffer.order(ByteOrder.nativeOrder())
    colorBuffer = byteBuffer.asIntBuffer()
    colorBuffer?.position(0)
  }

  /**
   * Adds a color to the buffer in ARGB format.
   *
   * @param a Alpha component (0-255).
   * @param r Red component (0-255).
   * @param g Green component (0-255).
   * @param b Blue component (0-255).
   */
  fun addColor(a: Int, r: Int, g: Int, b: Int) {
    val color =
        ((a and 0xFF) shl 24) or ((r and 0xFF) shl 16) or ((g and 0xFF) shl 8) or (b and 0xFF)
    colorBuffer?.put(color)
  }

  /**
   * Adds a color to the buffer as an integer in ARGB format.
   *
   * @param color The color value in ARGB format.
   */
  fun addColor(color: Int) {
    colorBuffer?.put(color)
  }

  /** Binds the color buffer to the OpenGL context and enables it for rendering. */
  fun bind(attributeLocation: Int) {
    colorBuffer?.position(0)

    // Enable the color attribute and set the buffer
    GLES20.glEnableVertexAttribArray(attributeLocation)
    GLES20.glVertexAttribPointer(
        attributeLocation,
        COLORS_PER_VERTEX,
        GLES20.GL_UNSIGNED_BYTE,
        true, // Normalize the values to [0, 1]
        COLORS_PER_VERTEX * Int.SIZE_BYTES,
        colorBuffer)
  }

  /** Unbinds the color buffer, disabling the color attribute location. */
  fun unbind() {
    GLES20.glDisableVertexAttribArray(COLOR_ATTRIBUTE_LOCATION)
  }

  companion object {
    private const val COLORS_PER_VERTEX = 4 // ARGB components
    private const val COLOR_ATTRIBUTE_LOCATION = 1 // Shader's color attribute location
  }
}
