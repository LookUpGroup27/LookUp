package com.github.lookupgroup27.lookup.ui.map.renderables

import android.content.Context
import android.opengl.GLES20
import android.opengl.GLUtils
import android.opengl.Matrix
import com.github.lookupgroup27.lookup.model.map.Camera
import com.github.lookupgroup27.lookup.util.opengl.BufferUtils.toBuffer
import com.github.lookupgroup27.lookup.util.opengl.ShaderUtils.readShader
import com.github.lookupgroup27.lookup.util.opengl.LabelUtils
import com.github.lookupgroup27.lookup.util.opengl.Position
import com.github.lookupgroup27.lookup.util.opengl.ShaderProgram
import java.nio.FloatBuffer

/**
 * A label that displays text in the 3D world.
 *
 * @param context The application context
 * @param text The text to display on the label
 * @param pos The position of the label TODO Implement this in others OpenGL classes
 */
class Label(context: Context, text: String, var pos: Position, size: Float) {
  private val shaderProgram: ShaderProgram
  private val textureId: Int
  private val vertexBuffer: FloatBuffer
  private val texCoordBuffer: FloatBuffer

  init {
    // Initialize shader program
    val vertexShaderCode = readShader(context, "label_vertex_shader.glsl")
    val fragmentShaderCode = readShader(context, "label_fragment_shader.glsl")

    shaderProgram = ShaderProgram(vertexShaderCode, fragmentShaderCode)

    // Define vertices for a quad that will display the label
    // These coordinates represent a quad that fills the screen
    val vertices =
      floatArrayOf(
        -size,
        -size,
        0f, // Bottom left
        size,
        -size,
        0f, // Bottom right
        -size,
        size,
        0f, // Top left
        size,
        size,
        0f // Top right
      )
    vertexBuffer = vertices.toBuffer()

    // Define texture coordinates
    val texCoords =
      floatArrayOf(
        0f,
        1f, // Bottom left
        1f,
        1f, // Bottom right
        0f,
        0f, // Top left
        1f,
        0f // Top right
      )
    texCoordBuffer = texCoords.toBuffer()

    // Initialize Label texture
    val textureHandles = IntArray(1)
    GLES20.glGenTextures(1, textureHandles, 0)
    textureId = textureHandles[0]

    // Bind texture
    GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureId)

    // Set texture parameters
    GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR)
    GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR)

    // Load bitmap to OpenGL
    val bitmap = LabelUtils.createLabelBitmap(text)
    GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bitmap, 0)
  }

  /**
   * Draws the label in the 3D world.
   *
   * @param camera The camera used to render the label
   */
  fun draw(camera: Camera) {
    shaderProgram.use() // FIXME: Remove this one line method ?

    // Get attribute and uniform locations
    val modelMatrixHandle = GLES20.glGetUniformLocation(shaderProgram.programId, "uModelMatrix")
    val viewMatrixHandle = GLES20.glGetUniformLocation(shaderProgram.programId, "uViewMatrix")
    val projMatrixHandle = GLES20.glGetUniformLocation(shaderProgram.programId, "uProjMatrix")
    val positionHandle = GLES20.glGetAttribLocation(shaderProgram.programId, "aPosition")
    val texCoordHandle = GLES20.glGetAttribLocation(shaderProgram.programId, "aTexCoordinate")
    val textureHandle = GLES20.glGetUniformLocation(shaderProgram.programId, "uTexture")

    // Bind texture
    GLES20.glActiveTexture(GLES20.GL_TEXTURE0)
    GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureId)
    GLES20.glUniform1i(textureHandle, 0)

    // Set up vertex and texture coordinate buffers
    vertexBuffer.position(0)
    GLES20.glVertexAttribPointer(positionHandle, 3, GLES20.GL_FLOAT, false, 0, vertexBuffer)
    GLES20.glEnableVertexAttribArray(positionHandle)

    texCoordBuffer.position(0)
    GLES20.glVertexAttribPointer(texCoordHandle, 2, GLES20.GL_FLOAT, false, 0, texCoordBuffer)
    GLES20.glEnableVertexAttribArray(texCoordHandle)

    val billboardMatrix = FloatArray(16)
    val modelMatrix = camera.modelMatrix.clone()

    // Extract camera look direction from view matrix
    val lookX = -camera.viewMatrix[2]
    val lookY = -camera.viewMatrix[6]
    val lookZ = -camera.viewMatrix[10]

    // Create billboard rotation (this is the vector compared to the object that points up for the text
    // Example we take a text Hello, we have:
    // ↑ Hello
    val upX = camera.viewMatrix[1]
    val upY = camera.viewMatrix[5]
    val upZ = camera.viewMatrix[9]

    // Calculate right vector (cross product)
    val rightX = upY * lookZ - upZ * lookY
    val rightY = upZ * lookX - upX * lookZ
    val rightZ = upX * lookY - upY * lookX

    // Set billboard matrix
    billboardMatrix[0] = -rightX
    billboardMatrix[1] = -rightY
    billboardMatrix[2] = -rightZ
    billboardMatrix[3] = 0f

    billboardMatrix[4] = upX
    billboardMatrix[5] = upY
    billboardMatrix[6] = upZ
    billboardMatrix[7] = 0f

    billboardMatrix[8] = lookX
    billboardMatrix[9] = lookY
    billboardMatrix[10] = lookZ
    billboardMatrix[11] = 0f

    billboardMatrix[12] = 0f
    billboardMatrix[13] = 0f
    billboardMatrix[14] = 0f
    billboardMatrix[15] = 1f

    // First translate to position
    Matrix.translateM(modelMatrix, 0, pos.x, pos.y, pos.z)

    // Then apply billboard rotation
    val rotatedMatrix = FloatArray(16)
    Matrix.multiplyMM(rotatedMatrix, 0, modelMatrix, 0, billboardMatrix, 0)

    // Set the MVP matrix
    GLES20.glUniformMatrix4fv(modelMatrixHandle, 1, false, rotatedMatrix, 0)
    GLES20.glUniformMatrix4fv(viewMatrixHandle, 1, false, camera.viewMatrix, 0)
    GLES20.glUniformMatrix4fv(projMatrixHandle, 1, false, camera.projMatrix, 0)

    // Draw the text
    GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4)
  }
}
