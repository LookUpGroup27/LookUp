package com.github.lookupgroup27.lookup.model.map.renderables

import android.content.Context
import android.opengl.GLES20
import android.opengl.Matrix
import com.github.lookupgroup27.lookup.model.map.renderables.utils.Sphere.generateSphericalGeometry
import com.github.lookupgroup27.lookup.model.map.skybox.buffers.ColorBuffer
import com.github.lookupgroup27.lookup.model.map.skybox.buffers.IndexBuffer
import com.github.lookupgroup27.lookup.model.map.skybox.buffers.TextureBuffer
import com.github.lookupgroup27.lookup.model.map.skybox.buffers.VertexBuffer
import com.github.lookupgroup27.lookup.util.opengl.ShaderProgram
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
    private val name: String = "Planet",
    private val position: FloatArray = floatArrayOf(0.0f, 0.0f, 0.0f),
    private val textureId: Int,
    private val numBands: Int = DEFAULT_NUM_BANDS,
    private val stepsPerBand: Int = DEFAULT_STEPS_PER_BAND
) {
  private val vertexBuffer = VertexBuffer()
  private val colorBuffer = ColorBuffer()
  private val indexBuffer = IndexBuffer()
  private val textureBuffer = TextureBuffer()
  private lateinit var shaderProgram: ShaderProgram
  private var textureHandle: Int = 0
  private var scale: Float = 0.3f

  /**
   * Updates the scale of the planet.
   *
   * @param newScale The new scaling factor. A value of 1.0 represents the original size.
   */
  fun setScale(newScale: Float) {
    scale = newScale
  }

  /**
   * Initializes the planet by generating its geometry, setting up vertex buffers, and loading the
   * texture. This method prepares the planet for rendering.
   */
  fun initialize() {
    // Generate geometry
    val geometryData = generateSphericalGeometry(numBands, stepsPerBand)

    // Reset buffers
    val numVertices = numBands * stepsPerBand
    vertexBuffer.reset(numVertices)
    colorBuffer.reset(numVertices)
    indexBuffer.reset(geometryData.indices.size)
    textureBuffer.reset(numVertices)

    // Add vertices
    for (i in 0 until geometryData.vertices.size step 3) {
      vertexBuffer.addVertex(
          geometryData.vertices[i], geometryData.vertices[i + 1], geometryData.vertices[i + 2])
    }

    // Add colors
    geometryData.colors.forEach { colorBuffer.addColor(it) }

    // Add texture coordinates
    for (i in 0 until geometryData.textureCoords.size step 2) {
      textureBuffer.addTexCoord(geometryData.textureCoords[i], geometryData.textureCoords[i + 1])
    }

    // Add indices
    geometryData.indices.forEach { indexBuffer.addIndex(it) }

    // Load texture
    val textureManager = TextureManager(context)
    textureHandle = textureManager.loadTexture(textureId)

    // Initialize shader program
    val vertexShaderCode =
        """
            attribute vec4 vPosition;
            attribute vec4 vColor;
            attribute vec2 vTexCoord;
            uniform mat4 uMVPMatrix;
            varying vec4 vInterpolatedColor;
            varying vec2 vInterpolatedTexCoord;
            void main() {
                gl_Position = uMVPMatrix * vPosition;
                vInterpolatedColor = vColor;
                vInterpolatedTexCoord = vTexCoord;
            }
        """
            .trimIndent()

    val fragmentShaderCode =
        """
            precision mediump float;
            varying vec4 vInterpolatedColor;
            varying vec2 vInterpolatedTexCoord;
            uniform sampler2D uTexture;
            void main() {
                gl_FragColor = texture2D(uTexture, vInterpolatedTexCoord);
            }
        """
            .trimIndent()

    shaderProgram = ShaderProgram(vertexShaderCode, fragmentShaderCode)
  }

  /**
   * Renders the planet using the provided Model-View-Projection (MVP) matrix.
   *
   * @param mvpMatrix A 4x4 matrix that combines the model, view, and projection transformations.
   */
  fun render(mvpMatrix: FloatArray) {
    shaderProgram.use()

    // Create a model matrix and apply scaling
    val modelMatrix = FloatArray(16)
    Matrix.setIdentityM(modelMatrix, 0)

    // Apply translation using position
    Matrix.translateM(modelMatrix, 0, position[0], position[1], position[2])

    Matrix.scaleM(modelMatrix, 0, scale, scale, scale) // Apply scale transformation

    // Combine the model matrix with the provided MVP matrix
    val scaledMvpMatrix = FloatArray(16)
    Matrix.multiplyMM(scaledMvpMatrix, 0, mvpMatrix, 0, modelMatrix, 0)

    // Pass the scaled MVP matrix to the shader
    val mvpMatrixHandle = GLES20.glGetUniformLocation(shaderProgram.programId, "uMVPMatrix")
    GLES20.glUniformMatrix4fv(mvpMatrixHandle, 1, false, scaledMvpMatrix, 0)

    val positionHandle = GLES20.glGetAttribLocation(shaderProgram.programId, "vPosition")
    vertexBuffer.bind(positionHandle)

    val colorHandle = GLES20.glGetAttribLocation(shaderProgram.programId, "vColor")
    colorBuffer.bind(colorHandle)

    val texCoordHandle = GLES20.glGetAttribLocation(shaderProgram.programId, "vTexCoord")
    textureBuffer.bind(texCoordHandle)

    // Bind texture
    val textureUniformHandle = GLES20.glGetUniformLocation(shaderProgram.programId, "uTexture")
    GLES20.glActiveTexture(GLES20.GL_TEXTURE0)
    GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureHandle)
    GLES20.glUniform1i(textureUniformHandle, 0)

    indexBuffer.bind()
    indexBuffer.draw(GLES20.GL_TRIANGLES)

    vertexBuffer.unbind(positionHandle)
    colorBuffer.unbind()
    textureBuffer.unbind()
  }

  companion object {
    private const val DEFAULT_NUM_BANDS = 20
    private const val DEFAULT_STEPS_PER_BAND = 28
  }
}
