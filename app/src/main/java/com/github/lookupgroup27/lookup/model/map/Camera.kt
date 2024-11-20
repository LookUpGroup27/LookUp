package com.github.lookupgroup27.lookup.model.map

import android.opengl.Matrix

/**
 * Represents a camera for handling movement and projection in our OpenGL World.
 *
 * The camera is initialized at the origin of the world (0, 0, 0), facing along the negative Z-axis.
 * The coordinate system is defined as:
 *
 *     ↑ Y
 *     │
 *     │
 *   ──┼────────> X
 *
 * The positive Y-axis points upward, the positive X-axis points to the right, and the Z-axis points
 * towards the viewer (into the screen).
 */
class Camera {

  val modelMatrix = FloatArray(16)
  val viewMatrix = FloatArray(16)
  val projMatrix = FloatArray(16)

  init {
    Matrix.setIdentityM(modelMatrix, 0)
    Matrix.setIdentityM(viewMatrix, 0)
    Matrix.setIdentityM(projMatrix, 0)
    Matrix.translateM(viewMatrix, 0, 0f, 0f, 0f)
  }

  /** Update the projection matrix based on the aspect ratio of the screen. */
  fun updateProjectionMatrix(ratio: Float) {
    Matrix.perspectiveM(projMatrix, 0, 45f, ratio, 0.1f, 100f)
  }

  /**
   * Set the camera's rotation.
   *
   * @param x the x-axis angle of the camera
   * @param y the y-axis angle of the camera
   * @param z the z-axis angle of the camera
   */
  fun setRotation(x: Float, y: Float, z: Float) {
    Matrix.setIdentityM(modelMatrix, 0)
    Matrix.setRotateM(modelMatrix, 0, x, 1f, 0f, 0f)
    Matrix.rotateM(modelMatrix, 0, y, 0f, 1f, 0f)
    Matrix.rotateM(modelMatrix, 0, z, 0f, 0f, 1f)
  }

  /** Move the camera to the left. */
  fun turnLeft() {
    Matrix.rotateM(viewMatrix, 0, 1f, 0f, -1f, 0f)
  }

  /** Move the camera to the right. */
  fun turnRight() {
    Matrix.rotateM(viewMatrix, 0, 1f, 0f, 1f, 0f)
  }

  /** Move the camera up. */
  fun turnUp() {
    Matrix.rotateM(viewMatrix, 0, 1f, -1f, 0f, 0f)
  }

  /** Move the camera down. */
  fun turnDown() {
    Matrix.rotateM(viewMatrix, 0, 1f, 1f, 0f, 0f)
  }

  /** Tilts the camera to the left. */
  fun tiltLeft() {
    Matrix.rotateM(viewMatrix, 0, 1f, 0f, 0f, -1f)
  }

  /** Tilts the camera to the right. */
  fun tiltRight() {
    Matrix.rotateM(viewMatrix, 0, 1f, 0f, 0f, 1f)
  }
}
