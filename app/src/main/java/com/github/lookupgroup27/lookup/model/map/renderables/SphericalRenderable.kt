package com.github.lookupgroup27.lookup.model.map.renderables

import android.content.Context
import android.opengl.GLES20
import com.github.lookupgroup27.lookup.model.map.renderables.utils.GeometryUtils.generateSphericalGeometry
import com.github.lookupgroup27.lookup.model.map.skybox.buffers.ColorBuffer
import com.github.lookupgroup27.lookup.model.map.skybox.buffers.IndexBuffer
import com.github.lookupgroup27.lookup.model.map.skybox.buffers.TextureBuffer
import com.github.lookupgroup27.lookup.model.map.skybox.buffers.VertexBuffer
import com.github.lookupgroup27.lookup.util.opengl.ShaderProgram
import com.github.lookupgroup27.lookup.util.opengl.ShaderUtils.readShader

/**
 * Base class for rendering spherical 3D objects in an OpenGL environment.
 *
 * This abstract class provides functionality for managing vertex buffers, shaders, and rendering
 * logic for objects with spherical geometry. It reduces code duplication for derived classes such
 * as planets and skyboxes.
 *
 * @param numBands The number of latitude bands used for tessellating the sphere. Higher values
 *   create smoother geometry but increase computational cost (default is 20).
 * @param stepsPerBand The number of longitude steps per latitude band. Higher values improve
 *   rendering fidelity but also increase computational cost (default is 28).
 */
open class SphereRenderer(
    private val context: Context,
    private val numBands: Int = DEFAULT_NUM_BANDS,
    private val stepsPerBand: Int = DEFAULT_STEPS_PER_BAND
) {
  /** Buffer for storing vertex positions. */
  private val vertexBuffer = VertexBuffer()

  /** Buffer for storing vertex colors. */
  private val colorBuffer = ColorBuffer()

  /** Buffer for storing vertex indices for triangle drawing. */
  private val indexBuffer = IndexBuffer()

  /** Buffer for storing texture coordinates. */
  private val textureBuffer = TextureBuffer()

  /** Shader program for rendering the object. */
  lateinit var shaderProgram: ShaderProgram

  /**
   * Initializes buffers for spherical geometry, including vertices, colors, and texture
   * coordinates.
   *
   * This method generates the geometry data for a sphere using the specified `numBands` and
   * `stepsPerBand` values and populates the corresponding buffers.
   */
  fun initializeBuffers() {
    val geometryData = generateSphericalGeometry(numBands, stepsPerBand)
    val numVertices = numBands * stepsPerBand

    // Reset buffers
    vertexBuffer.reset(numVertices)
    colorBuffer.reset(numVertices)
    indexBuffer.reset(geometryData.indices.size)
    textureBuffer.reset(numVertices)

    // Populate vertex buffer
    for (i in 0 until geometryData.vertices.size step 3) {
      vertexBuffer.addVertex(
          geometryData.vertices[i], geometryData.vertices[i + 1], geometryData.vertices[i + 2])
    }

    // Populate texture buffer
    for (i in 0 until geometryData.textureCoords.size step 2) {
      textureBuffer.addTexCoord(geometryData.textureCoords[i], geometryData.textureCoords[i + 1])
    }

    // Populate index buffer
    geometryData.indices.forEach { indexBuffer.addIndex(it) }
  }

  /**
   * Initializes shaders for rendering the object.
   *
   * This method creates and compiles vertex and fragment shaders with predefined code and links
   * them into a shader program for rendering.
   */
  fun initializeShaders() {
    val vertexShaderCode = readShader(context, "sphere_vertex_shader.glsl")

    val fragmentShaderCode = readShader(context, "sphere_fragment_shader.glsl")

    shaderProgram = ShaderProgram(vertexShaderCode, fragmentShaderCode)
  }

  /**
   * Binds the shader attributes, including vertex positions, colors, and texture coordinates.
   *
   * @param mvpMatrix The Model-View-Projection matrix to be passed to the shader for
   *   transformations.
   */
  fun bindShaderAttributes(mvpMatrix: FloatArray) {
    shaderProgram.use()

    // Get uniform locations
    val mvpMatrixHandle = GLES20.glGetUniformLocation(shaderProgram.programId, "uMVPMatrix")

    // Pass the MVP matrix
    GLES20.glUniformMatrix4fv(mvpMatrixHandle, 1, false, mvpMatrix, 0)

    // Enable vertex attributes
    val positionHandle = GLES20.glGetAttribLocation(shaderProgram.programId, "vPosition")
    GLES20.glEnableVertexAttribArray(positionHandle)
    vertexBuffer.bind(positionHandle)

    val colorHandle = GLES20.glGetAttribLocation(shaderProgram.programId, "vColor")
    GLES20.glEnableVertexAttribArray(colorHandle)
    colorBuffer.bind(colorHandle)

    val texCoordHandle = GLES20.glGetAttribLocation(shaderProgram.programId, "vTexCoord")
    GLES20.glEnableVertexAttribArray(texCoordHandle)
    textureBuffer.bind(texCoordHandle)
  }

  /** Unbinds the shader attributes after rendering. */
  fun unbindShaderAttributes() {
    val positionHandle = GLES20.glGetAttribLocation(shaderProgram.programId, "vPosition")
    vertexBuffer.unbind(positionHandle)
    colorBuffer.unbind()
    textureBuffer.unbind()
  }

  /** Draws the sphere using the currently bound buffers and shader program. */
  fun drawSphere() {
    indexBuffer.bind()
    indexBuffer.draw(GLES20.GL_TRIANGLES)
  }

  companion object {
    /** Default number of latitude bands for sphere tessellation. */
    const val DEFAULT_NUM_BANDS = 20

    /** Default number of longitude steps per latitude band. */
    const val DEFAULT_STEPS_PER_BAND = 28
  }
}
