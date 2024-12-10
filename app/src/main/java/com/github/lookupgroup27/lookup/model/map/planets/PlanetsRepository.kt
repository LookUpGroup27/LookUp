import com.github.lookupgroup27.lookup.model.location.LocationProvider
import com.github.lookupgroup27.lookup.model.map.planets.PlanetData
import com.github.lookupgroup27.lookup.utils.CelestialObjectsUtils
import java.io.IOException
import okhttp3.HttpUrl.Companion.toHttpUrlOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject

/** Handles planetary data fetching, conversions, and Cartesian coordinate computations. */
class PlanetsRepository(
    private val locationProvider: LocationProvider,
    private val client: OkHttpClient = OkHttpClient(),
    private val baseUrl: String = "https://ssd.jpl.nasa.gov/api/horizons.api"
) {

  val planets = mutableListOf<PlanetData>()

  init {
    // Initialize known planets with their IDs
    planets.addAll(
        listOf(
            PlanetData("Mercury", "199"),
            PlanetData("Venus", "299"),
            PlanetData("Mars", "499"),
            PlanetData("Jupiter", "599"),
            PlanetData("Saturn", "699"),
            PlanetData("Uranus", "799"),
            PlanetData("Neptune", "899")))
  }

  /**
   * Fetches planetary data from the Horizons API and updates Cartesian coordinates for all planets.
   */
  fun updatePlanetsData() {
    val currentLocation =
        locationProvider.currentLocation.value
            ?: throw IllegalStateException(
                "Current location is null. Cannot update planetary data.")

    val latitude = currentLocation.latitude
    val longitude = currentLocation.longitude
    val siderealTime = CelestialObjectsUtils.computeSiderealTime(longitude)

    planets.forEach { planet ->
      val position = fetchPlanetPosition(latitude, longitude, planet.id)
      if (position != null) {
        planet.ra = position.first
        planet.dec = position.second

        val (azimuth, altitude) =
            CelestialObjectsUtils.convertToHorizonCoordinates(
                ra = planet.ra,
                dec = planet.dec,
                latitude = latitude,
                localSiderealTime = siderealTime)
        planet.cartesian = CelestialObjectsUtils.convertToCartesian(azimuth, altitude, 1.0)
      }
    }
  }

  /**
   * Returns a list of Cartesian coordinates for all planets.
   *
   * @return List of Cartesian coordinates.
   */
  fun getPlanetsCartesianCoordinates(): List<Triple<Float, Float, Float>> {
    return planets.map { it.cartesian }
  }

  /**
   * Fetches planetary positions (RA/Dec) from the JPL Horizons API.
   *
   * @param lat Observer's latitude.
   * @param lon Observer's longitude.
   * @param planetId The ID of the planet (e.g., "499" for Mars).
   * @return A pair of RA and Dec in degrees, or null if the fetch fails.
   */
  private fun fetchPlanetPosition(
      lat: Double,
      lon: Double,
      planetId: String
  ): Pair<Double, Double>? {
    val urlBuilder = baseUrl.toHttpUrlOrNull()?.newBuilder()
    urlBuilder?.apply {
      addQueryParameter("format", "json")
      addQueryParameter("COMMAND", "'$planetId'")
      addQueryParameter("CENTER", "'coord@399'")
      addQueryParameter("COORD_TYPE", "'GEODETIC'")
      addQueryParameter("SITE_COORD", "'$lon,$lat,0'") // Longitude, Latitude, Elevation
      addQueryParameter("START_TIME", "'NOW'")
      addQueryParameter("STOP_TIME", "'NOW'")
      addQueryParameter("STEP_SIZE", "'1 m'")
      addQueryParameter("QUANTITIES", "'1'")
      addQueryParameter("ANG_FORMAT", "'DEG'")
      addQueryParameter("CSV_FORMAT", "'YES'")
    }

    val url = urlBuilder?.build() ?: return null

    val request = Request.Builder().url(url).build()

    client.newCall(request).execute().use { response ->
      if (!response.isSuccessful) throw IOException("API call failed with code ${response.code}")

      val json = JSONObject(response.body?.string() ?: return null)
      val result = json.optString("result", null) ?: return null

      // Escape the $ characters in the delimiters
      val dataLines = result.substringAfter("\$\$SOE").substringBefore("\$\$EOE").trim().lines()
      if (dataLines.isEmpty()) return null

      // Skip header lines if any
      val dataLine =
          dataLines.firstOrNull { it.isNotBlank() && !it.startsWith("Date") } ?: return null

      // Split the data line by commas and trim spaces
      val columns = dataLine.split(",").map { it.trim() }

      // According to the documentation, columns are:
      // 0: Date/Time
      // 1: R.A. (deg)
      // 2: DEC (deg)
      if (columns.size >= 3) {
        val raDeg = columns[1].toDoubleOrNull() ?: return null
        val decDeg = columns[2].toDoubleOrNull() ?: return null
        return Pair(raDeg, decDeg)
      }
    }
    return null
  }
}
