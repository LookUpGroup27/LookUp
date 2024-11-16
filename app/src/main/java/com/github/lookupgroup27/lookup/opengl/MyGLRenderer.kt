package com.github.lookupgroup27.lookup.opengl

import android.opengl.GLES20
import android.opengl.GLSurfaceView
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

class MyGLRenderer : GLSurfaceView.Renderer {

  private lateinit var mShape: Star
  val camera = Camera()

  override fun onSurfaceCreated(unused: GL10, config: EGLConfig) {
    // Set the background frame color
    GLES20.glClearColor(0.0f, 0.0f, 0.0f, 1.0f)

    GLES20.glEnable(GLES20.GL_DEPTH_TEST)
    mShape = Star()
  }

  override fun onDrawFrame(unused: GL10) {
    // Redraw background color
    GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT)
    GLES20.glClear(GLES20.GL_DEPTH_BUFFER_BIT)
    mShape.draw(camera)
  }

  override fun onSurfaceChanged(unused: GL10, width: Int, height: Int) {
    // Specify the size of the rendering window
    GLES20.glViewport(0, 0, width, height)

    val ratio: Float = width.toFloat() / height.toFloat()
    // Defines a projection matrix in terms of a field of view angle, an aspect ratio, and z clip
    // planes
    // It helps projects in correct aspect ratio the objects in the scene
    camera.updateProjectionMatrix(ratio)
  }
}

fun loadShader(type: Int, shaderCode: String): Int {
  // create a vertex shader type (GLES20.GL_VERTEX_SHADER)
  // or a fragment shader type (GLES20.GL_FRAGMENT_SHADER)
  return GLES20.glCreateShader(type).also { shader ->

    // add the source code to the shader and compile it
    GLES20.glShaderSource(shader, shaderCode)
    GLES20.glCompileShader(shader)
    val compileStatus = IntArray(1)
    GLES20.glGetShaderiv(shader, GLES20.GL_COMPILE_STATUS, compileStatus, 0)
    if (compileStatus[0] == 0) {
      GLES20.glGetShaderInfoLog(shader)?.let { log ->
        android.util.Log.e("ShaderError", "Error compiling shader: $log")
      }
      GLES20.glDeleteShader(shader)
    }
  }
}
