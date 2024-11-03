package com.github.lookupgroup27.lookup.opengl.dim3

import android.icu.text.Transliterator.Position
import android.opengl.Matrix

class Camera (var position: FloatArray, var width: Int, var height: Int) {
  var orientation: FloatArray = floatArrayOf(0f, 0f, -1f)
  var up: FloatArray = floatArrayOf(0f, 1f, 0f)

  var ratio = width.toFloat() / height.toFloat()

  var speed: Float = 0.1f

  fun Matrix(FOVdeg: Float, nearPlane: Float, farPlane: Float) {
    var view = FloatArray(16)
    var projection = FloatArray(16)
    Matrix.setIdentityM(view, 0)
    Matrix.setIdentityM(projection, 0)

    Matrix.setLookAtM(view, 0, position[0], position[1], position[2], position[0] + orientation[0], position[1] + orientation[1], position[2] + orientation[2], up[0], up[1], up[2])
    Matrix.perspectiveM(projection, 0, FOVdeg, ratio, nearPlane, farPlane)


  }

  fun Inputs() {

  }

}