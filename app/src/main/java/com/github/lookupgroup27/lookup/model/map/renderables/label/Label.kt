package com.github.lookupgroup27.lookup.model.map.renderables.label

/**
 * Data class representing a label in the skymap.
 *
 * @property text The text of the label.
 * @property position The position of the label in 3D space (x, y, z).
 * @property textureId The OpenGL texture ID for the label's bitmap (optional).
 */
data class Label(
    val text: String,
    val position: FloatArray,
    var textureId: Int? = null // Optional texture ID for OpenGL
)
