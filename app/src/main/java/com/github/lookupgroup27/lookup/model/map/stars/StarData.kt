package com.github.lookupgroup27.lookup.model.map.stars

/**
 * Represents the properties of a star.
 *
 * @param name The common name of the star.
 * @param ra Right Ascension in degrees.
 * @param dec Declination in degrees.
 * @param dist Distance in parsecs.
 * @param x Cartesian x-coordinate of the star.
 * @param y Cartesian y-coordinate of the star.
 * @param z Cartesian z-coordinate of the star.
 * @param magnitude Apparent visual magnitude.
 */
data class StarData(
    val name: String?,
    val ra: Double,
    val dec: Double,
    val dist: Double,
    var x: Double,
    var y: Double,
    var z: Double,
    val magnitude: Double,
)
