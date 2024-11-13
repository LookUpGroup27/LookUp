package com.github.lookupgroup27.lookup.util

import org.junit.Assert.assertEquals
import org.junit.Test

class LocationUtilsTest {

  @Test
  fun `calculateDistance returns zero for same coordinates`() {
    val distance =
        LocationUtils.calculateDistance(
            startLat = 37.7749, startLng = -122.4194, endLat = 37.7749, endLng = -122.4194)
    assertEquals(0.0, distance, 0.0)
  }

  @Test
  fun `calculateDistance returns correct distance for known locations`() {
    // Distance between San Francisco (37.7749, -122.4194) and Los Angeles (34.0522, -118.2437)
    val distance =
        LocationUtils.calculateDistance(
            startLat = 37.7749, startLng = -122.4194, endLat = 34.0522, endLng = -118.2437)
    assertEquals(559.0, distance, 1.0) // Expected ~559 km, allowing a small margin of error
  }

  @Test
  fun `calculateDistance works with coordinates at extreme latitudes`() {
    // Test near the North Pole
    val distance =
        LocationUtils.calculateDistance(
            startLat = 89.9999, startLng = 0.0, endLat = -89.9999, endLng = 0.0)
    assertEquals(20015.0, distance, 10.0) // Half the Earth's circumference, ~20,015 km

    // Test near the equator
    val equatorDistance =
        LocationUtils.calculateDistance(
            startLat = 0.0, startLng = 0.0, endLat = 0.0, endLng = 180.0)
    assertEquals(20015.0, equatorDistance, 10.0) // Half the Earth's circumference
  }

  @Test
  fun `calculateDistance handles large distances accurately`() {
    // Distance between two nearly antipodal points
    val distance =
        LocationUtils.calculateDistance(
            startLat = 37.7749, startLng = -122.4194, endLat = -37.7749, endLng = 57.5806)
    assertEquals(20000.0, distance, 500.0) // Roughly halfway around the Earth
  }

  @Test
  fun `calculateDistance returns accurate small distances`() {
    // Very close coordinates, expect a small distance result
    val distance =
        LocationUtils.calculateDistance(
            startLat = 37.7749, startLng = -122.4194, endLat = 37.7750, endLng = -122.4195)
    assertEquals(0.014, distance, 0.001) // Approx 14 meters in kilometers
  }
}
