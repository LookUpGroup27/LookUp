package com.github.lookupgroup27.lookup.model.starData

/**
 * Temporarily disabled due to missing implementation of the shader program in the test environment.
 */

/*
class StarsLoaderTest {

    private val mockContext = Mockito.mock(Context::class.java)
    private val mockRepository = Mockito.mock(StarDataRepository::class.java)
    private val starsLoader = StarsLoader(mockContext, mockRepository)

    @Test
    fun `test loadStars converts StarData to Star objects correctly`() {
        // Prepare mock data
        val mockStarDataList = listOf(
            StarData(
                name = "StarA",
                ra = 15.0,
                dec = -20.0,
                dist = 10.0,
                x = 1.0,
                y = 2.0,
                z = 3.0,
                magnitude = 4.0,
                spectralClass = "G2V"
            ),
            StarData(
                name = "StarB",
                ra = 30.0,
                dec = 40.0,
                dist = 20.0,
                x = 4.0,
                y = 5.0,
                z = 6.0,
                magnitude = 5.0,
                spectralClass = "K1V"
            )
        )

        // Mock repository methods
        Mockito.doNothing().`when`(mockRepository).updateStarPositions()
        Mockito.`when`(mockRepository.getUpdatedStars()).thenReturn(mockStarDataList)

        // Call the method under test
        val stars = starsLoader.loadStars()

        // Verify the results
        assertEquals(2, stars.size)

        // Expected colors based on spectral classes
        val expectedColorStarA = floatArrayOf(1.0f, 1.0f, 0.6f) // Yellow-white (G class)
        val expectedColorStarB = floatArrayOf(1.0f, 0.8f, 0.5f) // Orange (K class)

        // Verify StarA
        val starA = stars[0]
        assertEquals(1.0f, starA.position[0], 0.001f)
        assertEquals(2.0f, starA.position[1], 0.001f)
        assertEquals(3.0f, starA.position[2], 0.001f)
        assertEquals(expectedColorStarA.toList(), starA.color.toList())

        // Verify StarB
        val starB = stars[1]
        assertEquals(4.0f, starB.position[0], 0.001f)
        assertEquals(5.0f, starB.position[1], 0.001f)
        assertEquals(6.0f, starB.position[2], 0.001f)
        assertEquals(expectedColorStarB.toList(), starB.color.toList())

        // Verify that repository methods were called
        Mockito.verify(mockRepository).updateStarPositions()
        Mockito.verify(mockRepository).getUpdatedStars()
    }
}*/
