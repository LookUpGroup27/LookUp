package com.github.lookupgroup27.lookup.ui.map

import PlanetsRepository
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.ActivityInfo
import android.hardware.Sensor
import android.hardware.SensorManager
import android.view.ScaleGestureDetector
import androidx.activity.ComponentActivity
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.github.lookupgroup27.lookup.model.location.LocationProviderSingleton
import com.github.lookupgroup27.lookup.model.map.MapRenderer
import com.github.lookupgroup27.lookup.model.map.stars.StarDataRepository

// FOV constants
const val DEFAULT_FOV = 45f
const val MAX_FOV = DEFAULT_FOV + 40f
const val MIN_FOV = DEFAULT_FOV - 40f

/** The ViewModel for the map screen. */
class MapViewModel(
    context: Context,
    starDataRepository: StarDataRepository,
    planetsRepository: PlanetsRepository
) : ViewModel(), ScaleGestureDetector.OnScaleGestureListener {

  private var _fov by mutableFloatStateOf(DEFAULT_FOV)

  val fov: Float
    get() = _fov

  /** The zoom level of the camera as a percentage. */
  val zoomPercentage: Float
    get() = 100 - (_fov - MIN_FOV) / (MAX_FOV - MIN_FOV) * 100

  val mapRenderer = MapRenderer(context, starDataRepository, planetsRepository, fov)

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
   * Updates the zoom level of the camera.
   *
   * @param percentage the new zoom level as a percentage
   */
  fun updateZoom(percentage: Float) {
    updateFov(MIN_FOV + (MAX_FOV - MIN_FOV) * (100 - percentage) / 100)
  }

  /**
   * Updates the field of view of the camera.
   *
   * @param fov the new field of view
   */
  fun updateFov(fov: Float) {
    _fov = fov.coerceIn(MIN_FOV, MAX_FOV)
    mapRenderer.camera.updateFov(_fov)
  }

  override fun onScale(detector: ScaleGestureDetector): Boolean {
    updateFov((fov / detector.scaleFactor))
    return true
  }

  override fun onScaleBegin(detector: ScaleGestureDetector): Boolean {
    return true
  }

  override fun onScaleEnd(detector: ScaleGestureDetector) {
    // No-op
  }

  /** Companion object that provides a Factory for [MapViewModel]. */
  companion object {

    const val DEFAULT_FOV = 45f
    const val MAX_FOV = DEFAULT_FOV + 40f
    const val MIN_FOV = DEFAULT_FOV - 40f

    fun createFactory(context: Context): ViewModelProvider.Factory =
        object : ViewModelProvider.Factory {
          @Suppress("UNCHECKED_CAST")
          override fun <T : ViewModel> create(modelClass: Class<T>): T {

            // Initialize repositories
            val locationProvider = LocationProviderSingleton.getInstance(context)

            val starDataRepository = StarDataRepository(context, locationProvider)
            val planetsRepository = PlanetsRepository(locationProvider)

            // Return MapViewModel instance
            return MapViewModel(context, starDataRepository, planetsRepository) as T
          }
        }
  }
}
