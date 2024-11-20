package com.github.lookupgroup27.lookup.model.map.skybox.buffers

import android.opengl.GLES20
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.ShortBuffer

/**
 * Manages index data for OpenGL rendering.
 *
 * This class stores indices that define the order of rendering vertices and provides functionality
 * to bind and draw the indices.
 */
class IndexBuffer {

  private var indexBuffer: ShortBuffer? = null
  private var numIndices: Int = 0

  /**
   * Resets the buffer with the specified number of indices. Allocates memory to store the index
   * data.
   *
   * @param numIndices The number of indices to store.
   */
  fun reset(numIndices: Int) {
    this.numIndices = numIndices

    // Allocate memory for the index buffer
    val byteBuffer = ByteBuffer.allocateDirect(numIndices * Short.SIZE_BYTES)
    byteBuffer.order(ByteOrder.nativeOrder())
    indexBuffer = byteBuffer.asShortBuffer()
    indexBuffer?.position(0)
  }

  /**
   * Adds an index to the buffer.
   *
   * @param index The index to add.
   */
  fun addIndex(index: Short) {
    indexBuffer?.put(index)
  }

  /** Binds the index buffer to the OpenGL context and enables it for rendering. */
  fun bind() {
    indexBuffer?.position(0)
  }

  /**
   * Draws the elements using the specified OpenGL primitive type.
   *
   * @param primitiveType The type of primitive to render (e.g., `GLES20.GL_TRIANGLES`).
   */
  fun draw(primitiveType: Int) {
    indexBuffer?.let {
      GLES20.glDrawElements(primitiveType, numIndices, GLES20.GL_UNSIGNED_SHORT, it)
    }
  }

  /** Clears the buffer and releases resources. */
  fun clear() {
    indexBuffer?.clear()
    indexBuffer = null
  }
}
