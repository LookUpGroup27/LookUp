package com.github.lookupgroup27.lookup.model.map.planets

/**
 * Data class for planet information.
 */
data class PlanetData(
    val name: String,
    val id: String,
    var ra: Double = 0.0, // Right Ascension in degrees
    var dec: Double = 0.0, // Declination in degrees
    var cartesian: Triple<Float, Float, Float> = Triple(0.0f, 0.0f, 0.0f) // Cartesian coordinates
)