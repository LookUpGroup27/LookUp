package com.github.lookupgroup27.lookup.model.stars

import android.content.Context
import java.io.BufferedReader
import java.io.InputStreamReader
import kotlin.math.cos
import kotlin.math.sin

data class StarData(
    val name: String,
    val ra: Double,
    val dec: Double,
    val mag: Double,
    val spect: String?
)

class StarDataManager {

  /**
   * Loads stars data from a CSV file in the assets/stars directory.
   *
   * @param fileName Name of the CSV file to load.
   * @return A list of [StarData] objects.
   */
  fun loadStars(context: Context, fileName: String): List<StarData> {
    val stars = mutableListOf<StarData>()
    val inputStream = context.assets.open("stars/$fileName")
    val reader = BufferedReader(InputStreamReader(inputStream))

    // Parse each line after skipping the header
    reader.useLines { lines ->
      lines.drop(1).forEach { line ->
        try {
          val values = line.split(",")
          if (values.size > 15) {
            val name = values[6]
            val ra = values[7].toDoubleOrNull() ?: 0.0
            val dec = values[8].toDoubleOrNull() ?: 0.0
            val mag = values[13].toDoubleOrNull() ?: 0.0
            val spect = values.getOrNull(15)?.takeIf { it.isNotEmpty() }

            stars.add(StarData(name, ra, dec, mag, spect))
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
   * @param radius Radius for Cartesian conversion.
   * @return A [Triple] containing x, y, and z coordinates.
   */
  fun convertToCartesian(ra: Double, dec: Double, radius: Float): Triple<Float, Float, Float> {
    val raRad = Math.toRadians(ra)
    val decRad = Math.toRadians(dec)

    val x = (radius * cos(decRad) * cos(raRad)).toFloat()
    val y = (radius * cos(decRad) * sin(raRad)).toFloat()
    val z = (radius * sin(decRad)).toFloat()

    return Triple(x, y, z)
  }
}
