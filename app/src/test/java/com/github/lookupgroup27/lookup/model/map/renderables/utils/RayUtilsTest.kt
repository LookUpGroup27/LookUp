package com.github.lookupgroup27.lookup.model.map.renderables.utils

import android.opengl.Matrix
import com.github.lookupgroup27.lookup.model.map.Camera
import org.junit.Assert.assertArrayEquals
import org.junit.Test

class RayUtilsTest {

  @Test
  fun `calculateRay should handle edge screen coordinates2`() {
    val viewport = intArrayOf(0, 0, 1080, 1920)
    val camera =
        Camera(fov = 45f).apply {
          val ratio = viewport[2].toFloat() / viewport[3]
          Matrix.frustumM(projMatrix, 0, -ratio, ratio, -1f, 1f, 1f, 10f)
          Matrix.setIdentityM(viewMatrix, 0)
        }

    val topLeftRay = RayUtils.calculateRay(0f, 0f, camera, viewport)
    val bottomRightRay = RayUtils.calculateRay(1080f, 1920f, camera, viewport)

    assertArrayEquals(floatArrayOf(0f, 0f, 0f), topLeftRay.origin, 1e-6f)
    assertArrayEquals(floatArrayOf(0f, 0f, 0f), bottomRightRay.origin, 1e-6f)
  }

  @Test
  fun `calculateRay should handle edge screen coordinates`() {
    // Arrange
    val viewport = intArrayOf(0, 0, 1080, 1920)
    val camera =
        Camera(fov = 45f).apply {
          val ratio = viewport[2].toFloat() / viewport[3]
          Matrix.frustumM(projMatrix, 0, -ratio, ratio, -1f, 1f, 1f, 10f)
          Matrix.setIdentityM(viewMatrix, 0)
        }

    // Act
    val topLeftRay = RayUtils.calculateRay(0f, 0f, camera, viewport)
    val bottomRightRay = RayUtils.calculateRay(1080f, 1920f, camera, viewport)

    // Assert
    assertArrayEquals(
        "Top-left ray origin should be correct", floatArrayOf(0f, 0f, 0f), topLeftRay.origin, 1e-6f)
    assertArrayEquals(
        "Bottom-right ray origin should be correct",
        floatArrayOf(0f, 0f, 0f),
        bottomRightRay.origin,
        1e-6f)
  }
}
