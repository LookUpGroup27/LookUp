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
          color =
              starData.spectralClass?.let { getColorForSpectralClass(it) }
                  ?: floatArrayOf(1.0f, 1.0f, 1.0f), // Default to white if no spectral class
          size = 0.3f, // Use default size
          segments = CircleRenderer.DEFAULT_SEGMENTS, // Default segments value for rendering
          vertexShaderCode = "TODO_VERTEX_SHADER",
          fragmentShaderCode = "TODO_FRAGMENT_SHADER")
    }
  }

  /**
   * Determines the color of a star based on its spectral class.
   *
   * @param spectralClass The spectral class of the star.
   * @return A float array representing the RGB color of the star.
   */
  private fun getColorForSpectralClass(spectralClass: String): FloatArray {
    return when (spectralClass.firstOrNull()) {
      'O' -> floatArrayOf(0.6f, 0.7f, 1.0f) // Blue
      'B' -> floatArrayOf(0.7f, 0.8f, 1.0f) // Light Blue
      'A' -> floatArrayOf(0.9f, 0.9f, 1.0f) // White-blue
      'F' -> floatArrayOf(1.0f, 1.0f, 0.8f) // White
      'G' -> floatArrayOf(1.0f, 1.0f, 0.6f) // Yellow-white
      'K' -> floatArrayOf(1.0f, 0.8f, 0.5f) // Orange
      'M' -> floatArrayOf(1.0f, 0.5f, 0.5f) // Red
      else -> floatArrayOf(1.0f, 1.0f, 1.0f) // Default to white
    }
  }
}
