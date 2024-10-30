package com.github.lookupgroup27.lookup.opengl

import android.opengl.GLES20
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer
import java.nio.ShortBuffer

class Square : Shape() {

  override val color = floatArrayOf(1.0f, 1.0f, 1.0f, 1.0f)

  // number of coordinates per vertex in this array
  var squareCoords =
    floatArrayOf(
      -0.25f, -0.25f, 0.0f,
      -0.25f, 0.25f, 0.0f,
      0.25f, 0.25f, 0.0f,
      0.25f, -0.25f, 0.0f
    )

  var colorVertex =
    floatArrayOf(
      1.0f, 0.0f, 0.0f,
      0.0f, 1.0f, 0.0f,
      0.0f, 0.0f, 1.0f,
      0.0f, 0.0f, 0.0f
    )

  // Indices in which openGL will draw each triangle vertex by vertex
  // e.g. It will first draw the triangle with vertices 0, 3, 5
  // then 3, 2, 4 and so on
  private val drawOrder =
    shortArrayOf(
      0, 1, 2,
      0, 2, 3
    ) // order to draw vertices

  private val bytesForOneFloat = 4

  // initialize vertex byte buffer for shape coordinates
  private val vertexBuffer: FloatBuffer =
    // (# of coordinate values * 4 bytes per float)
    ByteBuffer.allocateDirect(squareCoords.size * bytesForOneFloat).run {
      order(ByteOrder.nativeOrder())
      asFloatBuffer().apply {
        put(squareCoords)
        position(0)
      }
    }
  private val colorBuffer: FloatBuffer =
    // (# of coordinate values * 4 bytes per float)
    ByteBuffer.allocateDirect(colorVertex.size * bytesForOneFloat).run {
      order(ByteOrder.nativeOrder())
      asFloatBuffer().apply {
        put(colorVertex)
        position(0)
      }
    }

  private val bytesForOneShort = 2

  // initialize byte buffer for the draw list
  private val drawListBuffer: ShortBuffer =
    // (# of coordinate values * 2 bytes per short)
    ByteBuffer.allocateDirect(drawOrder.size * bytesForOneShort).run {
      order(ByteOrder.nativeOrder())
      asShortBuffer().apply {
        put(drawOrder)
        position(0)
      }
    }

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

  private var positionHandle: Int = 0
  private var mColorHandle: Int = 0

  private val vertexCount: Int = squareCoords.size / COORDS_PER_VERTEX
  private val vertexStride: Int = COORDS_PER_VERTEX * 4

  override fun draw(mvpMatrix: FloatArray) {
    // Add program to OpenGL ES environment
    GLES20.glUseProgram(mProgram)

    vPMatrixHandle = GLES20.glGetUniformLocation(mProgram, "uMVPMatrix")

    // get handle to vertex shader's vPosition member
    positionHandle = GLES20.glGetAttribLocation(mProgram, "vPosition")
    GLES20.glEnableVertexAttribArray(positionHandle)

    GLES20.glVertexAttribPointer(
      positionHandle, COORDS_PER_VERTEX, GLES20.GL_FLOAT, false, vertexStride, vertexBuffer
    )

    // get handle to fragment shader's vColor member
    mColorHandle = GLES20.glGetAttribLocation(mProgram, "vColor")
    GLES20.glEnableVertexAttribArray(mColorHandle)
    GLES20.glVertexAttribPointer(
      mColorHandle, 3, GLES20.GL_FLOAT, false, 0, colorBuffer
    )


    GLES20.glUniformMatrix4fv(vPMatrixHandle, 1, false, mvpMatrix, 0)

    // This time we use draw elements cause it's a composition of multiple triangles
    // Draw every triangles
    GLES20.glDrawElements(
      GLES20.GL_TRIANGLES, drawOrder.size, GLES20.GL_UNSIGNED_SHORT, drawListBuffer
    )

    // Disable vertex array
    GLES20.glDisableVertexAttribArray(positionHandle)
    GLES20.glDisableVertexAttribArray(mColorHandle)
  }
}
