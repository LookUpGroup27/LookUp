package com.github.lookupgroup27.lookup.model.map

import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.opengl.Matrix

/**
 * Represents a camera for handling movement and projection in our OpenGL World.
 *
 * The camera follows the phone orientation. The camera when looking straight in the north direction
 * with the phone in portrait mode is projected as follows:
 *
 *     ↑ Z
 *     │
 *     │
 *   ──┼────────> X
 *
 * The positive Z-axis points upward, the positive X-axis points to the right, and the positive
 * Y-axis points into the screen.
 *
 * @param fov The field of view of the camera in degrees.
 */
class Camera(private var fov: Float) : SensorEventListener {

  val modelMatrix = FloatArray(16)
  val viewMatrix = FloatArray(16)
  val projMatrix = FloatArray(16)
  private var aspectRatio = 16 / 9f

  companion object {
    // Near and far clipping plane constants
    const val NEAR = 0.1f
    const val FAR = 100f
  }

  init {
    Matrix.setIdentityM(modelMatrix, 0)
    Matrix.setIdentityM(viewMatrix, 0)
    Matrix.setIdentityM(projMatrix, 0)
  }

  /** Update the projection matrix based on the aspect ratio of the screen. */
  private fun updateProjectionMatrix() {
    Matrix.perspectiveM(projMatrix, 0, fov, aspectRatio, NEAR, FAR)
  }

  /**
   * Update the projection matrix based on the aspect ratio of the screen.
   *
   * @param ratio The new aspect ratio of the screen.
   */
  fun updateScreenRatio(ratio: Float) {
    this.aspectRatio = ratio
    updateProjectionMatrix()
  }

  /**
   * Update the field of view of the camera.
   *
   * @param fov The new field of view in degrees.
   */
  fun updateFov(fov: Float) {
    this.fov = fov
    updateProjectionMatrix()
  }

  override fun onSensorChanged(event: SensorEvent?) {
    event?.let {
      if (event.sensor.type == Sensor.TYPE_ROTATION_VECTOR) {
        SensorManager.getRotationMatrixFromVector(viewMatrix, event.values)
      }
    }
  }

  override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) = Unit
}
