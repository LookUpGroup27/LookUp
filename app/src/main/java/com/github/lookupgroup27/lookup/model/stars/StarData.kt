package com.github.lookupgroup27.lookup.model.stars

/**
 * Represents the properties of a star.
 *
 * @param name The name of the star.
 * @param position The 3D Cartesian coordinates of the star (x, y, z).
 * @param size The size of the star for rendering.
 * @param color The RGB color of the star.
 * @param magnitude The magnitude (brightness) of the star.
 * @param spectralClass Optional spectral classification of the star.
 */
data class StarData(
    val name: String,
    val position: Triple<Float, Float, Float>,
    val size: Float,
    val color: FloatArray = floatArrayOf(1.0f, 1.0f, 1.0f), // Default white color
    val magnitude: Double,
    val spectralClass: String? = null
)
