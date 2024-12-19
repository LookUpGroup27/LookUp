package com.github.lookupgroup27.lookup.model.map.stars

import android.content.Context
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.*

class StarsLoaderTest {

  private lateinit var mockContext: Context
  private lateinit var mockRepository: StarDataRepository
  private lateinit var starsLoader: StarsLoader

  @Before
  fun setUp() {
    mockContext = mock(Context::class.java)
    mockRepository = mock(StarDataRepository::class.java)
    starsLoader = StarsLoader(mockContext, mockRepository)
  }

  @Test
  fun testLoadStars_callsUpdateStarPositions() {
    // Arrange: Set up the mock repository
    `when`(mockRepository.getUpdatedStars()).thenReturn(emptyList())

    // Act: Call loadStars
    starsLoader.loadStars()

    // Assert: Verify that updateStarPositions was called
    verify(mockRepository).updateStarPositions()
  }

  @Test
  fun testLoadStars_handlesEmptyStarDataList() {
    // Arrange: Return an empty star data list
    `when`(mockRepository.getUpdatedStars()).thenReturn(emptyList())

    // Act: Call loadStars
    val stars = starsLoader.loadStars()

    // Assert: Verify the result is an empty list
    assertEquals(0, stars.size)
  }
}
