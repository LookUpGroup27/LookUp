package com.github.lookupgroup27.lookup.model.map.renderables

import android.content.Context
import android.opengl.GLES20
import android.util.Log
import com.github.lookupgroup27.lookup.model.map.renderables.utils.GeometryUtils
import com.github.lookupgroup27.lookup.model.map.skybox.buffers.ColorBuffer
import com.github.lookupgroup27.lookup.model.map.skybox.buffers.IndexBuffer
import com.github.lookupgroup27.lookup.model.map.skybox.buffers.VertexBuffer
import com.github.lookupgroup27.lookup.util.ShaderUtils.readShader
import com.github.lookupgroup27.lookup.util.opengl.ShaderProgram

/**
 * Renderer for creating circular 2D objects in an OpenGL environment.
 *
 * @param segments Number of segments used to approximate the circle (default is DEFAULT_SEGMENTS)
 * @param radius Radius of the circle (default is 1.0f)
 * @param color Color of the circle in RGBA format (range 0-255) (default is white)
 */
open class CircleRenderer(
    private val context: Context,
    private val segments: Int = DEFAULT_SEGMENTS,
    private val radius: Float = 1.0f,
    private val color: IntArray = intArrayOf(255, 255, 255, 255)
) {
  /** Buffer for storing vertex positions. */
  private val vertexBuffer = VertexBuffer()

  /** Buffer for storing vertex colors. */
  private val colorBuffer = ColorBuffer()

  /** Buffer for storing vertex indices for triangle drawing. */
  private val indexBuffer = IndexBuffer()

  /** Shader program for rendering the object. */
  lateinit var shaderProgram: ShaderProgram

  /** Initializes buffers for circular geometry. */
  fun initializeBuffers() {
    val geometryData = GeometryUtils.generateCircularGeometry(segments)

    // Reset buffers to prepare for new data
    vertexBuffer.reset(geometryData.vertices.size / 3)
    colorBuffer.reset(geometryData.vertices.size / 3)
    indexBuffer.reset(geometryData.indices.size)

    // Add vertices to the vertex buffer
    for (i in geometryData.vertices.indices step 3) {
      vertexBuffer.addVertex(
          geometryData.vertices[i], // x-coordinate
          geometryData.vertices[i + 1], // y-coordinate
          geometryData.vertices[i + 2] // z-coordinate
          )
    }

    // Add indices to the index buffer
    for (index in geometryData.indices) indexBuffer.addIndex(index)

    // Apply a single color for all vertices
    repeat(geometryData.vertices.size / 3) {
      colorBuffer.addColor(color[3], color[0], color[1], color[2])
    }
  }

  /** Initializes shaders for rendering the circle. */
  fun initializeShaders() {
    Log.d("CircleRenderer", "Compiling shaders")
    val vertexShaderCode = readShader(context, "circle_vertex_shader.glsl")

    val fragmentShaderCode = readShader(context, "circle_fragment_shader.glsl")

    shaderProgram = ShaderProgram(vertexShaderCode, fragmentShaderCode)
  }

  /**
   * Binds shader attributes for rendering.
   *
   * @param mvpMatrix The Model-View-Projection matrix
   */
  fun bindShaderAttributes(mvpMatrix: FloatArray) {
    shaderProgram.use()

    // Pass the MVP matrix to the shader
    val mvpMatrixHandle = GLES20.glGetUniformLocation(shaderProgram.programId, "uMVPMatrix")
    GLES20.glUniformMatrix4fv(mvpMatrixHandle, 1, false, mvpMatrix, 0)

    // Bind vertex attributes
    val positionHandle = GLES20.glGetAttribLocation(shaderProgram.programId, "vPosition")
    vertexBuffer.bind(positionHandle)

    val colorHandle = GLES20.glGetAttribLocation(shaderProgram.programId, "vColor")
    colorBuffer.bind(colorHandle)
  }

  /** Unbinds the shader attributes after rendering. */
  fun unbindShaderAttributes() {
    val positionHandle = GLES20.glGetAttribLocation(shaderProgram.programId, "vPosition")
    vertexBuffer.unbind(positionHandle)
    colorBuffer.unbind()
  }

  /** Draws the circle using the currently bound buffers and shader program. */
  fun drawCircle() {
    indexBuffer.bind()
    val error = GLES20.glGetError()
    if (error != GLES20.GL_NO_ERROR) {
      Log.e("CircleRenderer", "OpenGL error during draw: $error")
    }
    indexBuffer.draw(GLES20.GL_TRIANGLES)
  }

  companion object {
    /** Default number of segments for circle approximation. */
    const val DEFAULT_SEGMENTS = 32
  }
}
