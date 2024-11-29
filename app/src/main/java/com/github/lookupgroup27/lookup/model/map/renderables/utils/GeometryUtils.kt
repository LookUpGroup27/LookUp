package com.github.lookupgroup27.lookup.model.map.renderables.utils

import kotlin.math.cos
import kotlin.math.sin

/**
 * Utility object for generating spherical geometry. This class provides methods for creating vertex
 * data, texture coordinates, and indices for rendering a 3D sphere using OpenGL.
 *
 * The sphere is tessellated into latitude bands and longitude steps, which control the level of
 * detail and smoothness of the sphere.
 */
object GeometryUtils {

  /**
   * Generates geometry data for a circular shape, tessellated into triangle segments.
   *
   * @param numSegments The number of triangle segments used to form the circle.
   * @return GeometryData containing vertices and indices for rendering the circle.
   */
  fun generateCircularGeometry(numSegments: Int = 32): GeometryData {
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
    for (i in 1..numSegments) {
      indices.add(0) // Center vertex
      indices.add(i.toShort())
      indices.add((i % numSegments + 1).toShort())
    }

    return GeometryData(
        vertices = vertices.toFloatArray(),
        textureCoords = FloatArray(0),
        indices = indices.toShortArray())
  }

    fun generateSphericalGeometry(numBands: Int = 20, stepsPerBand: Int = 28): GeometryData {
        val vertices = mutableListOf<Float>()
        val texCoords = mutableListOf<Float>()
        val indices = mutableListOf<Short>()

        val sinAngles = FloatArray(stepsPerBand)
        val cosAngles = FloatArray(stepsPerBand)

        // Calculate angles for a circular band
        val angleStep = 2.0f * Math.PI.toFloat() / (stepsPerBand - 1)
        var angle = 0f
        for (i in sinAngles.indices) {
            sinAngles[i] = Math.sin(angle.toDouble()).toFloat()
            cosAngles[i] = Math.cos(angle.toDouble()).toFloat()
            angle += angleStep
        }

        val bandStep = 2.0f / (numBands - 1)
        var bandPos = 1f

        // Generate vertices
        for (band in 0 until numBands) {

            val sinPhi = if (bandPos > -1) Math.sqrt(1 - bandPos * bandPos.toDouble()).toFloat() else 0f

            val v = (band + 1f) / numBands // Latitude for texture coordinates
            for (i in 0 until stepsPerBand) {
                val u = i.toFloat() / (stepsPerBand - 1) // Longitude for texture coordinates

                vertices.addAll(listOf(cosAngles[i] * sinPhi, bandPos, sinAngles[i] * sinPhi))
                texCoords.addAll(listOf(u, v))
            }

            bandPos -= bandStep
        }

        // Generate indices
        var topBandStart = 0
        var bottomBandStart = stepsPerBand

        for (band in 0 until numBands - 1) {
            for (i in 0 until stepsPerBand - 1) {
                val topLeft = (topBandStart + i).toShort()
                val topRight = (topLeft + 1).toShort()
                val bottomLeft = (bottomBandStart + i).toShort()
                val bottomRight = (bottomLeft + 1).toShort()

                // First triangle
                indices.addAll(listOf(topLeft, bottomRight, bottomLeft))

                // Second triangle
                indices.addAll(listOf(topRight, bottomRight, topLeft))
            }

            // Close the circular band
            indices.addAll(
                listOf(
                    (topBandStart + stepsPerBand - 1).toShort(),
                    bottomBandStart.toShort(),
                    (bottomBandStart + stepsPerBand - 1).toShort(),
                    topBandStart.toShort(),
                    bottomBandStart.toShort(),
                    (topBandStart + stepsPerBand - 1).toShort()))

            topBandStart += stepsPerBand
            bottomBandStart += stepsPerBand
        }

        return GeometryData(vertices.toFloatArray(), texCoords.toFloatArray(), indices.toShortArray())
    }

  /** Data class to hold geometry generation results */
  data class GeometryData(
      val vertices: FloatArray,
      val textureCoords: FloatArray,
      val indices: ShortArray
  )
}
