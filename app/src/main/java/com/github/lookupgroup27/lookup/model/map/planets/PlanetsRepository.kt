package com.github.lookupgroup27.lookup.model.map.planets

import android.content.Context
import com.github.lookupgroup27.lookup.R
import com.github.lookupgroup27.lookup.model.location.LocationProvider
import com.github.lookupgroup27.lookup.model.map.renderables.Moon
import com.github.lookupgroup27.lookup.model.map.renderables.Planet
import com.github.lookupgroup27.lookup.util.CelestialObjectsUtils
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import okhttp3.HttpUrl.Companion.toHttpUrlOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject

/**
 * Handles planetary data fetching, conversions, and Cartesian coordinate computations.
 *
 * This class:
 * - Maintains a list of [PlanetData] objects representing known planets in the solar system.
 * - Uses the device’s current location to determine the observer’s viewpoint on Earth.
 * - Fetches current planetary positions (Right Ascension and Declination) from NASA/JPL Horizons
 *   API.
 * - Converts the obtained RA/Dec into horizon coordinates (Azimuth, Altitude), and then into
 *   Cartesian coordinates suitable for 3D rendering.
 * - Allows mapping the planetary data into renderable [Planet] objects for visualization.
 */
class PlanetsRepository(
    private val locationProvider: LocationProvider,
    private val client: OkHttpClient = OkHttpClient(),
    private val baseUrl: String = "https://ssd.jpl.nasa.gov/api/horizons.api"
) {

  // Holds the planet information, including name, Horizons ID, and rendering texture references.
  val planets = mutableListOf<PlanetData>()

  init {
    // Initializes the list of known planets (excluding Earth).
    // Each planet has a unique Horizons ID, and we currently associate all planets with the same
    // texture.
    planets.addAll(
        listOf(
            PlanetData("Moon", "301", textureId = R.drawable.full_moon),
            PlanetData("Mercury", "199", textureId = R.drawable.mercury_texture),
            PlanetData("Venus", "299", textureId = R.drawable.venus_texture),
            PlanetData("Mars", "499", textureId = R.drawable.mars_texture),
            PlanetData("Jupiter", "599", textureId = R.drawable.jupiter_texture),
            PlanetData("Saturn", "699", textureId = R.drawable.saturn_texture),
            PlanetData("Uranus", "799", textureId = R.drawable.uranus_texture),
            PlanetData("Neptune", "899", textureId = R.drawable.neptune_texture)))
  }

  /**
   * Fetches planetary data from the Horizons API and updates Cartesian coordinates for all planets.
   *
   * Steps:
   * 1. Determine the current observer’s location from [locationProvider].
   * 2. Compute the local sidereal time for that location (this aligns the celestial coordinate
   *    system with local time).
   * 3. For each planet:
   *     - Fetch its RA/Dec from the Horizons API based on the observer’s lat/long and current time.
   *     - Convert RA/Dec to horizon coordinates (Azimuth/Altitude).
   *     - Convert horizon coordinates to Cartesian (x, y, z) for rendering.
   */
  fun updatePlanetsData() {

    // Retrieve current location; if none is available, we can’t proceed
    val currentLocation = locationProvider.currentLocation.value ?: return

    val latitude = currentLocation.latitude
    val longitude = currentLocation.longitude

    // Compute local sidereal time which is required for converting RA/Dec to horizon coordinates
    val siderealTime = CelestialObjectsUtils.computeSiderealTime(longitude)

    // Fetch and update each planet’s position
    planets.forEach { planet ->
      // Fetch Right Ascension (RA) and Declination (Dec) from the Horizons API
      val position = fetchPlanetPosition(latitude, longitude, planet.id)
      if (position != null) {
        planet.ra = position.first
        planet.dec = position.second

        // Convert RA/Dec to horizon coordinates (Azimuth, Altitude)
        val (azimuth, altitude) =
            CelestialObjectsUtils.convertToHorizonCoordinates(
                ra = planet.ra,
                dec = planet.dec,
                latitude = latitude,
                localSiderealTime = siderealTime)

        // Convert horizon coordinates to Cartesian coordinates for rendering
        planet.cartesian = CelestialObjectsUtils.convertToCartesian(azimuth, altitude)
      }
    }
  }

  /**
   * Maps the planet data in the planets list to renderable [Planet] objects.
   *
   * @param context The Android context used for resource access (e.g., textures).
   * @return A list of [Planet] renderable objects with position and texture.
   */
  fun mapToRenderablePlanets(context: Context): List<Planet> {
    // Transform each PlanetData into a Planet, providing positions as a float array
    return planets.map { planetData ->
      val position = planetData.cartesian

      if (planetData.name == "Moon") {
        Moon(
            context = context,
            position = floatArrayOf(position.first, position.second, position.third))
      } else {
        Planet(
            context = context,
            name = planetData.name,
            position = floatArrayOf(position.first, position.second, position.third),
            textureId = planetData.textureId // Resource ID for the planet’s texture
            )
      }
    }
  }

  /**
   * Returns a list of Cartesian coordinates for all planets.
   *
   * Useful for debugging or for other rendering systems that just need position data.
   *
   * @return List of Triple<Float, Float, Float> each representing x, y, z coordinates.
   */
  fun getPlanetsCartesianCoordinates(): List<Triple<Float, Float, Float>> {
    return planets.map { it.cartesian }
  }

  /**
   * Fetches planetary positions (RA/Dec) from the JPL Horizons API.
   *
   * The API call parameters:
   * - Coordinates are computed at the current system time.
   * - The time window requested is just one minute; we use the first line of data.
   * - The observer’s coordinates (lat, lon) are provided for accurate RA/Dec calculation.
   * - RA and Dec are requested in degrees.
   *
   * @param lat Observer's latitude in decimal degrees.
   * @param lon Observer's longitude in decimal degrees.
   * @param planetId The unique Horizons body ID for the planet (e.g., "499" for Mars).
   * @return A Pair<Double, Double>? representing (RA in degrees, Dec in degrees) or null if
   *   unavailable.
   */
  private fun fetchPlanetPosition(
      lat: Double,
      lon: Double,
      planetId: String
  ): Pair<Double, Double>? {
    // Format the current date/time as the start time
    val calendar = Calendar.getInstance()
    val dateFormat = SimpleDateFormat("yyyy-MMM-dd HH:mm", Locale.US)
    val startTime = dateFormat.format(calendar.time)

    // Add one minute to form the stop time
    calendar.add(Calendar.MINUTE, 1)
    val stopTime = dateFormat.format(calendar.time)

    // Build the request URL for Horizons API
    val urlBuilder = baseUrl.toHttpUrlOrNull()?.newBuilder()
    urlBuilder?.apply {
      addQueryParameter("format", "json")
      addQueryParameter("COMMAND", "'$planetId'") // Planet ID
      addQueryParameter("CENTER", "'coord@399'") // Using Earth-centered coordinates
      addQueryParameter("COORD_TYPE", "'GEODETIC'") // Geodetic coordinates
      addQueryParameter("SITE_COORD", "'$lon,$lat,0'") // Longitude, Latitude, Elevation=0
      addQueryParameter("START_TIME", "'$startTime'") // Current time
      addQueryParameter("STOP_TIME", "'$stopTime'") // One minute later
      addQueryParameter("STEP_SIZE", "'1 m'") // Step size of 1 minute
      addQueryParameter("QUANTITIES", "'1'") // Request RA/Dec data
      addQueryParameter("ANG_FORMAT", "'DEG'") // RA/Dec in degrees
    }

    val url = urlBuilder?.build() ?: return null

    // Make the network request to Horizons API
    val request = Request.Builder().url(url).build()
    client.newCall(request).execute().use { response ->
      if (!response.isSuccessful) throw IOException("API call failed with code ${response.code}")

      val json = JSONObject(response.body?.string() ?: return null)
      val result = json.optString("result", null) ?: return null

      // The data we need is found between the $$SOE and $$EOE markers in the result string
      val dataLines =
          result.substringAfter("$$" + "SOE").substringBefore("$$" + "EOE").trim().lines()

      // If no data lines are found, we cannot parse RA/Dec
      if (dataLines.isEmpty()) {
        // Debugging message can be logged here if needed
        return null
      }

      // Parse the first valid data line for RA and Dec
      val firstDataLine = dataLines.firstOrNull { it.isNotBlank() } ?: return null
      val columns = firstDataLine.split("\\s+".toRegex()).map { it.trim() }

      // According to Horizons format, RA and Dec are typically in columns 4 and 5 (0-based index: 3
      // and 4)
      if (columns.size >= 3) {
        val raDeg = columns[3].toDoubleOrNull() ?: return null
        val decDeg = columns[4].toDoubleOrNull() ?: return null
        return Pair(raDeg, decDeg)
      }

      return null
    }
  }
}
