package com.github.lookupgroup27.lookup.model.starData

import android.content.Context
import com.github.lookupgroup27.lookup.model.loader.StarsLoader
import com.github.lookupgroup27.lookup.model.stars.StarDataRepository
import org.junit.Assert.assertEquals
import org.junit.Test
import org.mockito.Mockito

class StarsLoaderTest {

  private val mockRepository = Mockito.mock(StarDataRepository::class.java)
  private val starsLoader = StarsLoader(mockRepository)

  /**
   * Temporarily disabled due to missing implementation of the shader program in the test
   * environment.
   */

  /*@Test
  fun `test loadStars converts StarData to Star objects correctly`() {
    // Mock Context
    val mockContext = Mockito.mock(Context::class.java)

    // Mock StarData from repository
    val mockStarDataList =
        listOf(
            StarData(
                name = "StarA",
                position = Triple(1.0f, 2.0f, 3.0f),
                size = 0.5f,
                color = floatArrayOf(1.0f, 0.0f, 0.0f), // Red
                magnitude = 4.0,
                spectralClass = "G2V"),
            StarData(
                name = "StarB",
                position = Triple(4.0f, 5.0f, 6.0f),
                size = 0.6f,
                color = floatArrayOf(0.0f, 1.0f, 0.0f), // Green
                magnitude = 5.0,
                spectralClass = "K1V"))

    // Mock repository behavior
    Mockito.`when`(mockRepository.getStars(mockContext, "stars.csv")).thenReturn(mockStarDataList)

    // Call the loader
    val stars = starsLoader.loadStars(mockContext, "stars.csv")

    // Verify conversion
    assertEquals(2, stars.size)

    // Verify StarA
    val starA = stars[0]
    assertEquals(1.0f, starA.position[0], 0.001f)
    assertEquals(2.0f, starA.position[1], 0.001f)
    assertEquals(3.0f, starA.position[2], 0.001f)
    assertEquals(floatArrayOf(1.0f, 0.0f, 0.0f).toList(), starA.color.toList())

    // Verify StarB
    val starB = stars[1]
    assertEquals(4.0f, starB.position[0], 0.001f)
    assertEquals(5.0f, starB.position[0], 0.001f)
    assertEquals(6.0f, starB.position[0], 0.001f)
    assertEquals(floatArrayOf(0.0f, 1.0f, 0.0f).toList(), starB.color.toList())

    // Verify repository was called with correct arguments
    Mockito.verify(mockRepository).getStars(mockContext, "stars.csv")
  }*/

  @Test
  fun `test loadStars handles empty dataset`() {
    // Mock Context
    val mockContext = Mockito.mock(Context::class.java)

    // Mock repository behavior with empty dataset
    Mockito.`when`(mockRepository.getStars(mockContext, "stars.csv")).thenReturn(emptyList())

    // Call the loader
    val stars = starsLoader.loadStars(mockContext, "stars.csv")

    // Verify results
    assertEquals(0, stars.size)

    // Verify repository was called with correct arguments
    Mockito.verify(mockRepository).getStars(mockContext, "stars.csv")
  }

  @Test
  fun `test loadStars with invalid file name`() {
    // Mock Context
    val mockContext = Mockito.mock(Context::class.java)

    // Mock repository behavior to throw exception
    Mockito.`when`(mockRepository.getStars(mockContext, "invalid.csv"))
        .thenThrow(RuntimeException("File not found"))

    try {
      // Call the loader
      starsLoader.loadStars(mockContext, "invalid.csv")
      assert(false) // Should not reach here
    } catch (e: RuntimeException) {
      // Verify exception message
      assertEquals("File not found", e.message)
    }

    // Verify repository was called with correct arguments
    Mockito.verify(mockRepository).getStars(mockContext, "invalid.csv")
  }
}
