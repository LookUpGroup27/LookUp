package com.github.lookupgroup27.lookup.utils

import java.util.Calendar
import kotlin.math.*
import net.fortuna.ical4j.model.TimeZone

object CelestialObjectsUtils {

  /**
   * Computes the local sidereal time based on longitude and the current time.
   *
   * @param longitude Observer's longitude in degrees.
   * @return Sidereal time in degrees.
   */
  fun computeSiderealTime(longitude: Double): Double {
    val currentTimeMillis = System.currentTimeMillis()
    val calendar =
        Calendar.getInstance(TimeZone.getTimeZone("UTC")).apply { timeInMillis = currentTimeMillis }

    val year = calendar.get(Calendar.YEAR)
    val month = calendar.get(Calendar.MONTH) + 1 // January is 0, so we add 1
    val day = calendar.get(Calendar.DAY_OF_MONTH)
    val hour = calendar.get(Calendar.HOUR_OF_DAY)
    val minute = calendar.get(Calendar.MINUTE)
    val second = calendar.get(Calendar.SECOND)

    // Julian Date calculation
    val jDay = day + (hour + minute / 60.0 + second / 3600.0) / 24.0
    val jMonth = if (month > 2) month else month + 12
    val jYear = if (month > 2) year else year - 1

    val a = floor(jYear / 100.0)
    val b = 2 - a + floor(a / 4.0)
    val jd = floor(365.25 * (jYear + 4716)) + floor(30.6001 * (jMonth + 1)) + jDay + b - 1524.5

    // Greenwich Sidereal Time (in degrees)
    val t = (jd - 2451545.0) / 36525.0
    val gst =
        280.46061837 + 360.98564736629 * (jd - 2451545.0) + 0.000387933 * t * t -
            t * t * t / 38710000.0

    // Local Sidereal Time (in degrees)
    val lst = (gst + longitude + 360.0) % 360.0
    return lst
  }

  /**
   * Converts equatorial coordinates (RA/Dec) to horizon coordinates (Azimuth/Altitude).
   *
   * @param ra Right Ascension in degrees.
   * @param dec Declination in degrees.
   * @param latitude Observer's latitude in degrees.
   * @param longitude Observer's longitude in degrees.
   * @param localSiderealTime The local sidereal time in degrees.
   * @return A pair representing azimuth and altitude in degrees.
   */
  fun convertToHorizonCoordinates(
      ra: Double,
      dec: Double,
      latitude: Double,
      localSiderealTime: Double
  ): Pair<Double, Double> {

    // Calculate hour angle in degrees
    val hourAngle = (localSiderealTime - ra + 360) % 360
    println("Hour Angle: $hourAngle")

    // Convert to radians where needed and calculate altitude directly
    val altitude =
        Math.toDegrees(
            asin(
                Math.sin(Math.toRadians(dec)) * Math.sin(Math.toRadians(latitude)) +
                    Math.cos(Math.toRadians(dec)) *
                        Math.cos(Math.toRadians(latitude)) *
                        Math.cos(Math.toRadians(hourAngle))))
    println("Altitude: $altitude")

    // Calculate azimuth using trigonometric functions, directly using degrees to radians conversion
    var azimuth =
        Math.toDegrees(
            atan2(
                -Math.cos(Math.toRadians(dec)) * Math.sin(Math.toRadians(hourAngle)),
                Math.sin(Math.toRadians(dec)) -
                    Math.sin(Math.toRadians(altitude)) * Math.sin(Math.toRadians(latitude))))

    // Ensure azimuth is in the range [0, 360)
    if (azimuth < 0) {
      azimuth += 360.0
    }
    println("Azimuth: $azimuth")

    return Pair(azimuth, altitude)
  }

  /**
   * Converts horizon coordinates (Azimuth/Altitude) to Cartesian coordinates.
   *
   * @param azimuth Azimuth in degrees.
   * @param altitude Altitude in degrees.
   * @param distance The distance to the celestial object in parsecs.
   * @return A Triple representing x, y, and z coordinates.
   */
  fun convertToCartesian(
      azimuth: Double,
      altitude: Double,
      distance: Double
  ): Triple<Float, Float, Float> {
    val azRad = Math.toRadians(azimuth)
    val altRad = Math.toRadians(altitude)

    val x = (distance * cos(altRad) * cos(azRad)).toFloat()
    val y = (distance * cos(altRad) * sin(azRad)).toFloat()
    val z = (distance * sin(altRad)).toFloat()

    return Triple(x, y, z)
  }
}
