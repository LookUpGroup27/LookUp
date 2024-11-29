package com.github.lookupgroup27.lookup.model.stars

import android.content.Context
import com.github.lookupgroup27.lookup.model.location.LocationProvider
import android.location.Location
import androidx.compose.ui.platform.LocalContext
import kotlin.math.*

/**
 * Repository class to manage star data, including fetching, location updates, and position calculations.
 */
class StarDataRepository(private val context: Context, private val locationProvider: LocationProvider) {

  companion object {
    private const val STAR_FILE_PATH = "stars/"
    private const val REQUIRED_COLUMNS_COUNT = 16
    private const val NAME_INDEX = 6
    private const val RA_INDEX = 7
    private const val DEC_INDEX = 8
    private const val MAGNITUDE_INDEX = 13
    private const val SPECTRAL_CLASS_INDEX = 15
  }

  private val stars = mutableListOf<StarData>()

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
        if (values.size >= REQUIRED_COLUMNS_COUNT) {
          val name = values[NAME_INDEX]
          val ra = values[RA_INDEX].toDoubleOrNull() ?: 0.0
          val dec = values[DEC_INDEX].toDoubleOrNull() ?: 0.0
          val magnitude = values[MAGNITUDE_INDEX].toDoubleOrNull() ?: 0.0
          val spectralClass = values.getOrNull(SPECTRAL_CLASS_INDEX)?.takeIf { it.isNotEmpty() }

          // Add star with default Cartesian position
          stars.add(
            StarData(
              name = name,
              position = Triple(0.0f, 0.0f, 0.0f),
              size = calculateSizeFromMagnitude(magnitude),
              magnitude = magnitude,
              spectralClass = spectralClass
            )
          )
        }
      }
    }
  }

  /**
   * Updates the Cartesian positions of all stars based on the current location and time.
   */
  fun updateStarPositions() {
    val location = locationProvider.currentLocation.value
    if (location != null) {
      val latitude = location.latitude
      val longitude = location.longitude
      val siderealTime = computeSiderealTime(longitude)

      stars.forEach { star ->
        // Convert RA/Dec to horizon coordinates
        val (azimuth, altitude) = convertToHorizonCoordinates(
          ra = star.position.first.toDouble(),
          dec = star.position.second.toDouble(),
          latitude = latitude,
          siderealTime = siderealTime
        )

        // Update the star's Cartesian position
        star.position = convertToCartesian(azimuth, altitude, 1.0f)
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

  /**
   * Computes the local sidereal time based on longitude and the current time.
   *
   * @param longitude Observer's longitude.
   * @return Sidereal time in degrees.
   */
  fun computeSiderealTime(longitude: Double): Double {
    val currentTimeMillis = System.currentTimeMillis()
    val jd = (currentTimeMillis / 86400000.0) + 2440587.5 // Julian Date
    val jdAtMidnight = floor(jd - 0.5) + 0.5
    val daysSinceJ2000 = jdAtMidnight - 2451545.0
    val meanSiderealTime = (280.46061837 + 360.98564736629 * daysSinceJ2000) % 360
    return (meanSiderealTime + longitude) % 360
  }

  /**
   * Converts celestial coordinates (RA/Dec) to horizon coordinates (Azimuth/Altitude).
   *
   * @param ra Right Ascension in degrees.
   * @param dec Declination in degrees.
   * @param latitude Observer's latitude in degrees.
   * @param siderealTime Local sidereal time in degrees.
   * @return A pair of Azimuth and Altitude in degrees.
   */
  public fun convertToHorizonCoordinates(
    ra: Double, dec: Double, latitude: Double, siderealTime: Double
  ): Pair<Double, Double> {
    // Ensure Hour Angle (HA) is within [0, 360) using modulo operation
    val ha = (siderealTime - ra + 360) % 360
    val haRad = Math.toRadians(ha)
    val decRad = Math.toRadians(dec)
    val latRad = Math.toRadians(latitude)

    // Calculate Altitude using trigonometric formula
    val sinAlt = Math.sin(decRad) * Math.sin(latRad) + Math.cos(decRad) * Math.cos(latRad) * Math.cos(haRad)
    val alt = Math.asin(sinAlt)

    // Calculate Azimuth using trigonometric formula
    val cosAz = (Math.sin(decRad) - Math.sin(alt) * Math.sin(latRad)) / (Math.cos(alt) * Math.cos(latRad))
    var az = Math.acos(cosAz)

    // Adjust Azimuth based on the Hour Angle (HA)
    if (Math.sin(haRad) > 0) {
      az = 2 * Math.PI - az
    }

    // Convert Azimuth and Altitude back to degrees
    val azimuth = (Math.toDegrees(az) + 360) % 360
    val altitude = Math.toDegrees(alt)

    return Pair(azimuth, altitude)
  }




  /**
   * Converts horizon coordinates (Azimuth/Altitude) to Cartesian coordinates.
   *
   * @param azimuth Azimuth in degrees.
   * @param altitude Altitude in degrees.
   * @param radius The radius of the celestial sphere.
   * @return A Triple representing x, y, and z coordinates.
   */
  fun convertToCartesian(
    azimuth: Double, altitude: Double, radius: Float
  ): Triple<Float, Float, Float> {
    val azRad = Math.toRadians(azimuth)
    val altRad = Math.toRadians(altitude)

    val x = (radius * cos(altRad) * cos(azRad)).toFloat()
    val y = (radius * cos(altRad) * sin(azRad)).toFloat()
    val z = (radius * sin(altRad)).toFloat()

    return Triple(x, y, z)
  }

  /**
   * Calculates the size of a star based on its magnitude.
   *
   * @param magnitude The brightness magnitude of the star.
   * @return A float representing the star size.
   */
  private fun calculateSizeFromMagnitude(magnitude: Double): Float {
    return ((1.0 / (1.0 + Math.exp(magnitude)) * 2) + 0.5).toFloat()
  }
}
