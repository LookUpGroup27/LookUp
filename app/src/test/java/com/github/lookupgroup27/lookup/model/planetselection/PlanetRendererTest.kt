package com.github.lookupgroup27.lookup.model.planetselection

import android.content.Context
import com.github.lookupgroup27.lookup.model.map.planets.PlanetData
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.*

class PlanetRendererTest {

  private lateinit var context: Context
  private lateinit var planetData: PlanetData
  private lateinit var renderer: PlanetRenderer

  @Before
  fun setUp() {
    // Mock context and PlanetData
    context = mock(Context::class.java)
    planetData = PlanetData(name = "Mars", "301", iconRes = 0, textureId = 1)

    // Initialize PlanetRenderer with mocked context and planetData
    renderer = PlanetRenderer(context, planetData)
  }

  @Test
  fun `updatePlanet updates planetData and reinitializes Planet`() {
    // New PlanetData to update
    val newPlanetData = PlanetData(name = "Mars", "301", iconRes = 1, textureId = 2)

    // Call updatePlanet
    renderer.updatePlanet(newPlanetData)

    // Verify that planetData is updated
    val privatePlanetDataField = renderer.javaClass.getDeclaredField("planetData")
    privatePlanetDataField.isAccessible = true
    val updatedPlanetData = privatePlanetDataField.get(renderer) as PlanetData

    assert(updatedPlanetData.name == "Mars") { "PlanetData name mismatch after update" }
    assert(updatedPlanetData.textureId == 2) { "PlanetData texture ID mismatch after update" }
  }
}
