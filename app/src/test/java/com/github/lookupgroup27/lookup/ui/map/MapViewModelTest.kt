package com.github.lookupgroup27.lookup.ui.map

import PlanetsRepository
import android.content.Context
import android.content.pm.ActivityInfo
import android.hardware.Sensor
import android.hardware.SensorManager
import android.view.ScaleGestureDetector
import androidx.activity.ComponentActivity
import com.github.lookupgroup27.lookup.model.map.stars.StarDataRepository
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertTrue
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

  @Test
  fun `updateFov works correctly on valid values`() {
    // Test the value DEFAULT_FOV
    viewModel.updateFov(MapViewModel.DEFAULT_FOV)
    assertEquals(MapViewModel.DEFAULT_FOV, viewModel.fov)

    // Test a range of valid values
    val fovs = MapViewModel.MIN_FOV.toInt()..MapViewModel.MAX_FOV.toInt() step 10
    for (fov in fovs) {
      viewModel.updateFov(fov.toFloat())
      assertEquals(fov.toFloat(), viewModel.fov)
    }
  }

  @Test
  fun `updateFov works correctly on invalid values`() {
    // Test a range of invalid values
    val fovs = listOf(MapViewModel.MIN_FOV.toInt() - 1, MapViewModel.MAX_FOV.toInt() + 1, 0)
    val expectedFov = listOf(MapViewModel.MIN_FOV, MapViewModel.MAX_FOV, MapViewModel.MIN_FOV)
    for (fov in fovs) {
      viewModel.updateFov(fov.toFloat())
      assertEquals(expectedFov[fovs.indexOf(fov)], viewModel.fov)
    }
  }

  @Test
  fun `onScale should update FOV based on scaleFactor`() {
    val initialFov = MapViewModel.DEFAULT_FOV
    val scaleFactor = 1.5f
    val detector = mock(ScaleGestureDetector::class.java)
    `when`(detector.scaleFactor).thenReturn(scaleFactor)

    viewModel.onScale(detector)

    val expectedFov =
        (initialFov / scaleFactor).coerceIn(MapViewModel.MIN_FOV, MapViewModel.MAX_FOV)
    assert(expectedFov == viewModel.fov)
  }

  @Test
  fun `onScaleBegin should always return true`() {
    val detector = mock(ScaleGestureDetector::class.java)
    val result = viewModel.onScaleBegin(detector)
    assertTrue(result)
  }

  @Test
  fun `onScaleEnd should perform no operation`() {
    val detector = mock(ScaleGestureDetector::class.java)
    viewModel.onScaleEnd(detector)
    verifyNoInteractions(detector)
  }
}
