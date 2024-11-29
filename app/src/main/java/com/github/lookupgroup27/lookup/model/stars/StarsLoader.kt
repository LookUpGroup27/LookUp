package com.github.lookupgroup27.lookup.model.loader

import com.github.lookupgroup27.lookup.model.map.renderables.Star
import com.github.lookupgroup27.lookup.model.stars.StarDataRepository

/**
 * Converts star data into renderable objects for OpenGL rendering.
 *
 * @param repository The repository managing star data and computations.
 */
class StarsLoader(private val repository: StarDataRepository) {

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
                x = starData.position.first,
                y = starData.position.second,
                z = starData.position.third,
                color = starData.color,
                vertexShaderCode = "TODO_VERTEX_SHADER", // Replace with actual shader code
                fragmentShaderCode = "TODO_FRAGMENT_SHADER" // Replace with actual shader code
            )
        }
    }
}
