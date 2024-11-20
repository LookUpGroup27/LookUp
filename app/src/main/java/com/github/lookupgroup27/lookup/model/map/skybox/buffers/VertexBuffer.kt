package com.github.lookupgroup27.lookup.model.map.skybox.buffers

import android.opengl.GLES20
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer

/**
 * Manages vertex data for OpenGL rendering.
 *
 * This class provides functionality to store and bind vertex positions
 * for efficient use in the rendering pipeline.
 */
class VertexBuffer {

    private var vertexBuffer: FloatBuffer? = null
    private var numVertices: Int = 0

    /**
     * Resets the buffer with the specified number of vertices.
     * Allocates memory to store the vertex data.
     *
     * @param numVertices The number of vertices to store.
     */
    fun reset(numVertices: Int) {
        this.numVertices = numVertices

        // Allocate memory for the vertex buffer
        val byteBuffer = ByteBuffer.allocateDirect(numVertices * FLOATS_PER_VERTEX * Float.SIZE_BYTES)
        byteBuffer.order(ByteOrder.nativeOrder())
        vertexBuffer = byteBuffer.asFloatBuffer()
        vertexBuffer?.position(0)
    }

    /**
     * Adds a vertex to the buffer with specified x, y, and z coordinates.
     *
     * @param x X-coordinate of the vertex.
     * @param y Y-coordinate of the vertex.
     * @param z Z-coordinate of the vertex.
     */
    fun addVertex(x: Float, y: Float, z: Float) {
        vertexBuffer?.put(x)
        vertexBuffer?.put(y)
        vertexBuffer?.put(z)
    }

    /**
     * Binds the vertex buffer to the OpenGL context and enables it for rendering.
     *
     * @param attributeLocation The attribute location in the shader program.
     */
    fun bind(attributeLocation: Int) {
        vertexBuffer?.position(0)

        // Enable the vertex attribute and set the buffer
        GLES20.glEnableVertexAttribArray(attributeLocation)
        GLES20.glVertexAttribPointer(
            attributeLocation,
            FLOATS_PER_VERTEX,
            GLES20.GL_FLOAT,
            false,
            FLOATS_PER_VERTEX * Float.SIZE_BYTES,
            vertexBuffer
        )
    }

    /**
     * Unbinds the vertex buffer, disabling the attribute location.
     *
     * @param attributeLocation The attribute location in the shader program.
     */
    fun unbind(attributeLocation: Int) {
        GLES20.glDisableVertexAttribArray(attributeLocation)
    }

    companion object {
        private const val FLOATS_PER_VERTEX = 3 // x, y, z
    }
}
