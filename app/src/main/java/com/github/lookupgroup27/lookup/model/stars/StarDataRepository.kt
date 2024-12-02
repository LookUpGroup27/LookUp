package com.github.lookupgroup27.lookup.model.stars

import android.content.Context
import android.util.Log
import java.io.BufferedReader
import java.io.InputStreamReader

/** Repository class to manage star data, including fetching and preprocessing. */
class StarDataRepository {

  companion object {
    private const val STAR_FILE_PATH = "stars/"
    private const val REQUIRED_COLUMNS_COUNT = 16
    private const val NAME_INDEX = 6
    private const val RA_INDEX = 7
    private const val DEC_INDEX = 8
    private const val MAGNITUDE_INDEX = 13
    private const val SPECTRAL_CLASS_INDEX = 15
  }

  /**
   * Loads star data from a CSV file and converts it into a list of [StarData] objects.
   *
   * @param context The application context to access assets.
   * @param fileName The name of the CSV file to load from the assets directory.
   * @return A list of processed [StarData] objects ready for use in rendering.
   */
  fun getStars(context: Context, fileName: String): List<StarData> {

    require(fileName.isNotBlank()) { "File name must not be blank" }
    require(fileName.endsWith(".csv")) { "File name must be a .csv file" }

    val stars = mutableListOf<StarData>()
    val inputStream = context.assets.open(STAR_FILE_PATH + fileName)
    val reader = BufferedReader(InputStreamReader(inputStream))

    reader.useLines { lines ->
      lines.drop(1).forEach { line ->
        try {
          val values = line.split(",")

          if (values.size >= REQUIRED_COLUMNS_COUNT) {
            val name = values[NAME_INDEX]
            val ra = values[RA_INDEX].toDoubleOrNull() ?: 0.0
            val dec = values[DEC_INDEX].toDoubleOrNull() ?: 0.0
            val magnitude = values[MAGNITUDE_INDEX].toDoubleOrNull() ?: 0.0
            val spectralClass = values.getOrNull(SPECTRAL_CLASS_INDEX)?.takeIf { it.isNotEmpty() }

            // Convert RA and DEC to Cartesian coordinates
            val position = convertToCartesian(ra, dec, 1.0f)

            // Calculate star size based on magnitude
            val size = calculateSizeFromMagnitude(magnitude)

            stars.add(
                StarData(
                    name = name,
                    position = position,
                    size = size,
                    magnitude = magnitude,
                    spectralClass = spectralClass))
          }
        } catch (e: Exception) {
          Log.e("StarDataRepository", "Error parsing line: $line", e)
        }
      }
    }

    return stars
  }

  /**
   * Converts celestial coordinates (RA, DEC) to Cartesian coordinates.
   *
   * @param ra Right Ascension in degrees.
   * @param dec Declination in degrees.
   * @param radius The radius for Cartesian conversion.
   * @return A [Triple] containing x, y, and z coordinates.
   */
  fun convertToCartesian(ra: Double, dec: Double, radius: Float): Triple<Float, Float, Float> {
    val raRad = Math.toRadians(ra) // Convert RA to radians
    val decRad = Math.toRadians(dec) // Convert DEC to radians

    val x = (radius * Math.cos(decRad) * Math.cos(raRad)).toFloat()
    val y = (radius * Math.cos(decRad) * Math.sin(raRad)).toFloat()
    val z = (radius * Math.sin(decRad)).toFloat()

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
