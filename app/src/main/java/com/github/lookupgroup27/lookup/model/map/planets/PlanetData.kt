package com.github.lookupgroup27.lookup.model.map.planets

/**
 * Data class for planet information.
 *
 * Each [PlanetData] object holds:
 * - The planet's name and a unique Horizons API ID.
 * - Its current Right Ascension (RA) and Declination (Dec) in degrees.
 * - Its current Cartesian coordinates (x, y, z) for rendering in 3D space.
 * - A texture resource ID for rendering the planet's surface.
 */
data class PlanetData(
    val name: String,
    val id: String,
    var ra: Double = 0.0, // Right Ascension in degrees
    var dec: Double = 0.0, // Declination in degrees
    var cartesian: Triple<Float, Float, Float> = Triple(0.0f, 0.0f, 0.0f), // Cartesian coordinates
    val textureId: Int
)
