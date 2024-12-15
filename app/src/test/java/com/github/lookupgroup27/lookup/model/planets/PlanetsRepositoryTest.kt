import android.content.Context
import android.location.Location
import androidx.test.core.app.ApplicationProvider
import com.github.lookupgroup27.lookup.model.location.LocationProvider
import com.github.lookupgroup27.lookup.model.map.planets.PlanetData
import com.google.firebase.FirebaseApp
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertNotNull
import junit.framework.TestCase.fail
import okhttp3.OkHttpClient
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class PlanetsRepositoryTest {

  private lateinit var repository: PlanetsRepository
  private lateinit var mockLocationProvider: TestLocationProvider
  private lateinit var mockWebServer: MockWebServer
  private val tolerance = 0.01 // Tolerance for floating-point comparisons
  private lateinit var context: Context

  @Before
  fun setup() {
    // Initialize MockWebServer
    mockWebServer = MockWebServer()
    mockWebServer.start()

    context = ApplicationProvider.getApplicationContext()
    if (FirebaseApp.getApps(context).isEmpty()) {
      FirebaseApp.initializeApp(context)
    }

    // Initialize TestLocationProvider
    mockLocationProvider = TestLocationProvider(context)
    // Set a test location
    val observerLatitude = 46.51852 // Lausanne, Switzerland
    val observerLongitude = 6.56188
    mockLocationProvider.setLocation(observerLatitude, observerLongitude)

    // Initialize PlanetsRepository with the mock client and base URL
    repository =
        PlanetsRepository(
            locationProvider = mockLocationProvider,
            client = OkHttpClient(),
            baseUrl = mockWebServer.url("/").toString())

    // Option 1: Limit the planets list to only Mars
    repository.planets.clear()
    repository.planets.add(PlanetData("Mars", "499", textureId = 0))
  }

  @After
  fun tearDown() {
    mockWebServer.shutdown()
  }

  @Test
  fun `test updatePlanetsData updates planetary positions`() {
    // Prepare mock response for Mars
    val soe = "$$" + "SOE"
    val eoe = "$$" + "EOE"
    val mockApiResponseMars =
        """
            {
              "signature": {
                "version": "1.0",
                "source": "NASA/JPL Horizons API"
              },
              "result": "
        *******************************************************************************
        Ephemeris / API_USER Sat Oct 2 00:00:00 2021 Pasadena, USA / Horizons
        *******************************************************************************
        Target body name: Mars (499)                      {source: mar097}
        Center body name: Earth (399)                     {source: mar097}
        Center-site name: coord@399
        *******************************************************************************
        Start time      : A.D. 2021-Oct-02 00:00:00.0000 UT
        Stop  time      : A.D. 2021-Oct-02 00:00:00.0000 UT
        Step-size       : 1 minutes
        *******************************************************************************
        Target pole/equ : IAU_MARS                        {East-longitude positive}
        Target radii    : 3396.19 x 3396.19 x 3376.20 km  {Equator, meridian, pole}
        Center geodetic : 0.000000, 0.000000, 0.000       {W-lon(deg),Lat(deg),Alt(km)}
        Center cylindric: 0.000000, 0.000, 0.000          {W-lon(deg),Dxy(km),Dz(km)}
        Center pole/equ : High-precision EOP model        {East-longitude positive}
        Center radii    : 6378.1366 x 6378.1366 x 6356.7519 km  {Equator, meridian, pole}
        Target primary  : Sun
        Vis. interferer : MOON (R_eq= 1737.4) km          {source: mar097}
        Rel. light bend : Sun, EARTH                      {source: mar097}
        Rel. lght bnd GM: 1.3271E+11, 3.9860E+05 km^3/s^2
        Small perturbers: Ceres, Pallas, Vesta            {source: mar097}
        Atmos refraction: NO (AIRLESS)
        RA format       : DEG
        Time format     : CAL
        Calendar mode   : Gregorian
        Output format   : CSV
        Output units    : KM-S
           
           
        $soe
        2021-Oct-02 00:00:00.00000 * 25.0 -10.0
        $eoe
        *******************************************************************************
                  "
            }
        """
            .trimIndent()

    // Enqueue the mock response
    mockWebServer.enqueue(MockResponse().setResponseCode(200).setBody(mockApiResponseMars))

    // Execute the method under test
    try {
      repository.updatePlanetsData()
    } catch (e: Exception) {
      fail("Exception during updatePlanetsData: ${e.message}")
    }

    // Verify that planets data has been updated
    val planetsCartesian = repository.getPlanetsCartesianCoordinates()
    assertNotNull(planetsCartesian)
    assertEquals(1, planetsCartesian.size) // Only Mars is in the list

    // Check Mars data
    val mars = repository.planets.find { it.id == "499" }
    assertNotNull(mars)

    val expectedRa = 25.0
    val expectedDec = -10.0

    if (mars != null) {
      assertEquals(expectedRa, mars.ra, tolerance)
    }
    if (mars != null) {
      assertEquals(expectedDec, mars.dec, tolerance)
    }
  }

  /** TestLocationProvider allows for manual setting of location values. */
  class TestLocationProvider(context: Context) : LocationProvider(context) {
    fun setLocation(latitude: Double?, longitude: Double?) {
      if (latitude != null && longitude != null) {
        currentLocation.value =
            Location("test").apply {
              this.latitude = latitude
              this.longitude = longitude
            }
      } else {
        currentLocation.value = null
      }
    }
  }
}
