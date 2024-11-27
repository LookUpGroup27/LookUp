package com.github.lookupgroup27.lookup.ui.map

import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.ActivityInfo
import android.hardware.Sensor
import android.hardware.SensorManager
import androidx.activity.ComponentActivity
import androidx.lifecycle.ViewModel
import com.github.lookupgroup27.lookup.model.map.MapRenderer

/** The ViewModel for the map screen. */
class MapViewModel : ViewModel() {
  val mapRenderer = MapRenderer()

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
}
