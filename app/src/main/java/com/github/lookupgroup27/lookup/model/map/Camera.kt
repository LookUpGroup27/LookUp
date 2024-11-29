package com.github.lookupgroup27.lookup.model.map

import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.opengl.Matrix
import android.util.Log
import android.view.ScaleGestureDetector

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
 */
class Camera : SensorEventListener, ScaleGestureDetector.OnScaleGestureListener {

  val modelMatrix = FloatArray(16)
  val viewMatrix = FloatArray(16)
  val projMatrix = FloatArray(16)
  private var fov = DEFAULT_FOV
  private var aspectRatio = DEFAULT_ASPECT_RATIO

  companion object {
    // FOV constants
    const val MAX_FOV = 110f
    const val DEFAULT_FOV = 45f
    const val MIN_FOV = 1f

    // Near and far clipping plane constants
    const val NEAR = 0.1f
    const val FAR = 100f

    // Aspect ratio constants
    const val DEFAULT_ASPECT_RATIO = 1f
  }

  init {
    Matrix.setIdentityM(modelMatrix, 0)
    Matrix.setIdentityM(viewMatrix, 0)
    Matrix.setIdentityM(projMatrix, 0)
  }

  /** Update the projection matrix based on the aspect ratio of the screen. */
  fun updateProjectionMatrix(ratio: Float) {
    this.aspectRatio = ratio
    Matrix.perspectiveM(projMatrix, 0, fov, aspectRatio, NEAR, FAR)
  }

  override fun onSensorChanged(event: SensorEvent?) {
    event?.let {
      if (event.sensor.type == Sensor.TYPE_ROTATION_VECTOR) {
        SensorManager.getRotationMatrixFromVector(viewMatrix, event.values)
      }
    }
  }

  override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
    when (sensor?.type) {
      Sensor.TYPE_ROTATION_VECTOR -> {
        when (accuracy) {
          SensorManager.SENSOR_STATUS_UNRELIABLE -> {
            // TODO : Provide a warning about unreliable sensor data
            Log.w("SensorAccuracy", "Rotation vector sensor is unreliable")
          }
          SensorManager.SENSOR_STATUS_ACCURACY_LOW -> {
            Log.i("SensorAccuracy", "Rotation vector sensor accuracy is low")
          }
          SensorManager.SENSOR_STATUS_ACCURACY_HIGH -> {
            Log.d("SensorAccuracy", "Rotation vector sensor accuracy is high")
          }
        }
      }
    }
  }

  override fun onScale(detector: ScaleGestureDetector): Boolean {
    val scaleFactor = detector.scaleFactor
    fov = (fov / scaleFactor).coerceIn(MIN_FOV, MAX_FOV)
    Matrix.perspectiveM(projMatrix, 0, fov, aspectRatio, NEAR, FAR)

    return true
  }

  override fun onScaleBegin(detector: ScaleGestureDetector): Boolean {
    return true
  }

  override fun onScaleEnd(detector: ScaleGestureDetector) {
    // No-op
  }
}
