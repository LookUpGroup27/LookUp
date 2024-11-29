package com.github.lookupgroup27.lookup.model.stars

import android.content.Context
import android.content.res.AssetManager
import java.io.ByteArrayInputStream
import java.io.InputStream
import org.junit.Assert.assertEquals
import org.junit.Test
import org.mockito.Mockito

class StarDataRepositoryTest {

  private val repository = StarDataRepository()
  private val tolerance = 0.01f // Tolerance for floating-point comparisons

  @Test
  fun `test getStars parses CSV correctly and computes properties`() {
    // Mock CSV content
    val mockFileContent =
        """
        id,hip,hd,hr,gl,bf,proper,ra,dec,dist,pmra,pmdec,rv,mag,absmag,spect,ci,x,y,z,vx,vy,vz,rarad,decrad,pmrarad,pmdecrad,bayer,flam,con,comp,comp_primary,base,var,var_min,var_max
        1,,,Gl 1,,,StarA,0.0,0.0,2.0,0.0,0.0,0.0,5.20,,K1V,,1.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,,,,,,,,
        2,,,Gl 2,,,StarB,90.0,0.0,2.0,0.0,0.0,0.0,6.20,,G2V,,0.1,0.1,0.1,0.0,0.0,0.0,0.0,0.0,0.0,,,,,,,,
        3,,,Gl 3,,,StarC,0.0,90.0,1.0,0.0,0.0,0.0,4.00,,F3V,,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,,,,,,,,
    """
            .trimIndent()

    // Mock InputStream to simulate file reading
    val mockInputStream: InputStream = ByteArrayInputStream(mockFileContent.toByteArray())

    // Mock AssetManager
    val mockAssetManager =
        Mockito.mock(AssetManager::class.java).apply {
          Mockito.`when`(open(Mockito.anyString())).thenReturn(mockInputStream)
        }

    // Mock Context
    val mockContext =
        Mockito.mock(Context::class.java).apply {
          Mockito.`when`(assets).thenReturn(mockAssetManager)
        }

    // Call the repository method
    val stars = repository.getStars(mockContext, "mock.csv")

    // Tolerance for floating-point comparisons
    val tolerance = 0.01f

    // Assertions for StarA
    assertEquals("StarA", stars[0].name)
    assertEquals(1.0f, stars[0].position.first, tolerance) // X for RA=0, DEC=0
    assertEquals(0.0f, stars[0].position.second, tolerance) // Y for RA=0, DEC=0
    assertEquals(0.0f, stars[0].position.third, tolerance) // Z for RA=0, DEC=0
    assertEquals(0.503f, stars[0].size, tolerance) // Size based on magnitude
    assertEquals("K1V", stars[0].spectralClass)

    // Assertions for StarB
    assertEquals("StarB", stars[1].name)
    assertEquals(0.0f, stars[1].position.first, tolerance) // X for RA=90, DEC=0
    assertEquals(1.0f, stars[1].position.second, tolerance) // Y for RA=90, DEC=0
    assertEquals(0.0f, stars[1].position.third, tolerance) // Z for RA=90, DEC=0
    assertEquals(0.501f, stars[1].size, tolerance) // Size based on magnitude
    assertEquals("G2V", stars[1].spectralClass)

    // Assertions for StarC
    assertEquals("StarC", stars[2].name)
    assertEquals(0.0f, stars[2].position.first, tolerance) // X for RA=0, DEC=90
    assertEquals(0.0f, stars[2].position.second, tolerance) // Y for RA=0, DEC=90
    assertEquals(1.0f, stars[2].position.third, tolerance) // Z for RA=0, DEC=90
    assertEquals(0.536f, stars[2].size, tolerance) // Size based on magnitude
    assertEquals("F3V", stars[2].spectralClass)
  }

  @Test
  fun `test convertToCartesian computes Cartesian coordinates accurately`() {
    val radius = 1.0f
    val testCases =
        listOf(
            Triple(0.0, 0.0, Triple(1.0f, 0.0f, 0.0f)), // RA=0, DEC=0
            Triple(90.0, 0.0, Triple(0.0f, 1.0f, 0.0f)), // RA=90, DEC=0
            Triple(180.0, 0.0, Triple(-1.0f, 0.0f, 0.0f)), // RA=180, DEC=0
            Triple(270.0, 0.0, Triple(0.0f, -1.0f, 0.0f)), // RA=270, DEC=0
            Triple(0.0, 90.0, Triple(0.0f, 0.0f, 1.0f)), // RA=0, DEC=90
            Triple(0.0, -90.0, Triple(0.0f, 0.0f, -1.0f)) // RA=0, DEC=-90
            )

    for ((ra, dec, expected) in testCases) {
      val (x, y, z) = repository.convertToCartesian(ra, dec, radius)
      assertEquals("X mismatch for RA=$ra, DEC=$dec", expected.first, x, tolerance)
      assertEquals("Y mismatch for RA=$ra, DEC=$dec", expected.second, y, tolerance)
      assertEquals("Z mismatch for RA=$ra, DEC=$dec", expected.third, z, tolerance)
    }
  }
}
