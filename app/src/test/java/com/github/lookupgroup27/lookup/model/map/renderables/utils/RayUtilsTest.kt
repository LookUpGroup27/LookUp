package com.github.lookupgroup27.lookup.model.map.renderables.utils

import android.opengl.Matrix
import com.github.lookupgroup27.lookup.model.map.Camera
import com.github.lookupgroup27.lookup.model.map.Ray
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

  @Test
  fun `calculateRay should handle invalid homogeneous coordinates`() {
    // Arrange
    val viewport = intArrayOf(0, 0, 1080, 1920)
    val camera =
        Camera(fov = 45f).apply {
          val ratio = viewport[2].toFloat() / viewport[3]
          Matrix.frustumM(projMatrix, 0, -ratio, ratio, -1f, 1f, 1f, 10f)
          Matrix.setIdentityM(viewMatrix, 0)
        }

    // Act - Modify camera to create an invalid invertedVPMatrix with w=0
    val invalidViewport = intArrayOf(0, 0, 1080, 0) // Height of zero invalidates projection
    val ray = RayUtils.calculateRay(540f, 960f, camera, invalidViewport)

    // Assert - Should return a default ray due to invalid homogeneous coordinates
    val expectedRay = Ray(floatArrayOf(0f, 0f, 0f), floatArrayOf(0f, 0f, 0f))
    assertArrayEquals("Ray origin should be default", expectedRay.origin, ray.origin, 1e-6f)
    assertArrayEquals(
        "Ray direction should be default", expectedRay.direction, ray.direction, 1e-6f)
  }

  @Test
  fun `calculateRay should handle zero magnitude direction`() {
    // Arrange
    val viewport = intArrayOf(0, 0, 1080, 1920)
    val camera =
        Camera(fov = 45f).apply {
          val ratio = viewport[2].toFloat() / viewport[3]
          Matrix.frustumM(projMatrix, 0, -ratio, ratio, -1f, 1f, 1f, 10f)
          Matrix.setIdentityM(viewMatrix, 0)
        }

    // Act - Simulate ray direction with zero magnitude
    val ray = RayUtils.calculateRay(540f, 960f, camera, viewport)

    // Assert - Should gracefully handle division by zero and return default ray
    assertArrayEquals("Ray origin should be default", floatArrayOf(0f, 0f, 0f), ray.origin, 1e-6f)
    assertArrayEquals(
        "Ray direction should be default", floatArrayOf(0f, 0f, 0f), ray.direction, 1e-6f)
  }
}
