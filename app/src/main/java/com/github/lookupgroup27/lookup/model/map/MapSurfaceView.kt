package com.github.lookupgroup27.lookup.model.map

import android.content.Context
import android.opengl.GLSurfaceView

/**
 * Our GLSurfaceView for rendering the map.
 *
 * @param context The context of the application.
 * @param renderer The renderer to use for drawing on the GLSurfaceView.
 */
class MapSurfaceView(
    context: Context,
    renderer: MapRenderer
) : GLSurfaceView(context) {

  init {
    // Create an OpenGL ES 2.0 context
    setEGLContextClientVersion(2)

    // Set the provided renderer for drawing on the GLSurfaceView
    renderer.updateContext(context)
    setRenderer(renderer)
  }
}
