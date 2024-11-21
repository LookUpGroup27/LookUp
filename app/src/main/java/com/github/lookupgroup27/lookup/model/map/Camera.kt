package com.github.lookupgroup27.lookup.model.map

import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.opengl.Matrix
import android.util.Log

/**
 * Represents a camera for handling movement and projection in our OpenGL World.
 *
 * The camera follows the phone orientation.
 * The camera when looking straight in the north direction is projected as follows:
 *
 *     ↑ Z
 *     │
 *     │
 *   ──┼────────> X
 *
 * The positive Z-axis points upward, the positive X-axis points to the right, and
 * the positive Y-axis points into the screen.
 */
class Camera : SensorEventListener {

  val modelMatrix = FloatArray(16)
  val viewMatrix = FloatArray(16)
  val projMatrix = FloatArray(16)

  companion object {
    const val FOV = 45f
    const val NEAR = 0.1f
    const val FAR = 100f
  }

  init {
    Matrix.setIdentityM(modelMatrix, 0)
    Matrix.setIdentityM(viewMatrix, 0)
    Matrix.setIdentityM(projMatrix, 0)
  }

  /** Update the projection matrix based on the aspect ratio of the screen. */
  fun updateProjectionMatrix(ratio: Float) {
    Matrix.perspectiveM(projMatrix, 0, FOV, ratio, NEAR, FAR)
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
}
