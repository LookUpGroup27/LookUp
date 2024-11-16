package com.github.lookupgroup27.lookup.opengl

import android.opengl.Matrix

class Camera {
  fun updateProjectionMatrix(ratio: Float) {
    Matrix.perspectiveM(projMatrix, 0, 45f, ratio, 0.1f, 100f)
  }

  private val modelMatrix = FloatArray(16)
  private val viewMatrix = FloatArray(16)
  private val projMatrix = FloatArray(16)

  init {
    Matrix.setIdentityM(modelMatrix, 0)
    Matrix.setIdentityM(viewMatrix, 0)
    Matrix.setIdentityM(projMatrix, 0)
    Matrix.translateM(viewMatrix, 0, 0f, 0f, -2.0f)
  }

  fun turnLeft() {
    Matrix.rotateM(projMatrix, 0, 1f, 0f, -1f, 0f)
  }

  fun turnRight() {
    Matrix.rotateM(projMatrix, 0, 1f, 0f, 1f, 0f)
  }

  fun turnUp() {
    Matrix.rotateM(projMatrix, 0, 1f, -1f, 0f, 0f)
  }

  fun turnDown() {
    Matrix.rotateM(projMatrix, 0, 1f, 1f, 0f, 0f)
  }

  fun tiltLeft() {
    Matrix.rotateM(projMatrix, 0, 1f, 0f, 0f, -1f)
  }

  fun tiltRight() {
    Matrix.rotateM(projMatrix, 0, 1f, 0f, 0f, 1f)
  }

  fun getModelMatrix(): FloatArray {
    return modelMatrix
  }

  fun getViewMatrix(): FloatArray {
    return viewMatrix
  }

  fun getProjMatrix(): FloatArray {
    return projMatrix
  }
}
