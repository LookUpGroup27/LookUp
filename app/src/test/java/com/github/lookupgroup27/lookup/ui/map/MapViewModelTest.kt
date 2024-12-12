package com.github.lookupgroup27.lookup.ui.map

import PlanetsRepository
import android.content.Context
import android.content.pm.ActivityInfo
import android.hardware.Sensor
import android.hardware.SensorManager
import androidx.activity.ComponentActivity
import com.github.lookupgroup27.lookup.model.map.stars.StarDataRepository
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.kotlin.whenever

@RunWith(MockitoJUnitRunner::class)
class MapViewModelTest {
  private lateinit var viewModel: MapViewModel

  @Mock private lateinit var mockActivity: ComponentActivity

  @Mock private lateinit var mockSensorManager: SensorManager

  @Mock private lateinit var mockSensor: Sensor

  @Mock private val mockContext: Context = mock()
  @Mock private val mockStarDataRepository: StarDataRepository = mock()
  @Mock private val mockPlanetsRepository: PlanetsRepository = mock()

  @Before
  fun setUp() {
    viewModel = MapViewModel(mockContext, mockStarDataRepository, mockPlanetsRepository)

    // Mock system service retrieval
    whenever(mockActivity.getSystemService(Context.SENSOR_SERVICE)).thenReturn(mockSensorManager)

    whenever(mockSensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR)).thenReturn(mockSensor)
  }

  @After
  fun tearDown() {
    reset(mockActivity, mockSensorManager, mockSensor)
  }

  @Test
  fun testLockScreenOrientation() {
    viewModel.lockScreenOrientation(mockActivity)

    verify(mockActivity).requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
  }

  @Test
  fun testUnlockScreenOrientation() {
    val originalOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
    viewModel.lockScreenOrientation(mockActivity)
    viewModel.unlockScreenOrientation(mockActivity, originalOrientation)

    verify(mockActivity).requestedOrientation = originalOrientation
  }

  @Test
  fun testRegisterSensorListener() {
    viewModel.registerSensorListener(mockActivity)

    verify(mockActivity).getSystemService(Context.SENSOR_SERVICE)
    verify(mockSensorManager).getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR)
    verify(mockSensorManager)
        .registerListener(
            viewModel.mapRenderer.camera, mockSensor, SensorManager.SENSOR_DELAY_NORMAL)
  }

  @Test
  fun testUnregisterSensorListener() {
    viewModel.unregisterSensorListener(mockActivity)

    verify(mockActivity).getSystemService(Context.SENSOR_SERVICE)
    verify(mockSensorManager).unregisterListener(viewModel.mapRenderer.camera)
  }
}
