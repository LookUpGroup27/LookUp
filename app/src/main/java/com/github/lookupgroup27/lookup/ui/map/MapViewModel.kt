package com.github.lookupgroup27.lookup.ui.map

import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.ActivityInfo
import android.hardware.Sensor
import android.hardware.SensorManager
import android.view.ScaleGestureDetector
import androidx.activity.ComponentActivity
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.github.lookupgroup27.lookup.model.map.Camera
import com.github.lookupgroup27.lookup.model.map.MapRenderer

/** The ViewModel for the map screen. */
class MapViewModel : ViewModel(), ScaleGestureDetector.OnScaleGestureListener {
  private var _fov by mutableStateOf(Camera.DEFAULT_FOV)
  val fov: Float
    get() = _fov

  val mapRenderer = MapRenderer(fov)

  /**
   * Locks the screen orientation to portrait mode.
   *
   * @param activity the activity to lock the screen orientation for
   */
  @SuppressLint("SourceLockedOrientationActivity")
  fun lockScreenOrientation(activity: ComponentActivity) {
    activity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
  }

  /**
   * Unlocks the screen orientation.
   *
   * @param activity the activity to unlock the screen orientation for
   * @param originalOrientation the original orientation of the screen before it was locked
   */
  fun unlockScreenOrientation(activity: ComponentActivity, originalOrientation: Int) {
    activity.requestedOrientation = originalOrientation
  }

  /**
   * Registers a sensor listener to the activity to control the camera orientation.
   *
   * @param activity the activity to register the sensor listener for
   */
  fun registerSensorListener(activity: ComponentActivity) {
    val sensorManager = activity.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    val orientation = sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR)
    sensorManager.registerListener(
        mapRenderer.camera, orientation, SensorManager.SENSOR_DELAY_NORMAL)
  }

  /**
   * Unregisters the sensor listener from the activity.
   *
   * @param activity the activity to unregister the sensor listener from
   */
  fun unregisterSensorListener(activity: ComponentActivity) {
    val sensorManager = activity.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    sensorManager.unregisterListener(mapRenderer.camera)
  }

  /**
   * Updates the field of view of the camera.
   *
   * @param fov the new field of view
   */
  fun updateFov(fov: Float) {
    _fov = fov
    mapRenderer.camera.updateFov(_fov)
  }

  override fun onScale(detector: ScaleGestureDetector): Boolean {
    updateFov((fov / detector.scaleFactor).coerceIn(Camera.MIN_FOV, Camera.MAX_FOV))
    return true
  }

  override fun onScaleBegin(detector: ScaleGestureDetector): Boolean {
    return true
  }

  override fun onScaleEnd(detector: ScaleGestureDetector) {
    // No-op
  }
}
