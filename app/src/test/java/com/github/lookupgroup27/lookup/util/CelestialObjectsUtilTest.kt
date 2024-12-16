package com.github.lookupgroup27.lookup.util

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class CelestialObjectsUtilsTest {

  @Test
  fun `test computeSiderealTime with known longitude`() {
    val longitude = 0.0 // Greenwich longitude
    val siderealTime = CelestialObjectsUtils.computeSiderealTime(longitude)

    // Sidereal time can vary, but we will test for a reasonable range
    assert(siderealTime in 0.0..360.0) {
      "Sidereal time should be between 0 and 360 degrees, but was $siderealTime"
    }
  }

  @Test
  fun `test convertToHorizonCoordinates produces values within tolerated bounds`() {
    // Define test cases with RA, Dec, Latitude, and Local Sidereal Time
    val testCases =
        listOf(
            Triple(0.0, 0.0, 46.0), // RA=0, Dec=0, Latitude=46.0
            Triple(90.0, 0.0, 46.0), // RA=90, Dec=0, Latitude=46.0
            Triple(180.0, 0.0, 46.0), // RA=180, Dec=0, Latitude=46.0
            Triple(0.0, 90.0, 46.0), // RA=0, Dec=90, Latitude=46.0
            Triple(270.0, -45.0, 46.0) // RA=270, Dec=-45, Latitude=46.0
            )

    val siderealTime = 100.0 // Local sidereal time (example value)
    val azimuthBounds = 0.0..360.0 // Azimuth must be within [0, 360)
    val altitudeBounds = -90.0..90.0 // Altitude must be within [-90, 90]

    // Iterate over test cases
    for ((ra, dec, latitude) in testCases) {
      val (azimuth, altitude) =
          CelestialObjectsUtils.convertToHorizonCoordinates(
              ra = ra, dec = dec, latitude = latitude, localSiderealTime = siderealTime)

      // Assert azimuth is within bounds
      assertTrue(
          "Azimuth $azimuth is out of bounds for RA=$ra, Dec=$dec, Latitude=$latitude",
          azimuth in azimuthBounds)

      // Assert altitude is within bounds
      assertTrue(
          "Altitude $altitude is out of bounds for RA=$ra, Dec=$dec, Latitude=$latitude",
          altitude in altitudeBounds)
    }
  }

  /**
   * Temporary test as computeSiderealTime changes values with time emporary test as
   * computeSiderealTime changes values with time
   */

  /*@Test
  fun `test convertToHorizonCoordinates with Stellarium data for Vega`() {
    // Given values derived from Stellarium
    val ra = 279.4380 // Right Ascension in degrees (converted from 18h 37m 45.2s)
    val dec = 38.8079 // Declination in degrees (converted from +38° 48' 28.4")
    val latitude = 46.51853 // Observer's latitude in degrees
    val localSiderealTime = 50

    // Perform the horizon coordinate conversion
    val (calculatedAzimuth, calculatedAltitude) =
        CelestialObjectsUtils.convertToHorizonCoordinates(
            ra = ra,
            dec = dec,
            latitude = latitude,
            localSiderealTime = CelestialObjectsUtils.computeSiderealTime(6.56188))

    // Expected values from Stellarium
    val expectedAzimuth = 283.7021 // Azimuth in degrees
    val expectedAltitude = 44.7768 // Altitude in degrees

    // Allow tolerance for differences
    val tolerance = 15.0

    // Assertions to verify the correctness of the calculated values
    assertEquals(expectedAzimuth, calculatedAzimuth, tolerance)
    assertEquals(expectedAltitude, calculatedAltitude, tolerance)
  }*/

  @Test
  fun `test convertToCartesian for known azimuth and altitude`() {
    val azimuth = 45.0 // Azimuth in degrees
    val altitude = 30.0 // Altitude in degrees

    val (x, y, z) = CelestialObjectsUtils.convertToCartesian(azimuth = azimuth, altitude = altitude)

    // Calculated expected values manually for testing
    val expectedY =
        (100 * Math.cos(Math.toRadians(altitude)) * Math.cos(Math.toRadians(azimuth))).toFloat()
    val expectedX =
        (100 * Math.cos(Math.toRadians(altitude)) * Math.sin(Math.toRadians(azimuth))).toFloat()
    val expectedZ = (100 * Math.sin(Math.toRadians(altitude))).toFloat()

    assertEquals(expectedX, x, 0.01f)
    assertEquals(expectedY, y, 0.01f)
    assertEquals(expectedZ, z, 0.01f)
  }
}
