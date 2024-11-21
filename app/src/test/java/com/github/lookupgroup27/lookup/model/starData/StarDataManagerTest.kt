package com.github.lookupgroup27.lookup.model.starData

import android.content.Context
import android.content.res.AssetManager
import com.github.lookupgroup27.lookup.model.stars.StarDataManager
import java.io.ByteArrayInputStream
import java.io.InputStream
import org.junit.Assert.assertEquals
import org.junit.Test
import org.mockito.Mockito

class StarDataManagerTest {
    private val starDataManager = StarDataManager()

    @Test
    fun `test loadStars parses CSV correctly`() {
        // Mock file content
        val mockFileContent =
            """
                id,hip,hd,hr,gl,bf,proper,ra,dec,dist,pmra,pmdec,rv,mag,absmag,spect,ci,x,y,z,vx,vy,vz,rarad,decrad,pmrarad,pmdecrad,bayer,flam,con,comp,comp_primary,base,var,var_min,var_max
                1,,,Gl 1,,,StarA,0.000,1.000,2.000,0.000,0.000,0.000,5.20,,K1V,,1.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,,,,,,,,
                2,,,Gl 2,,,StarB,10.000,10.000,2.000,0.000,0.000,0.000,6.20,,G2V,,0.1,0.1,0.1,0.0,0.0,0.0,0.0,0.0,0.0,,,,,,,,
            """.trimIndent()

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

        // Call the function with mocked context
        val stars = starDataManager.loadStars(mockContext, "mock.csv")

        // Validate the results
        assertEquals(2, stars.size)
        assertEquals("StarA", stars[0].name)
        assertEquals(0.0, stars[0].ra, 0.01)
        assertEquals(1.0, stars[0].dec, 0.01)
        assertEquals(5.2, stars[0].mag, 0.01)
        assertEquals("K1V", stars[0].spect)

        assertEquals("StarB", stars[1].name)
        assertEquals(10.0, stars[1].ra, 0.01)
        assertEquals(10.0, stars[1].dec, 0.01)
        assertEquals(6.2, stars[1].mag, 0.01)
        assertEquals("G2V", stars[1].spect)
    }

    @Test
    fun `test convertToCartesian converts coordinates correctly`() {
        val radius = 1.0f
        val testCases = listOf(
            Triple(0.0, 0.0, Triple(1.0f, 0.0f, 0.0f)),
            Triple(90.0, 0.0, Triple(0.0f, 1.0f, 0.0f)),
            Triple(180.0, 0.0, Triple(-1.0f, 0.0f, 0.0f)),
            Triple(270.0, 0.0, Triple(0.0f, -1.0f, 0.0f)),
            Triple(0.0, 90.0, Triple(0.0f, 0.0f, 1.0f)),
            Triple(0.0, -90.0, Triple(0.0f, 0.0f, -1.0f)),
            Triple(360.0, 0.0, Triple(1.0f, 0.0f, 0.0f)),
            Triple(45.0, 45.0, Triple(0.5f, 0.5f, 0.7071f)),
            Triple(135.0, -45.0, Triple(-0.5f, 0.5f, -0.7071f))
        )

        for ((ra, dec, expected) in testCases) {
            val (x, y, z) = starDataManager.convertToCartesian(ra, dec, radius)
            assertEquals("X coordinate should match for RA=$ra DEC=$dec", expected.first, x, 0.001f)
            assertEquals("Y coordinate should match for RA=$ra DEC=$dec", expected.second, y, 0.001f)
            assertEquals("Z coordinate should match for RA=$ra DEC=$dec", expected.third, z, 0.001f)
        }
    }

    @Test
    fun `test convertToCartesian handles zero radius`() {
        val radius = 0.0f
        val (x, y, z) = starDataManager.convertToCartesian(45.0, 45.0, radius)
        assertEquals("X should be 0 for zero radius", 0.0f, x, 0.001f)
        assertEquals("Y should be 0 for zero radius", 0.0f, y, 0.001f)
        assertEquals("Z should be 0 for zero radius", 0.0f, z, 0.001f)
    }

    @Test
    fun `test convertToCartesian handles negative DEC values`() {
        val radius = 1.0f
        val testCases = listOf(
            Triple(0.0, -45.0, Triple(0.7071f, 0.0f, -0.7071f)),
            Triple(90.0, -45.0, Triple(0.0f, 0.7071f, -0.7071f))
        )

        for ((ra, dec, expected) in testCases) {
            val (x, y, z) = starDataManager.convertToCartesian(ra, dec, radius)
            assertEquals("X coordinate should match for RA=$ra DEC=$dec", expected.first, x, 0.001f)
            assertEquals("Y coordinate should match for RA=$ra DEC=$dec", expected.second, y, 0.001f)
            assertEquals("Z coordinate should match for RA=$ra DEC=$dec", expected.third, z, 0.001f)
        }
    }
}