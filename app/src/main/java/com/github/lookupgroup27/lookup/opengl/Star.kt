package com.github.lookupgroup27.lookup.opengl

import android.opengl.GLES20
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer
import java.nio.ShortBuffer

const val COORDS_PER_VERTEX = 3

class Star (val x: Float, val y: Float, val z: Float, color: FloatArray) {

  // Vertex shader source code
  protected val vertexShaderCode =
      // This matrix member variable provides a hook to manipulate
      // the coordinates of the objects that use this vertex shader
      "uniform mat4 modelMatrix;" +
          "uniform mat4 viewMatrix;" +
          "uniform mat4 projMatrix;" +
          "attribute vec4 vPosition;" +
          "attribute vec3 vColor;" +
          "varying vec3 color;" +
          "void main() {" +
          // the matrix must be included as a modifier of gl_Position
          // Note that the uMVPMatrix factor *must be first* in order
          // for the matrix multiplication product to be correct.
          "  gl_Position = projMatrix * viewMatrix * modelMatrix * vPosition;" +
          "  color = vColor;" +
          "}"

  // Use to access and set the view transformation
  protected var vPMatrixHandle: Int = 0

  // Fragment shader source code
  protected val fragmentShaderCode =
      "precision mediump float;" +
          "varying vec3 color;" +
          "void main() {" +
          "  gl_FragColor = vec4(color, 1.0);" +
          "}"

  val color = floatArrayOf(1.0f, 1.0f, 1.0f, 1.0f)

  // number of coordinates per vertex in this array
  var coords =
      floatArrayOf(
          -0.05f + x, -0.05f + y, 0.05f + z,
          -0.05f + x, -0.05f + y, -0.05f + z,
          0.05f + x, -0.05f + y, -0.05f + z,
          0.05f+ x, -0.05f + y, 0.05f + z,
          0.0f+ x, 0.05f + y, 0.0f + z)

  var colorVertex =
      floatArrayOf(
        color[0], color[1], color[2],
        color[0], color[1], color[2],
        color[0], color[1], color[2],
        color[0], color[1], color[2],
        0.0f, 0.0f, 0.0f)

  // Indices in which openGL will draw each triangle vertex by vertex
  // e.g. It will first draw the triangle with vertices 0, 3, 5
  // then 3, 2, 4 and so on
  private val drawOrder =
      shortArrayOf(0, 1, 2, 0, 2, 3, 0, 1, 4, 1, 2, 4, 2, 3, 4, 3, 0, 4) // order to draw vertices

  private val bytesForOneFloat = 4

  // initialize vertex byte buffer for shape coordinates
  private val vertexBuffer: FloatBuffer =
      // (# of coordinate values * 4 bytes per float)
      ByteBuffer.allocateDirect(coords.size * bytesForOneFloat).run {
        order(ByteOrder.nativeOrder())
        asFloatBuffer().apply {
          put(coords)
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
  private var modelMatrixHandle: Int = 0
  private var viewMatrixHandle: Int = 0
  private var projMatrixHandle: Int = 0
  private var mColorHandle: Int = 0

  private val vertexCount: Int = coords.size / COORDS_PER_VERTEX
  private val vertexStride: Int = COORDS_PER_VERTEX * 4

  fun draw(camera: Camera) {
    // Add program to OpenGL ES environment
    GLES20.glUseProgram(mProgram)

    // get handle to vertex shader's vPosition member
    positionHandle = GLES20.glGetAttribLocation(mProgram, "vPosition")
    GLES20.glEnableVertexAttribArray(positionHandle)

    GLES20.glVertexAttribPointer(
        positionHandle, COORDS_PER_VERTEX, GLES20.GL_FLOAT, false, vertexStride, vertexBuffer)

    // get handle to fragment shader's vColor member
    mColorHandle = GLES20.glGetAttribLocation(mProgram, "vColor")
    GLES20.glEnableVertexAttribArray(mColorHandle)
    GLES20.glVertexAttribPointer(mColorHandle, 3, GLES20.GL_FLOAT, false, 0, colorBuffer)

    modelMatrixHandle = GLES20.glGetUniformLocation(mProgram, "modelMatrix")
    GLES20.glUniformMatrix4fv(modelMatrixHandle, 1, false, camera.modelMatrix, 0)
    viewMatrixHandle = GLES20.glGetUniformLocation(mProgram, "viewMatrix")
    GLES20.glUniformMatrix4fv(viewMatrixHandle, 1, false, camera.viewMatrix, 0)
    projMatrixHandle = GLES20.glGetUniformLocation(mProgram, "projMatrix")
    GLES20.glUniformMatrix4fv(projMatrixHandle, 1, false, camera.projMatrix, 0)

    // This time we use draw elements cause it's a composition of multiple triangles
    // Draw every triangles
    GLES20.glDrawElements(
        GLES20.GL_TRIANGLES, drawOrder.size, GLES20.GL_UNSIGNED_SHORT, drawListBuffer)

    // Disable vertex array
    GLES20.glDisableVertexAttribArray(positionHandle)
    GLES20.glDisableVertexAttribArray(mColorHandle)
  }
}
