package com.github.lookupgroup27.lookup.model.map.renderables.utils

import org.junit.Assert.assertArrayEquals
import org.junit.Assert.assertEquals
import org.junit.Test

class GeometryUtilsTest {

  @Test
  fun testGenerateCircularGeometry_vertices() {
    val numSegments = 4
    val geometryData = GeometryUtils.generateCircularGeometry(numSegments)

    // Expected vertices for a 4-segment circle
    val expectedVertices =
        floatArrayOf(
            0f,
            0f,
            0f, // Center vertex
            1f,
            0f,
            0f, // First segment
            0f,
            1f,
            0f, // Second segment
            -1f,
            0f,
            0f, // Third segment
            0f,
            -1f,
            0f, // Fourth segment
            1f,
            0f,
            0f // Wrap around to the first segment
            )

    // Assert that the vertices match
    assertArrayEquals(expectedVertices, geometryData.vertices, 0.001f)
  }

  @Test
  fun testGenerateCircularGeometry_indices() {
    val numSegments = 4
    val geometryData = GeometryUtils.generateCircularGeometry(numSegments)

    // Expected indices for a 4-segment circle
    val expectedIndices =
        shortArrayOf(
            0,
            1,
            2, // Triangle 1
            0,
            2,
            3, // Triangle 2
            0,
            3,
            4, // Triangle 3
            0,
            4,
            1 // Triangle 4 (wrap around)
            )

    // Assert that the indices match
    assertArrayEquals(expectedIndices, geometryData.indices)
  }

  @Test
  fun testGenerateCircularGeometry_numberOfVerticesAndIndices() {
    val numSegments = 32
    val geometryData = GeometryUtils.generateCircularGeometry(numSegments)

    // The number of vertices should be numSegments + 2 (center + one for wraparound)
    assertEquals(numSegments + 2, geometryData.vertices.size / 3)

    // The number of indices should be numSegments * 3 (3 indices per triangle)
    assertEquals(numSegments * 3, geometryData.indices.size)
  }
}
