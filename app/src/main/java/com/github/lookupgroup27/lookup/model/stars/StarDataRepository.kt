package com.github.lookupgroup27.lookup.model.stars

import android.content.Context
import java.io.BufferedReader
import java.io.InputStreamReader

/** Repository class to manage star data, including fetching and preprocessing. */
class StarDataRepository {

  /**
   * Loads star data from a CSV file and converts it into a list of [StarData] objects.
   *
   * @param context The application context to access assets.
   * @param fileName The name of the CSV file to load from the assets directory.
   * @return A list of processed [StarData] objects ready for use in rendering.
   */
  fun getStars(context: Context, fileName: String): List<StarData> {
    val stars = mutableListOf<StarData>()
    val inputStream = context.assets.open("stars/$fileName")
    val reader = BufferedReader(InputStreamReader(inputStream))

    reader.useLines { lines ->
      lines.drop(1).forEach { line ->
        try {
          val values = line.split(",")

          if (values.size > 15) {
            val name = values[6]
            val ra = values[7].toDoubleOrNull() ?: 0.0
            val dec = values[8].toDoubleOrNull() ?: 0.0
            val magnitude = values[13].toDoubleOrNull() ?: 0.0
            val spectralClass = values.getOrNull(15)?.takeIf { it.isNotEmpty() }

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
          println("Error parsing line: $line. Error: ${e.message}")
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
