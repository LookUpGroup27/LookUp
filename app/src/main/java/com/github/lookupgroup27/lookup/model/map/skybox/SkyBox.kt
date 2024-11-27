package com.github.lookupgroup27.lookup.model.map.skybox

import android.opengl.GLES20
import android.opengl.Matrix
import com.github.lookupgroup27.lookup.model.map.Camera
import com.github.lookupgroup27.lookup.model.map.renderables.utils.Sphere.generateSphericalGeometry
import com.github.lookupgroup27.lookup.model.map.skybox.buffers.ColorBuffer
import com.github.lookupgroup27.lookup.model.map.skybox.buffers.IndexBuffer
import com.github.lookupgroup27.lookup.model.map.skybox.buffers.TextureBuffer
import com.github.lookupgroup27.lookup.model.map.skybox.buffers.VertexBuffer
import com.github.lookupgroup27.lookup.util.opengl.ShaderProgram

/**
 * Represents a skybox in the OpenGL rendering environment.
 *
 * The skybox consists of a set of vertices, colors, and indices to simulate the sky using a
 * spherical or cubic geometry.
 *
 * @param numBands The number of vertex bands in the skybox.
 * @param stepsPerBand The number of steps in each band.
 */
class SkyBox(
    private val numBands: Int = DEFAULT_NUM_BANDS,
    private val stepsPerBand: Int = DEFAULT_STEPS_PER_BAND
) {
  private val vertexBuffer = VertexBuffer()
  private val colorBuffer = ColorBuffer()
  private val indexBuffer = IndexBuffer()
  private val textureBuffer = TextureBuffer() // New texture buffer

  // Shader program for rendering the SkyBox
  private var shaderProgram: ShaderProgram

  /** Initializes the skybox by generating its geometry and buffers. */
  init {
    val numVertices = numBands * stepsPerBand
    val numIndices = (numBands - 1) * stepsPerBand * 6

    val geometryData = generateSphericalGeometry(numBands, stepsPerBand)

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
    // Initialize the ShaderProgram
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
   * Renders the skybox using the shader program.
   *
   * @param camera The camera to use for rendering the skybox.
   */
  fun draw(camera: Camera) {

    // Create a modified view matrix for the skybox that ignores translations
    val skyboxViewMatrix = FloatArray(16)
    System.arraycopy(camera.viewMatrix, 0, skyboxViewMatrix, 0, 16)
    skyboxViewMatrix[12] = 0f // X translation
    skyboxViewMatrix[13] = 0f // Y translation
    skyboxViewMatrix[14] = 0f // Z translation

    // Compute the skybox MVP matrix
    val skyboxMvpMatrix = FloatArray(16)
    Matrix.multiplyMM(skyboxMvpMatrix, 0, camera.projMatrix, 0, skyboxViewMatrix, 0)

    shaderProgram.use()

    // Pass the MVP matrix to the shader
    val mvpMatrixHandle = GLES20.glGetUniformLocation(shaderProgram.programId, "uMVPMatrix")
    GLES20.glUniformMatrix4fv(mvpMatrixHandle, 1, false, skyboxMvpMatrix, 0)

    // Bind the vertex and color buffers
    val positionHandle = GLES20.glGetAttribLocation(shaderProgram.programId, "vPosition")
    vertexBuffer.bind(positionHandle)

    val colorHandle = GLES20.glGetAttribLocation(shaderProgram.programId, "vColor")
    colorBuffer.bind(colorHandle)

    val texCoordHandle = GLES20.glGetAttribLocation(shaderProgram.programId, "vTexCoord")
    textureBuffer.bind(texCoordHandle)

    // Draw the elements
    indexBuffer.bind()
    indexBuffer.draw(GLES20.GL_TRIANGLES)

    // Unbind the buffers
    vertexBuffer.unbind(positionHandle)
    colorBuffer.unbind()
    textureBuffer.unbind()
  }

  companion object {
    private const val DEFAULT_NUM_BANDS = 20
    private const val DEFAULT_STEPS_PER_BAND = 28
  }
}
