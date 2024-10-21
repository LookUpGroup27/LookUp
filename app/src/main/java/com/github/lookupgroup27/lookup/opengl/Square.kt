package com.github.lookupgroup27.lookup.opengl

import android.opengl.GLES20
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer
import java.nio.ShortBuffer

class Square : Shape() {

  override val color = floatArrayOf(0.0f, 0.0f, 1.0f, 1.0f)

  // number of coordinates per vertex in this array
  var squareCoords = floatArrayOf(
    -0.5f, 0.5f, 0.0f,      // top left
    -0.5f, -0.5f, 0.0f,      // bottom left
    0.5f, -0.5f, 0.0f,      // bottom right
    0.5f, 0.5f, 0.0f       // top right
  )

  private val drawOrder = shortArrayOf(0, 1, 2, 0, 2, 3) // order to draw vertices

  // initialize vertex byte buffer for shape coordinates
  private val vertexBuffer: FloatBuffer =
    // (# of coordinate values * 4 bytes per float)
    ByteBuffer.allocateDirect(squareCoords.size * 4).run {
      order(ByteOrder.nativeOrder())
      asFloatBuffer().apply {
        put(squareCoords)
        position(0)
      }
    }

  // initialize byte buffer for the draw list
  private val drawListBuffer: ShortBuffer =
    // (# of coordinate values * 2 bytes per short)
    ByteBuffer.allocateDirect(drawOrder.size * 2).run {
      order(ByteOrder.nativeOrder())
      asShortBuffer().apply {
        put(drawOrder)
        position(0)
      }
    }

  private val vertexShaderCode =
    "attribute vec4 vPosition;" +
      "void main() {" +
      "  gl_Position = vPosition;" +
      "}"

  private val fragmentShaderCode =
    "precision mediump float;" +
      "uniform vec4 vColor;" +
      "void main() {" +
      "  gl_FragColor = vColor;" +
      "}"

  private var mProgram: Int

  init {
    val vertexShader: Int = loadShader(GLES20.GL_VERTEX_SHADER, vertexShaderCode)
    val fragmentShader: Int = loadShader(GLES20.GL_FRAGMENT_SHADER, fragmentShaderCode)

    // create empty OpenGL ES Program
    mProgram = GLES20.glCreateProgram().also {

      // add the vertex shader to program
      GLES20.glAttachShader(it, vertexShader)

      // add the fragment shader to program
      GLES20.glAttachShader(it, fragmentShader)

      // creates OpenGL ES program executables
      GLES20.glLinkProgram(it)
    }
  }

  private var positionHandle: Int = 0
  private var mColorHandle: Int = 0

  private val vertexCount: Int = squareCoords.size / COORDS_PER_VERTEX
  private val vertexStride: Int = COORDS_PER_VERTEX * 4

  override fun draw() {
    // Add program to OpenGL ES environment
    GLES20.glUseProgram(mProgram)

    // get handle to vertex shader's vPosition member
    positionHandle = GLES20.glGetAttribLocation(mProgram, "vPosition").also {
      GLES20.glEnableVertexAttribArray(it)

      GLES20.glVertexAttribPointer(
        it,
        COORDS_PER_VERTEX,
        GLES20.GL_FLOAT,
        false,
        vertexStride,
        vertexBuffer
      )

      // get handle to fragment shader's vColor member
      mColorHandle = GLES20.glGetUniformLocation(mProgram, "vColor").also {
        GLES20.glUniform4fv(it, 1, color, 0)
      }

      // Draw the square
      GLES20.glDrawElements(
        GLES20.GL_TRIANGLES,
        drawOrder.size,
        GLES20.GL_UNSIGNED_SHORT,
        drawListBuffer
      )

      // Disable vertex array
      GLES20.glDisableVertexAttribArray(it)
    }
  }
}