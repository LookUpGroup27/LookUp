package com.github.lookupgroup27.lookup.model.map.stars

import android.content.Context
import com.github.lookupgroup27.lookup.model.location.LocationProvider
import com.github.lookupgroup27.lookup.utils.CelestialObjectsUtils

/**
 * Repository class to manage star data, including fetching, location updates, and position
 * calculations.
 */
class StarDataRepository(
    private val context: Context,
    private val locationProvider: LocationProvider
) {
  companion object {
    private const val STAR_FILE_PATH = "stars/"
    private const val NAME_INDEX = 6
    private const val RA_INDEX = 7
    private const val DEC_INDEX = 8
    private const val DIST_INDEX = 9
    private const val X_INDEX = 17
    private const val Y_INDEX = 18
    private const val Z_INDEX = 19
    private const val MAGNITUDE_INDEX = 13
    private const val SPECTRAL_CLASS_INDEX = 15
  }

  private val stars = mutableListOf<StarData>()
  private var isSkyMapLoaded = false

  init {
    // Start location updates when the repository is created
    locationProvider.requestLocationUpdates()
  }

  /**
   * Loads star data from a CSV file and converts it into a list of [StarData] objects.
   *
   * @param fileName The name of the CSV file to load from the assets directory.
   */
  fun loadStarsFromCSV(fileName: String) {
    require(fileName.isNotBlank()) { "File name must not be blank" }
    require(fileName.endsWith(".csv")) { "File name must be a .csv file" }

    stars.clear()
    val inputStream = context.assets.open(STAR_FILE_PATH + fileName)
    val reader = inputStream.bufferedReader()

    reader.useLines { lines ->
      lines.drop(1).forEach { line ->
        val values = line.split(",")

        if (values.size > maxOf(RA_INDEX, DEC_INDEX, DIST_INDEX, X_INDEX, Y_INDEX, Z_INDEX)) {
          val name = values.getOrNull(NAME_INDEX)
          val ra = values[RA_INDEX].toDoubleOrNull() ?: 0.0
          val dec = values[DEC_INDEX].toDoubleOrNull() ?: 0.0
          val dist = values[DIST_INDEX].toDoubleOrNull() ?: 0.0
          val x = values[X_INDEX].toDoubleOrNull() ?: 0.0
          val y = values[Y_INDEX].toDoubleOrNull() ?: 0.0
          val z = values[Z_INDEX].toDoubleOrNull() ?: 0.0
          val magnitude = values[MAGNITUDE_INDEX].toDoubleOrNull() ?: 0.0
          val spectralClass = values.getOrNull(SPECTRAL_CLASS_INDEX)

          stars.add(
              StarData(
                  name = name,
                  ra = ra,
                  dec = dec,
                  dist = dist,
                  x = x,
                  y = y,
                  z = z,
                  magnitude = magnitude,
                  spectralClass = spectralClass))
        }
      }
    }
  }

  /**
   * Updates the Cartesian positions of all stars based on the current location and time. This
   * function uses the observer's location and calculates the stars' positions accordingly.
   */
  fun updateStarPositions() {
    val location = locationProvider.currentLocation.value

    if (location != null) {
      val latitude = location.latitude
      val longitude = location.longitude
      val siderealTime = CelestialObjectsUtils.computeSiderealTime(longitude)

      stars.forEach { star ->
        // Convert RA/Dec to horizon coordinates based on the observer's location
        val (azimuth, altitude) =
            CelestialObjectsUtils.convertToHorizonCoordinates(
                ra = star.ra, dec = star.dec, latitude = latitude, localSiderealTime = siderealTime)

        // Update the star's Cartesian position using horizon coordinates
        val updatedCartesianPosition =
            CelestialObjectsUtils.convertToCartesian(azimuth, altitude, star.dist)
        star.x = updatedCartesianPosition.first.toDouble()
        star.y = updatedCartesianPosition.second.toDouble()
        star.z = updatedCartesianPosition.third.toDouble()
      }
    }
  }

  /**
   * Returns the list of stars with their updated Cartesian positions.
   *
   * @return A list of [StarData] objects.
   */
  fun getUpdatedStars(): List<StarData> {
    return stars
  }
}
