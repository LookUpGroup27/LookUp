package com.github.lookupgroup27.lookup.model.map.renderables

import BufferUtils.toBuffer
import android.opengl.GLES20
import com.github.lookupgroup27.lookup.model.map.Camera

/**
 * Represents a star in the 3D space
 *
 * @param x the x coordinate of the star
 * @param y the y coordinate of the star
 * @param z the z coordinate of the star
 * @param color the color of the star
 */
class Star(x: Float, y: Float, z: Float, color: FloatArray) : Object() {

  private val vertexShaderCode =
      "uniform mat4 modelMatrix;" +
          "uniform mat4 viewMatrix;" +
          "uniform mat4 projMatrix;" +
          "attribute vec4 vPosition;" +
          "attribute vec3 vColor;" +
          "varying vec3 color;" +
          "void main() {" +
          "  gl_Position = projMatrix * viewMatrix * modelMatrix * vPosition;" +
          "  color = vColor;" +
          "}"

  private val fragmentShaderCode =
      "precision mediump float;" +
          "varying vec3 color;" +
          "void main() {" +
          "  gl_FragColor = vec4(color, 1.0);" +
          "}"

  private val vertexCoordinates: FloatArray =
      floatArrayOf(
          -0.05f + x,
          -0.05f + y,
          0.05f + z,
          -0.05f + x,
          -0.05f + y,
          -0.05f + z,
          0.05f + x,
          -0.05f + y,
          -0.05f + z,
          0.05f + x,
          -0.05f + y,
          0.05f + z,
          0.00f + x,
          0.05f + y,
          0.00f + z)

  private val vertexColor: FloatArray =
      floatArrayOf(
          color[0],
          color[1],
          color[2],
          color[0],
          color[1],
          color[2],
          color[0],
          color[1],
          color[2],
          color[0],
          color[1],
          color[2],
          0.0f,
          0.0f,
          0.0f)

  private val vertexDrawOrder: ShortArray =
      shortArrayOf(0, 1, 2, 0, 2, 3, 0, 1, 4, 1, 2, 4, 2, 3, 4, 3, 0, 4)

  private var mProgram: Int

  init {
    val vertexShader: Int = loadShader(GLES20.GL_VERTEX_SHADER, vertexShaderCode)
    val fragmentShader: Int = loadShader(GLES20.GL_FRAGMENT_SHADER, fragmentShaderCode)

    // create empty OpenGL ES Program
    mProgram =
        GLES20.glCreateProgram().also {

          // add the vertex shader to program
          GLES20.glAttachShader(it, vertexShader)

          // add the fragment shader to program
          GLES20.glAttachShader(it, fragmentShader)

          // creates OpenGL ES program executables
          GLES20.glLinkProgram(it)
        }
  }

  /**
   * Draw the star on the screen
   *
   * @param camera the camera to use to draw the star
   */
  fun draw(camera: Camera) {
    // Add program to OpenGL ES environment
    GLES20.glUseProgram(mProgram)

    // get handle to vertex shader's vPosition member
    val positionHandle = GLES20.glGetAttribLocation(mProgram, "vPosition")
    GLES20.glEnableVertexAttribArray(positionHandle)

    GLES20.glVertexAttribPointer(
        positionHandle,
        3,
        GLES20.GL_FLOAT,
        false,
        3 * Float.SIZE_BYTES,
        vertexCoordinates.toBuffer())

    // get handle to fragment shader's vColor member
    val mColorHandle = GLES20.glGetAttribLocation(mProgram, "vColor")
    GLES20.glEnableVertexAttribArray(mColorHandle)
    GLES20.glVertexAttribPointer(mColorHandle, 3, GLES20.GL_FLOAT, false, 0, vertexColor.toBuffer())

    val modelMatrixHandle = GLES20.glGetUniformLocation(mProgram, "modelMatrix")
    GLES20.glUniformMatrix4fv(modelMatrixHandle, 1, false, camera.modelMatrix, 0)
    val viewMatrixHandle = GLES20.glGetUniformLocation(mProgram, "viewMatrix")
    GLES20.glUniformMatrix4fv(viewMatrixHandle, 1, false, camera.viewMatrix, 0)
    val projMatrixHandle = GLES20.glGetUniformLocation(mProgram, "projMatrix")
    GLES20.glUniformMatrix4fv(projMatrixHandle, 1, false, camera.projMatrix, 0)

    // This time we use draw elements cause it's a composition of multiple triangles
    // Draw every triangles
    GLES20.glDrawElements(
        GLES20.GL_TRIANGLES,
        vertexDrawOrder.size,
        GLES20.GL_UNSIGNED_SHORT,
        vertexDrawOrder.toBuffer())

    // Disable vertex array
    GLES20.glDisableVertexAttribArray(positionHandle)
    GLES20.glDisableVertexAttribArray(mColorHandle)
  }
}
