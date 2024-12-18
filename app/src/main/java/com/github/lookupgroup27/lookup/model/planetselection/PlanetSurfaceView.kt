package com.github.lookupgroup27.lookup.model.planetselection

import android.annotation.SuppressLint
import android.content.Context
import android.opengl.GLSurfaceView
import com.github.lookupgroup27.lookup.model.map.planets.PlanetData

/**
 * A custom GLSurfaceView that renders the currently selected planet using OpenGL.
 *
 * @param context The application context.
 * @param planet The currently selected planet to render.
 */
@SuppressLint("ViewConstructor")
class PlanetSurfaceView(context: Context, private var planet: PlanetData) : GLSurfaceView(context) {

  private val planetRenderer: PlanetRenderer

  init {
    // Set OpenGL ES version
    setEGLContextClientVersion(2)

    // Initialize the PlanetRenderer
    planetRenderer = PlanetRenderer(context, planet)
    setRenderer(planetRenderer)

    // Render only when the content changes
    renderMode = RENDERMODE_CONTINUOUSLY
  }

  /**
   * Update the currently displayed planet and notify the renderer.
   *
   * @param newPlanet The new planet data to render.
   */
  fun updatePlanet(newPlanet: PlanetData) {
    planet = newPlanet
    queueEvent { planetRenderer.updatePlanet(newPlanet) }
  }
}
