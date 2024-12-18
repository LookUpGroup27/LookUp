package com.github.lookupgroup27.lookup.model.map

import PlanetsRepository
import android.content.Context
import com.github.lookupgroup27.lookup.model.map.stars.StarDataRepository
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.mock

class MapRendererTest {

  private lateinit var mapRenderer: MapRenderer

  @Before
  fun setUp() {
    // Initialize MapRenderer with a mock or test context
    val mockContext = mock(Context::class.java)
    val mockStarDataRepository = mock(StarDataRepository::class.java)
    val mockPlanetsRepository = mock(PlanetsRepository::class.java)
    mapRenderer = MapRenderer(mockContext, mockStarDataRepository, mockPlanetsRepository, 45f)
  }

  @Test
  fun `computeDeltaTime should return correct delta time`() {
    // Arrange: Inject a fixed time provider
    var fakeTime = 1000L
    mapRenderer.timeProvider = { fakeTime }

    // Simulate first frame
    mapRenderer.computeDeltaTime() // Initializes lastFrameTime

    // Act: Advance time and compute deltaTime
    fakeTime += 200 // Simulate 200 milliseconds later
    val deltaTime = mapRenderer.computeDeltaTime()

    // Assert
    assertEquals("Delta time should be 0.2 seconds", 0.2f, deltaTime, 1e-6f)
  }
}
