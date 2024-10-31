package com.github.lookupgroup27.lookup.opengl.dim2

import android.opengl.GLES20
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer

class Triangle : Shape() {
  // Coordinates of each vertex in counterclockwise order of the triangle
  var triangleCoords =
      floatArrayOf( // in counterclockwise order:
          0.0f, 0.622008459f, 0.0f, // top
          -0.5f, -0.311004243f, 0.0f, // bottom left
          0.5f, -0.311004243f, 0.0f // bottom right
      )

  // Set color with red, green, blue and alpha (opacity) values
  override val color = floatArrayOf(0.63671875f, 0.76953125f, 0.22265625f, 1.0f)


  // This buffer is used to pass the vertex data to OpenGL ES for rendering
  // The FloatBuffer will be usd in the draw method to pass the triangleCoords to OpenGL ES
  private var vertexBuffer: FloatBuffer =
      // Allocates a direct bytre buffer
      ByteBuffer.allocateDirect(triangleCoords.size * 4).run {
        // use the device hardware's native byte order
        order(ByteOrder.nativeOrder())

        // convert the ByteBuffer to a FloatBuffer so that we can use it to pass the vertex data to OpenGL ES
        asFloatBuffer().apply {
          // add the coordinates to the FloatBuffer
          put(triangleCoords)
          // set the buffer to read the first coordinate
          position(0)
        }
      }

  private var mProgram: Int

  init {
    // Load the vertex/fragment shaders
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

  private val vertexCount: Int = triangleCoords.size / COORDS_PER_VERTEX
  private val vertexStride: Int = COORDS_PER_VERTEX * 4 // 4 bytes per vertex

  override fun draw(mvpMatrix: FloatArray) {

    // Add program to OpenGL ES environment
    GLES20.glUseProgram(mProgram)

    // get handle to shape's transformation matrix
    vPMatrixHandle = GLES20.glGetUniformLocation(mProgram, "uMVPMatrix")

    // get handle to vertex shader's vPosition member
    positionHandle =
        GLES20.glGetAttribLocation(mProgram, "vPosition").also {

          // Enable a handle to the triangle vertices
          GLES20.glEnableVertexAttribArray(it)

          // Prepare the triangle coordinate data
          GLES20.glVertexAttribPointer(
              it, COORDS_PER_VERTEX, GLES20.GL_FLOAT, false, vertexStride, vertexBuffer)

          // get handle to fragment shader's vColor member
          mColorHandle =
              GLES20.glGetUniformLocation(mProgram, "vColor").also { colorHandle ->

                // Set color for drawing the triangle
                GLES20.glUniform4fv(colorHandle, 1, color, 0)
              }
          // Pass the projection and view transformation to the shader
          GLES20.glUniformMatrix4fv(vPMatrixHandle, 1, false, mvpMatrix, 0)

          // Draw the triangle
          GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, vertexCount)

          // Disable vertex array
          GLES20.glDisableVertexAttribArray(it)
        }
  }
}
