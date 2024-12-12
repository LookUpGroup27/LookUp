package com.github.lookupgroup27.lookup.model.loader

import android.content.Context
import com.github.lookupgroup27.lookup.model.map.renderables.CircleRenderer
import com.github.lookupgroup27.lookup.model.map.renderables.Star
import com.github.lookupgroup27.lookup.model.map.stars.StarDataRepository

/**
 * Converts star data into renderable objects for OpenGL rendering.
 *
 * @param context The Android context required for Star objects.
 * @param repository The repository managing star data and computations.
 */
class StarsLoader(private val context: Context, private val repository: StarDataRepository) {

  /**
   * Converts star data from the repository into renderable [Star] objects.
   *
   * @return A list of renderable [Star] objects.
   */
  fun loadStars(): List<Star> {
    // Ensure star positions are updated before rendering
    repository.updateStarPositions()

    // Convert StarData to OpenGL-compatible Star objects
    return repository.getUpdatedStars().map { starData ->
      Star(
          context = context,
          position = floatArrayOf(starData.x.toFloat(), starData.y.toFloat(), starData.z.toFloat()),
          color = floatArrayOf(1.0f, 1.0f, 1.0f), // Default
          size = 0.3f, // Use default size
          segments = CircleRenderer.DEFAULT_SEGMENTS, // Default segments value for rendering
          vertexShaderCode = "TODO_VERTEX_SHADER",
          fragmentShaderCode = "TODO_FRAGMENT_SHADER")
    }
  }
}
