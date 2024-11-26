package com.github.lookupgroup27.lookup.model.map.skybox

import android.opengl.GLES20
import android.opengl.Matrix
import android.util.Log
import com.github.lookupgroup27.lookup.model.map.Camera
import com.github.lookupgroup27.lookup.model.map.skybox.buffers.ColorBuffer
import com.github.lookupgroup27.lookup.model.map.skybox.buffers.IndexBuffer
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

  // Shader program for rendering the SkyBox
  private var shaderProgram: ShaderProgram

  /** Initializes the skybox by generating its geometry and buffers. */
  init {
    val numVertices = numBands * stepsPerBand
    val numIndices = (numBands - 1) * stepsPerBand * 6

    vertexBuffer.reset(numVertices)
    colorBuffer.reset(numVertices)
    indexBuffer.reset(numIndices)

    generateGeometry()
    // Initialize the ShaderProgram
    val vertexShaderCode =
        """
            attribute vec4 vPosition;
            attribute vec4 vColor;
            uniform mat4 uMVPMatrix;
            varying vec4 vInterpolatedColor;
            void main() {
                gl_Position = uMVPMatrix * vPosition;
                vInterpolatedColor = vColor;
            }
        """
            .trimIndent()

    val fragmentShaderCode =
        """
        precision mediump float;
        varying vec4 vInterpolatedColor;
        void main() {
            gl_FragColor = vInterpolatedColor;
        }
      """
            .trimIndent()

    shaderProgram = ShaderProgram(vertexShaderCode, fragmentShaderCode)
  }

  /** Generates the vertices, colors, and indices for the skybox. */
  private fun generateGeometry() {
    val sinAngles = FloatArray(stepsPerBand)
    val cosAngles = FloatArray(stepsPerBand)

    // Calculate angles for a circular band
    val angleStep = 2.0f * Math.PI.toFloat() / (stepsPerBand - 1)
    var angle = 0f
    for (i in sinAngles.indices) {
      sinAngles[i] = Math.sin(angle.toDouble()).toFloat()
      cosAngles[i] = Math.cos(angle.toDouble()).toFloat()
      angle += angleStep
    }

    val bandStep = 2.0f / (numBands - 1)
    var bandPos = 1f

    // Generate vertices and colors
    for (band in 0 until numBands) {
      val intensity =
          if (bandPos > 0) {
            (bandPos * 20 + 50).toInt()
          } else {
            (bandPos * 40 + 40).toInt()
          }

      val color = (intensity shl 16) or 0xff000000.toInt()
      val sinPhi = if (bandPos > -1) Math.sqrt(1 - bandPos * bandPos.toDouble()).toFloat() else 0f

      for (i in 0 until stepsPerBand) {
        vertexBuffer.addVertex(cosAngles[i] * sinPhi, bandPos, sinAngles[i] * sinPhi)
        colorBuffer.addColor(color)

        Log.d("SkyBox", "Vertex: (${cosAngles[i] * sinPhi}, $bandPos, ${sinAngles[i] * sinPhi})")
      }

      bandPos -= bandStep
    }

    // Generate indices
    var topBandStart = 0
    var bottomBandStart = stepsPerBand

    for (band in 0 until numBands - 1) {
      for (i in 0 until stepsPerBand - 1) {
        val topLeft = (topBandStart + i).toShort()
        val topRight = (topLeft + 1).toShort()
        val bottomLeft = (bottomBandStart + i).toShort()
        val bottomRight = (bottomLeft + 1).toShort()

        // First triangle
        indexBuffer.addIndex(topLeft)
        indexBuffer.addIndex(bottomRight)
        indexBuffer.addIndex(bottomLeft)

        // Second triangle
        indexBuffer.addIndex(topRight)
        indexBuffer.addIndex(bottomRight)
        indexBuffer.addIndex(topLeft)
      }

      // Close the circular band
      indexBuffer.addIndex((topBandStart + stepsPerBand - 1).toShort())
      indexBuffer.addIndex(bottomBandStart.toShort())
      indexBuffer.addIndex((bottomBandStart + stepsPerBand - 1).toShort())
      indexBuffer.addIndex(topBandStart.toShort())
      indexBuffer.addIndex(bottomBandStart.toShort())
      indexBuffer.addIndex((topBandStart + stepsPerBand - 1).toShort())

      topBandStart += stepsPerBand
      bottomBandStart += stepsPerBand
    }
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

    // Draw the elements
    indexBuffer.bind()
    indexBuffer.draw(GLES20.GL_TRIANGLES)

    // Unbind the buffers
    vertexBuffer.unbind(positionHandle)
    colorBuffer.unbind()
  }

  companion object {
    private const val DEFAULT_NUM_BANDS = 20
    private const val DEFAULT_STEPS_PER_BAND = 28
  }
}
