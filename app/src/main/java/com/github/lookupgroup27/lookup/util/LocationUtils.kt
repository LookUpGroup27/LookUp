package com.github.lookupgroup27.lookup.util

import kotlin.math.*

/** Utility object for location-related calculations. */
object LocationUtils {

  /**
   * Calculates the distance in kilometers between two points specified by latitude and longitude
   * using the Haversine formula.
   *
   * @param startLat Latitude of the starting point
   * @param startLng Longitude of the starting point
   * @param endLat Latitude of the destination point
   * @param endLng Longitude of the destination point
   * @return Distance in kilometers
   */
  fun calculateDistance(
      startLat: Double,
      startLng: Double,
      endLat: Double,
      endLng: Double
  ): Double {
    val earthRadius = 6371.0 // Radius of the Earth in kilometers

    // Convert latitude and longitude differences to radians
    val dLat = Math.toRadians(endLat - startLat)
    val dLng = Math.toRadians(endLng - startLng)

    // Apply the Haversine formula
    val a =
        sin(dLat / 2).pow(2) +
            cos(Math.toRadians(startLat)) * cos(Math.toRadians(endLat)) * sin(dLng / 2).pow(2)
    val c = 2 * atan2(sqrt(a), sqrt(1 - a))

    return earthRadius * c // Distance in kilometers
  }
}
