package com.github.lookupgroup27.lookup.utils

import java.time.ZoneOffset
import java.time.ZonedDateTime
import kotlin.math.*
import kotlin.math.floor

object CelestialObjectsUtils {

  private val SCALING_FACTOR = 100

  /**
   * Converts Right Ascension (RA) from hours to degrees.
   *
   * Right Ascension is typically measured in hours, where 1 hour corresponds to 15 degrees of arc.
   * This function performs the conversion by multiplying the RA value in hours by 15.
   *
   * @param raHours The Right Ascension value in hours. This is a floating-point number where:
   *     - 1 hour = 15 degrees
   *     - 0.1 hour = 1.5 degrees Example: 6.5 hours corresponds to 97.5 degrees.
   *
   * @return The Right Ascension value converted to degrees.
   */
  fun convertRaHoursToDegrees(raHours: Double): Double {
    return raHours * 15.0
  }

  /**
   * Calculates the Julian Day Number for a given date.
   *
   * @param year The year.
   * @param month The month (1-12).
   * @param day The day of the month.
   * @return Julian Day Number as a Double.
   */
  fun getJulianDay(year: Int, month: Int, day: Int): Double {
    val a = ((14 - month) / 12)
    val y = year + 4800 - a
    val m = month + 12 * a - 3
    return day + ((153 * m + 2) / 5) + 365 * y + (y / 4) - (y / 100) + (y / 400) - 32045.5
  }

  /**
   * Computes the Local Sidereal Time (LST) at the given longitude for the current UTC time.
   *
   * Sidereal time is a measure of the Earth's rotation relative to distant stars rather than the
   * Sun. Local Sidereal Time is useful in astronomical calculations such as determining which
   * constellations are currently overhead at a given location.
   *
   * This function:
   * 1. Retrieves the current UTC time.
   * 2. Converts that time into a Julian Date (JD).
   * 3. Computes the Greenwich Sidereal Time (GST) using standard astronomical formulas.
   * 4. Adjusts GST for the given longitude to produce the Local Sidereal Time (LST).
   *
   * @param longitude The observer's longitude in degrees, where positive values are East and
   *   negative values are West.
   * @return The local sidereal time in degrees [0, 360).
   */
  fun computeSiderealTime(longitude: Double): Double {
    val now = ZonedDateTime.now(ZoneOffset.UTC)
    val year = now.year
    val month = now.monthValue
    val day = now.dayOfMonth
    val hour = now.hour
    val minute = now.minute
    val second = now.second

    // Calculate Julian Date
    val jd = calculateJulianDate(year, month, day, hour, minute, second)

    // Calculate GST
    val gst = calculateGst(jd)
    // Normalize GST
    val gstCorrected = (gst + 360.0) % 360.0

    // Calculate LST
    val lst = (gstCorrected + longitude + 360) % 360.0
    return lst
  }

  /**
   * Converts a given Gregorian calendar date and time (UTC) into a Julian Date.
   *
   * The Julian Date is a continuous count of days and fractions of a day from the start of the
   * Julian Period. It is commonly used in astronomical calculations for its simplicity.
   *
   * This function follows the standard algorithm for converting a Gregorian date to Julian Date:
   *
   * Steps:
   * 1. If the month is January or February, treat them as the 13th or 14th month of the previous
   *    year.
   * 2. Compute the integral part and fractional day.
   * 3. Apply corrections for the Gregorian calendar reform.
   *
   * @param year The year in UTC.
   * @param month The month of the year (1-12).
   * @param day The day of the month (1-31).
   * @param hour The hour of the day (0-23).
   * @param minute The minute of the hour (0-59).
   * @param second The second of the minute (0-59).
   * @return The Julian Date as a Double.
   */
  private fun calculateJulianDate(
      year: Int,
      month: Int,
      day: Int,
      hour: Int,
      minute: Int,
      second: Int
  ): Double {
    val jYear = if (month > 2) year else year - 1
    val jMonth = if (month > 2) month else month + 12
    val dayFraction = (hour + minute / 60.0 + second / 3600.0) / 24.0

    val a = floor(jYear / 100.0)
    val b = 2 - a + floor(a / 4.0)

    return floor(365.25 * (jYear + 4716)) + floor(30.6001 * (jMonth + 1)) + day + dayFraction + b -
        1524.5
  }

  /**
   * Calculates the Greenwich Sidereal Time (GST) for a given Julian Date.
   *
   * GST is the sidereal time at the Prime Meridian (0° longitude). It is computed from the Julian
   * Date using a standard polynomial approximation defined in astronomical references.
   *
   * Formula: T = (JD - 2451545.0) / 36525 GST (in degrees) = 280.46061837
   * + 360.98564736629 * (JD - 2451545.0)
   * + 0.000387933 * T^2
   * - T^3 / 38710000
   *
   * @param jd The Julian Date at the observation time.
   * @return The Greenwich Sidereal Time in degrees (not normalized).
   */
  private fun calculateGst(jd: Double): Double {
    val t = (jd - 2451545.0) / 36525.0
    return 280.46061837 + 360.98564736629 * (jd - 2451545.0) + 0.000387933 * t * t -
        (t * t * t / 38710000.0)
  }

  /**
   * Converts equatorial coordinates (RA/Dec) to horizon coordinates (Azimuth/Altitude).
   *
   * @param ra Right Ascension in degrees.
   * @param dec Declination in degrees.
   * @param latitude Observer's latitude in degrees.
   * @param localSiderealTime The local sidereal time in degrees.
   * @return A pair representing azimuth and altitude in degrees.
   */
  fun convertToHorizonCoordinates(
      ra: Double, // Right Ascension in degrees
      dec: Double, // Declination in degrees
      latitude: Double, // Observer's latitude in degrees
      localSiderealTime: Double // Local Sidereal Time in degrees
  ): Pair<Double, Double> {
    // Step 1: Compute the Hour Angle (HA)
    val hourAngle =
        ((localSiderealTime - ra + 360.0) % 360.0).let { if (it > 180) it - 360 else it }

    // Step 2: Convert all angles to radians
    val haRad = Math.toRadians(hourAngle)
    val decRad = Math.toRadians(dec)
    val latRad = Math.toRadians(latitude)

    // Step 3: Compute Altitude (Alt)
    val sinAlt = sin(decRad) * sin(latRad) + cos(decRad) * cos(latRad) * cos(haRad)
    val altRad = asin(sinAlt) // Altitude in radians
    val altitude = Math.toDegrees(altRad)

    // Step 4: Compute Azimuth (Az)
    val sinAz = -sin(haRad) * cos(decRad)
    val cosAz = (sin(decRad) - sin(latRad) * sinAlt) / (cos(latRad) * cos(altRad))
    val azimuth = Math.toDegrees(atan2(sinAz, cosAz)).let { if (it < 0) it + 360 else it }

    return Pair(azimuth, altitude)
  }

  /**
   * Converts horizon coordinates (Azimuth/Altitude) to Cartesian coordinates.
   *
   * @param azimuth Azimuth in degrees.
   * @param altitude Altitude in degrees.
   * @return A Triple representing x, y, and z coordinates.
   */
  fun convertToCartesian(
      azimuth: Double,
      altitude: Double,
  ): Triple<Float, Float, Float> {
    val azRad = Math.toRadians(azimuth)
    val altRad = Math.toRadians(altitude)

    val x = (SCALING_FACTOR * cos(altRad) * sin(azRad)).toFloat()
    val y = (SCALING_FACTOR * cos(altRad) * cos(azRad)).toFloat()
    val z = (SCALING_FACTOR * sin(altRad)).toFloat()

    return Triple(x, y, z)
  }
}
