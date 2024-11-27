package com.github.lookupgroup27.lookup.model.map

import android.content.Context
import android.opengl.GLSurfaceView

class MapSurfaceView(
    context: Context,
    renderer: com.github.lookupgroup27.lookup.model.map.Renderer
) : GLSurfaceView(context) {

  init {
    // Create an OpenGL ES 2.0 context
    setEGLContextClientVersion(2)

    // Set the provided renderer for drawing on the GLSurfaceView
    renderer.updateContext(context)
    setRenderer(renderer)
  }
}
