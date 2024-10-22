package com.github.lookupgroup27.lookup.opengl

const val COORDS_PER_VERTEX = 3

abstract class Shape {
  abstract val color: FloatArray

  protected val vertexShaderCode =
      // This matrix member variable provides a hook to manipulate
      // the coordinates of the objects that use this vertex shader
      "uniform mat4 uMVPMatrix;" +
          "attribute vec4 vPosition;" +
          "void main() {" +
          // the matrix must be included as a modifier of gl_Position
          // Note that the uMVPMatrix factor *must be first* in order
          // for the matrix multiplication product to be correct.
          "  gl_Position = uMVPMatrix * vPosition;" +
          "}"

  // Use to access and set the view transformation
  protected var vPMatrixHandle: Int = 0

  protected val fragmentShaderCode =
      "precision mediump float;" +
          "uniform vec4 vColor;" +
          "void main() {" +
          "  gl_FragColor = vColor;" +
          "}"

  abstract fun draw(mvpMatrix: FloatArray)
}
