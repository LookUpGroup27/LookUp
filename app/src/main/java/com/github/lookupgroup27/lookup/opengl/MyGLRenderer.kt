package com.github.lookupgroup27.lookup.opengl

import android.opengl.GLES20
import android.opengl.GLSurfaceView
import android.opengl.Matrix
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

class MyGLRenderer : GLSurfaceView.Renderer {

  @Volatile var angle: Float = 0f

  private lateinit var mShape: Shape

  private val vPMatrix = FloatArray(16)
  private val projectionMatrix = FloatArray(16)
  private val viewMatrix = FloatArray(16)
  private val rotationMatrix = FloatArray(16)

  override fun onSurfaceCreated(unused: GL10, config: EGLConfig) {
    // Set the background frame color
    GLES20.glClearColor(0.0f, 0.0f, 0.0f, 1.0f)
    // Initialize a triangle and a square
    mShape = Triangle()
  }

  override fun onDrawFrame(unused: GL10) {
    val scratch = FloatArray(16)

    Matrix.setLookAtM(viewMatrix, 0, 0f, 0f, 3f, 0f, 0f, 0f, 0f, 1f, 0f)

    Matrix.multiplyMM(vPMatrix, 0, projectionMatrix, 0, viewMatrix, 0)

    // Create a rotation transformation for the triangle
    //    val time = System.currentTimeMillis() % 4000L
    //    val angle = 0.090f * time.toInt()
    Matrix.setRotateM(rotationMatrix, 0, angle, 0f, 0f, -1.0f)

    Matrix.multiplyMM(scratch, 0, vPMatrix, 0, rotationMatrix, 0)

    // Redraw background color
    GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT)
    mShape.draw(scratch)
  }

  override fun onSurfaceChanged(unused: GL10, width: Int, height: Int) {
    GLES20.glViewport(0, 0, width, height)

    val ratio: Float = width.toFloat() / height.toFloat()
    Matrix.frustumM(projectionMatrix, 0, -ratio, ratio, -1f, 1f, 3f, 7f)
  }
}

fun loadShader(type: Int, shaderCode: String): Int {
  // create a vertex shader type (GLES20.GL_VERTEX_SHADER)
  // or a fragment shader type (GLES20.GL_FRAGMENT_SHADER)
  return GLES20.glCreateShader(type).also { shader ->

    // add the source code to the shader and compile it
    GLES20.glShaderSource(shader, shaderCode)
    GLES20.glCompileShader(shader)
  }
}
