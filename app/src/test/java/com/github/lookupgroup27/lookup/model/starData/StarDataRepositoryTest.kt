package com.github.lookupgroup27.lookup.model.map.stars

import android.content.Context
import android.content.res.AssetManager
import android.location.Location
import androidx.test.core.app.ApplicationProvider
import com.github.lookupgroup27.lookup.model.location.LocationProvider
import com.github.lookupgroup27.lookup.utils.CelestialObjectsUtils
import com.google.firebase.FirebaseApp
import java.io.ByteArrayInputStream
import java.io.InputStream
import java.time.LocalDateTime
import java.time.ZoneOffset
import junit.framework.TestCase.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class StarDataRepositoryTest {

  private lateinit var repository: StarDataRepository
  private lateinit var mockContext: Context
  private lateinit var mockAssetManager: AssetManager
  private lateinit var mockLocationProvider: TestLocationProvider
  private lateinit var context: Context

  private val tolerance = 0.01f // Tolerance for floating-point comparisons

  @Before
  fun setup() {
    // Initialize Firebase
    context = ApplicationProvider.getApplicationContext()
    if (FirebaseApp.getApps(context).isEmpty()) {
      FirebaseApp.initializeApp(context)
    }

    // Mock Context and AssetManager
    mockAssetManager = Mockito.mock(AssetManager::class.java)
    mockContext = Mockito.mock(Context::class.java)

    // Use doReturn to mock the 'assets' property
    Mockito.doReturn(mockAssetManager).`when`(mockContext).assets

    // Initialize TestLocationProvider with context
    mockLocationProvider = TestLocationProvider(context)

    // Initialize the repository
    repository = StarDataRepository(mockContext, mockLocationProvider)
  }

  @Test
  fun `test loadStarsFromCSV parses CSV correctly and computes properties`() {
    // Mock CSV content
    val mockFileContent =
        """
            id,hip,hd,hr,gl,bf,proper,ra,dec,dist,pmra,pmdec,rv,mag,absmag,spect,ci,x,y,z
            1,0,0,0,Gl 1,,StarA,0.0,0.0,2.0,0.0,0.0,0.0,5.20,0.0,K1V,0.0,1.0,0.0,0.0
            2,0,0,0,Gl 2,,StarB,90.0,0.0,2.0,0.0,0.0,0.0,6.20,0.0,G2V,0.0,0.1,0.1,0.1
            3,0,0,0,Gl 3,,StarC,0.0,90.0,1.0,0.0,0.0,0.0,4.00,0.0,F3V,0.0,0.0,0.0,1.0
        """
            .trimIndent()

    val mockInputStream: InputStream = ByteArrayInputStream(mockFileContent.toByteArray())
    Mockito.doReturn(mockInputStream).`when`(mockAssetManager).open(Mockito.anyString())

    // Load stars
    repository.loadStarsFromCSV("mock.csv")
    val stars = repository.getUpdatedStars()

    // Assertions for StarA
    val starA = stars[0]
    assertEquals("StarA", starA.name)
    assertEquals(0.0, starA.ra, tolerance.toDouble())
    assertEquals(0.0, starA.dec, tolerance.toDouble())
    assertEquals(2.0, starA.dist, tolerance.toDouble())
    assertEquals(5.20, starA.magnitude, tolerance.toDouble())
    assertEquals(1.0, starA.x, tolerance.toDouble())
    assertEquals(0.0, starA.y, tolerance.toDouble())
    assertEquals(0.0, starA.z, tolerance.toDouble())

    // Assertions for StarB and StarC...
  }

  @Test
  fun `test updateStarPositions updates Cartesian coordinates`() {
    // Set the mock location
    val observerLatitude = 46.51852 // Lausanne, Switzerland
    val observerLongitude = 6.56188
    mockLocationProvider.setLocation(observerLatitude, observerLongitude)

    // Set the system time to a fixed value
    val fixedDateTime = LocalDateTime.of(2023, 1, 1, 0, 0)
    val fixedTimeMillis = fixedDateTime.toInstant(ZoneOffset.UTC).toEpochMilli()

    // Mock CSV content
    val mockFileContent =
        """
        id,hip,hd,hr,gl,bf,proper,ra,dec,dist,pmra,pmdec,rv,mag,absmag,spect,ci,x,y,z
        1,0,0,0,Gl 1,,StarA,0.0,0.0,2.0,0.0,0.0,0.0,5.20,0.0,K1V,0.0,0.0,0.0,0.0
        2,0,0,0,Gl 2,,StarB,90.0,0.0,3.0,0.0,0.0,0.0,6.20,0.0,G2V,0.0,0.0,0.0,0.0
    """
            .trimIndent()

    val mockInputStream: InputStream = ByteArrayInputStream(mockFileContent.toByteArray())
    Mockito.doReturn(mockInputStream).`when`(mockAssetManager).open(Mockito.anyString())

    // Load stars
    repository.loadStarsFromCSV("mock.csv")

    // Update star positions
    repository.updateStarPositions()

    val stars = repository.getUpdatedStars()

    // Compute expected positions
    val lst = CelestialObjectsUtils.computeSiderealTime(observerLongitude)

    for (star in stars) {
      val (azimuth, altitude) =
          CelestialObjectsUtils.convertToHorizonCoordinates(
              ra = star.ra, dec = star.dec, latitude = observerLatitude, localSiderealTime = lst)

      val expectedPosition =
          CelestialObjectsUtils.convertToCartesian(azimuth = azimuth, altitude = altitude)

      // Assertions
      assertEquals(expectedPosition.first.toDouble(), star.x, tolerance.toDouble())
      assertEquals(expectedPosition.second.toDouble(), star.y, tolerance.toDouble())
      assertEquals(expectedPosition.third.toDouble(), star.z, tolerance.toDouble())
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
