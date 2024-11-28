package com.github.lookupgroup27.lookup.model.map.renderables.utils

import kotlin.math.cos
import kotlin.math.sin

object GeometryUtils {

  /**
   * Generates geometry data for a circular shape, tessellated into triangle segments.
   *
   * @param numSegments The number of triangle segments used to form the circle.
   * @return GeometryData containing vertices and indices for rendering the circle.
   */
  fun generateCircularGeometry(numSegments: Int = 36): GeometryData {
    val vertices = mutableListOf<Float>()
    val indices = mutableListOf<Short>()

    // Center vertex for the circle
    vertices.addAll(listOf(0f, 0f, 0f))

    // Generate circle vertices
    val angleStep = (2 * Math.PI / numSegments).toFloat()
    for (i in 0..numSegments) {
      val angle = i * angleStep
      vertices.addAll(listOf(cos(angle.toDouble()).toFloat(), sin(angle.toDouble()).toFloat(), 0f))
    }

    // Generate indices for triangle fan
    for (i in 1 until numSegments) {
      indices.addAll(listOf(0, i.toShort(), (i + 1).toShort()))
    }
    indices.addAll(listOf(0, numSegments.toShort(), 1)) // Close the circle

    return GeometryData(
        vertices = vertices.toFloatArray(),
        textureCoords = FloatArray(0), // No texture for a flat circle
        indices = indices.toShortArray())
  }

  /** Data class to hold geometry generation results */
  data class GeometryData(
      val vertices: FloatArray,
      val textureCoords: FloatArray,
      val indices: ShortArray
  )
}
