package com.github.lookupgroup27.lookup.model.map.planets

import android.content.Context
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject
import java.io.IOException
import kotlin.math.*

/**
 * Handles planetary data computations and conversions.
 */
class PlanetsRepository(private val context: Context) {

    private val client = OkHttpClient()

    // Data structure to hold planet information
    private data class Planet(
        val name: String,
        val id: String,
        var ra: Double = 0.0, // Right Ascension in degrees
        var dec: Double = 0.0, // Declination in degrees
        var cartesian: Triple<Float, Float, Float> = Triple(0.0f, 0.0f, 0.0f) // Cartesian coordinates
    )

    // List of planets to compute and store
    private val planets = mutableListOf(
        Planet("Mercury", "199"),
        Planet("Venus", "299"),
        Planet("Mars", "499"),
        Planet("Jupiter", "599"),
        Planet("Saturn", "699"),
        Planet("Uranus", "799"),
        Planet("Neptune", "899")
    )

    /**
     * Fetches and updates all planetary positions (RA/Dec) and computes Cartesian coordinates.
     *
     * @param latitude Observer's latitude.
     * @param longitude Observer's longitude.
     */
    fun updatePlanetaryData(latitude: Double, longitude: Double) {
        val siderealTime = computeSiderealTime(longitude)
        planets.forEach { planet ->
            val position = fetchPlanetPosition(latitude, longitude, planet.id)
            if (position != null) {
                planet.ra = position.first
                planet.dec = position.second
                val (azimuth, altitude) = convertToHorizonCoordinates(
                    position.first, position.second, latitude, siderealTime
                )
                planet.cartesian = convertToCartesian(azimuth, altitude, 1.0f)
            }
        }
    }

    /**
     * Returns a list of Cartesian coordinates for all planets.
     *
     * @return List of Cartesian coordinates for rendering.
     */
    fun getPlanetaryCartesianCoordinates(): List<Triple<Float, Float, Float>> {
        return planets.map { it.cartesian }
    }

    /**
     * Fetches planetary positions (RA/Dec) from the JPL Horizons API.
     *
     * @param lat Observer's latitude.
     * @param lon Observer's longitude.
     * @param planetId The ID of the planet in the API (e.g., "499" for Mars).
     * @return A pair of RA and Dec in degrees, or null if the fetch fails.
     */
    private fun fetchPlanetPosition(lat: Double, lon: Double, planetId: String): Pair<Double, Double>? {
        val url = "https://ssd.jpl.nasa.gov/api/horizons.api?format=json&COMMAND='$planetId'&CENTER='coord@399'&SITE_COORD='$lat,$lon,0'&START_TIME=NOW&STOP_TIME=NOW&STEP_SIZE='1m'"
        val request = Request.Builder().url(url).build()

        client.newCall(request).execute().use { response ->
            if (!response.isSuccessful) throw IOException("Unexpected code $response")
            val json = JSONObject(response.body?.string() ?: return null)
            val result = json.optJSONObject("result") ?: return null
            val ra = result.optDouble("RA", Double.NaN)
            val dec = result.optDouble("DEC", Double.NaN)
            return if (!ra.isNaN() && !dec.isNaN()) Pair(ra, dec) else null
        }
    }

    /**
     * Computes the local sidereal time based on longitude and current time.
     *
     * @param longitude Observer's longitude.
     * @return Sidereal time in degrees.
     */
    private fun computeSiderealTime(longitude: Double): Double {
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
    private fun convertToHorizonCoordinates(
        ra: Double,
        dec: Double,
        latitude: Double,
        siderealTime: Double
    ): Pair<Double, Double> {
        val ha = siderealTime - ra // Hour Angle
        val haRad = Math.toRadians(ha)
        val decRad = Math.toRadians(dec)
        val latRad = Math.toRadians(latitude)

        val sinAlt = sin(decRad) * sin(latRad) + cos(decRad) * cos(latRad) * cos(haRad)
        val alt = Math.asin(sinAlt)

        val cosAz = (sin(decRad) - sin(alt) * sin(latRad)) / (cos(alt) * cos(latRad))
        val az = if (haRad > 0) 2 * PI - acos(cosAz) else acos(cosAz)

        return Pair(Math.toDegrees(az), Math.toDegrees(alt))
    }

    /**
     * Converts horizon coordinates (Azimuth/Altitude) to Cartesian coordinates.
     *
     * @param azimuth Azimuth in degrees.
     * @param altitude Altitude in degrees.
     * @param radius The radius of the celestial sphere.
     * @return A Triple representing x, y, and z coordinates.
     */
    private fun convertToCartesian(azimuth: Double, altitude: Double, radius: Float): Triple<Float, Float, Float> {
        val azRad = Math.toRadians(azimuth)
        val altRad = Math.toRadians(altitude)

        val x = (radius * cos(altRad) * cos(azRad)).toFloat()
        val y = (radius * cos(altRad) * sin(azRad)).toFloat()
        val z = (radius * sin(altRad)).toFloat()

        return Triple(x, y, z)
    }
}
