package com.github.lookupgroup27.lookup.opengl.dim2

const val COORDS_PER_VERTEX = 3

abstract class Shape {
  abstract val color: FloatArray


  // Vertex shader source code
  protected val vertexShaderCode =
      // This matrix member variable provides a hook to manipulate
      // the coordinates of the objects that use this vertex shader
      "uniform mat4 uMVPMatrix;" +
          "attribute vec4 vPosition;" +
          "attribute vec3 vColor;" +
          "varying vec3 color;" +
          "void main() {" +
          // the matrix must be included as a modifier of gl_Position
          // Note that the uMVPMatrix factor *must be first* in order
          // for the matrix multiplication product to be correct.
          "  gl_Position = uMVPMatrix * vPosition;" +
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

  abstract fun draw(mvpMatrix: FloatArray)
}
