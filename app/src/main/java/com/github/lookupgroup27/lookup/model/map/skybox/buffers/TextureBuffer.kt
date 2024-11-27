package com.github.lookupgroup27.lookup.model.map.skybox.buffers

import android.opengl.GLES20
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer

/**
 * Manages texture coordinates for OpenGL rendering.
 *
 * This class stores UV coordinates for vertices and provides functionality to bind and use them in
 * shaders.
 */
class TextureBuffer {

  private var textureBuffer: FloatBuffer? = null
  private var numVertices: Int = 0

  /**
   * Resets the buffer with the specified number of vertices. Allocates memory to store the texture
   * coordinates.
   *
   * @param numVertices The number of vertices whose texture coordinates will be stored.
   */
  fun reset(numVertices: Int) {
    this.numVertices = numVertices

    // Allocate memory for the texture buffer
    val byteBuffer = ByteBuffer.allocateDirect(numVertices * COORDS_PER_VERTEX * Float.SIZE_BYTES)
    byteBuffer.order(ByteOrder.nativeOrder())
    textureBuffer = byteBuffer.asFloatBuffer()
    textureBuffer?.position(0)
  }

  /**
   * Adds texture coordinates (U, V) to the buffer.
   *
   * @param u The U coordinate (horizontal).
   * @param v The V coordinate (vertical).
   */
  fun addTexCoord(u: Float, v: Float) {
    textureBuffer?.put(u)
    textureBuffer?.put(v)
  }

  /** Binds the texture buffer to the OpenGL context and enables it for rendering. */
  fun bind(attributeLocation: Int) {
    textureBuffer?.position(0)

    // Enable the texture attribute and set the buffer
    GLES20.glEnableVertexAttribArray(attributeLocation)
    GLES20.glVertexAttribPointer(
        attributeLocation,
        COORDS_PER_VERTEX,
        GLES20.GL_FLOAT,
        false,
        COORDS_PER_VERTEX * Float.SIZE_BYTES,
        textureBuffer)
  }

  /** Unbinds the texture buffer, disabling the texture coordinate attribute location. */
  fun unbind() {
    GLES20.glDisableVertexAttribArray(TEXTURE_COORD_ATTRIBUTE_LOCATION)
  }

  companion object {
    private const val COORDS_PER_VERTEX = 2 // U and V coordinates
    private const val TEXTURE_COORD_ATTRIBUTE_LOCATION =
        2 // Shader's texture coordinate attribute location
  }
}
