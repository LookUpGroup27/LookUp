package com.github.lookupgroup27.lookup.model.map.stars

import android.content.Context
import com.github.lookupgroup27.lookup.model.location.LocationProvider
import com.github.lookupgroup27.lookup.utils.CelestialObjectsUtils

/**
 * Repository class to manage star data, including:
 * - Loading star catalog data from a CSV file.
 * - Storing and updating the stars' positions based on the observer's location and local sidereal
 *   time.
 * - Converting celestial coordinates (RA/Dec) into horizon coordinates (Azimuth/Altitude), and then
 *   into Cartesian coordinates for 3D rendering.
 *
 * The CSV file should contain star attributes like name, RA, Dec, distance, and Cartesian
 * coordinates. Magnitude and spectral class data may also be included.
 */
class StarDataRepository(
    private val context: Context,
    private val locationProvider: LocationProvider
) {
  companion object {
    private const val STAR_FILE_PATH = "stars/hyg_stars.csv"
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

  private var isStarsLoaded = false
  private val stars = mutableListOf<StarData>()

  /**
   * Loads star data from a specified CSV file and populates the [stars] list.
   *
   * The CSV file is expected to contain columns for RA, Dec, and other stellar attributes. RA
   * values are converted from hours to degrees before storage. Any missing or non-parsable values
   * default to zero.
   *
   * @param filePath The relative path to the CSV file within the app's assets.
   */
  fun loadStarsFromCSV(filePath: String) {
    stars.clear()
    val inputStream = context.assets.open(filePath)
    val reader = inputStream.bufferedReader()

    reader.useLines { lines ->
      // Skip the header line and process each subsequent line
      lines.drop(1).forEach { line ->
        val values = line.split(",")

        // Ensure that the required columns are available before parsing
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

          // Convert RA from hours to degrees for consistency with Dec
          stars.add(
              StarData(
                  name = name,
                  ra = CelestialObjectsUtils.convertRaHoursToDegrees(ra),
                  dec = dec,
                  dist = dist,
                  x = x,
                  y = y,
                  z = z,
                  magnitude = magnitude))
        }
      }
    }
  }

  /**
   * Updates the Cartesian positions of all loaded stars based on the observer's current location
   * and the local sidereal time.
   *
   * Steps:
   * 1. Load star data from CSV if not already loaded.
   * 2. Get observer's location (latitude, longitude).
   * 3. Compute local sidereal time.
   * 4. Convert each star's RA/Dec to horizon coordinates (Azimuth/Altitude).
   * 5. Convert those horizon coordinates to Cartesian (x, y, z) and update the star data.
   */
  fun updateStarPositions() {
    if (!isStarsLoaded) {
      loadStarsFromCSV(STAR_FILE_PATH)
      isStarsLoaded = true
    }

    val location = locationProvider.currentLocation.value

    // If the observer's location is known, compute and update star positions
    if (location != null) {
      val latitude = location.latitude
      val longitude = location.longitude
      val siderealTime = CelestialObjectsUtils.computeSiderealTime(longitude)

      stars.forEach { star ->
        val (azimuth, altitude) =
            CelestialObjectsUtils.convertToHorizonCoordinates(
                ra = star.ra, dec = star.dec, latitude = latitude, localSiderealTime = siderealTime)

        // Update the star's (x, y, z) coordinates based on the computed horizon coordinates
        val updatedCartesianPosition = CelestialObjectsUtils.convertToCartesian(azimuth, altitude)
        star.x = updatedCartesianPosition.first.toDouble()
        star.y = updatedCartesianPosition.second.toDouble()
        star.z = updatedCartesianPosition.third.toDouble()
      }
    }
  }

  /**
   * Returns the list of [StarData] objects after their positions have been updated.
   *
   * @return A list of stars with their latest Cartesian coordinates.
   */
  fun getUpdatedStars(): List<StarData> {
    return stars
  }
}
