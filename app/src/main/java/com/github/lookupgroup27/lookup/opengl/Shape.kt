package com.github.lookupgroup27.lookup.opengl

const val COORDS_PER_VERTEX = 3

abstract class Shape {
    abstract val color: FloatArray

    abstract fun draw()
}