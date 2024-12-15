package com.github.lookupgroup27.lookup.model.map.renderables.utils

import android.opengl.Matrix
import android.util.Log
import com.github.lookupgroup27.lookup.model.map.Camera
import com.github.lookupgroup27.lookup.model.map.Ray

object RayUtils {
  /**
   * Calculates a ray in world coordinates that starts at the camera position and passes through the
   * specified screen coordinates.
   *
   * @param screenX The x-coordinate of the screen point.
   * @param screenY The y-coordinate of the screen point.
   * @param camera The camera used to calculate the ray.
   * @return The ray in world coordinates.
   */
  fun calculateRay(screenX: Float, screenY: Float, camera: Camera, viewport: IntArray): Ray {
    // Validate viewport dimensions
    if (viewport[2] <= 0 || viewport[3] <= 0) {
      Log.d("RayUtils", "Viewport dimensions are invalid: ${viewport.joinToString()}")
      return Ray(floatArrayOf(0f, 0f, 0f), floatArrayOf(0f, 0f, 0f))
    }

    val projectionMatrix = camera.projMatrix
    val viewMatrix = camera.viewMatrix

    // Convert screen coordinates to normalized device coordinates (NDC)
    val ndcX = (2.0f * screenX) / viewport[2] - 1.0f
    val ndcY = 1.0f - (2.0f * screenY) / viewport[3]

    // Combine projection and view matrices and invert
    val invertedVPMatrix = FloatArray(16)
    val vpMatrix = FloatArray(16)
    Matrix.multiplyMM(vpMatrix, 0, projectionMatrix, 0, viewMatrix, 0)
    if (!Matrix.invertM(invertedVPMatrix, 0, vpMatrix, 0)) {
      Log.d("RayUtils", "Matrix inversion failed")
      return Ray(floatArrayOf(0f, 0f, 0f), floatArrayOf(0f, 0f, 0f))
    }

    // Transform near and far points from NDC to world space
    val nearPoint = floatArrayOf(ndcX, ndcY, -1.0f, 1.0f)
    val worldNearPoint = FloatArray(4)
    Matrix.multiplyMV(worldNearPoint, 0, invertedVPMatrix, 0, nearPoint, 0)

    val farPoint = floatArrayOf(ndcX, ndcY, 1.0f, 1.0f)
    val worldFarPoint = FloatArray(4)
    Matrix.multiplyMV(worldFarPoint, 0, invertedVPMatrix, 0, farPoint, 0)

    // Guard against division by zero
    if (worldNearPoint[3] == 0f || worldFarPoint[3] == 0f) {
      Log.d("RayUtils", "Invalid w component in world points")
      return Ray(floatArrayOf(0f, 0f, 0f), floatArrayOf(0f, 0f, 0f))
    }

    // Convert homogeneous coordinates to 3D
    for (i in 0..2) {
      worldNearPoint[i] /= worldNearPoint[3]
      worldFarPoint[i] /= worldFarPoint[3]
    }

    // Calculate ray direction
    val direction =
        floatArrayOf(
            worldFarPoint[0] - worldNearPoint[0],
            worldFarPoint[1] - worldNearPoint[1],
            worldFarPoint[2] - worldNearPoint[2])
    val magnitude =
        Math.sqrt(
                (direction[0] * direction[0] +
                        direction[1] * direction[1] +
                        direction[2] * direction[2])
                    .toDouble())
            .toFloat()
    if (magnitude == 0f) {
      Log.d("RayUtils", "Ray direction magnitude is zero")
      return Ray(floatArrayOf(0f, 0f, 0f), floatArrayOf(0f, 0f, 0f))
    }

    direction[0] /= magnitude
    direction[1] /= magnitude
    direction[2] /= magnitude

    return Ray(origin = worldNearPoint.copyOfRange(0, 3), direction = direction)
  }
}
