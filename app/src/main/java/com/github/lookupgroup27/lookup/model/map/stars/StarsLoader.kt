package com.github.lookupgroup27.lookup.model.loader

import android.content.Context
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
          size = 0.2f)
    }
  }
}
