package com.github.lookupgroup27.lookup.model.map

import android.hardware.Sensor
import android.hardware.SensorManager
import org.junit.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.times
import org.mockito.Mockito.verify

class CameraTest {

  @Test
  fun testOnAccuracyChanged() {
    val sensor = mock(Sensor::class.java)
    val camera = Camera(90f)

    camera.onAccuracyChanged(sensor, SensorManager.SENSOR_STATUS_ACCURACY_HIGH)

    // Since the method does nothing, we just verify that it can be called without exceptions
    verify(sensor, times(0)).type // This is just to use the sensor mock
  }
}
