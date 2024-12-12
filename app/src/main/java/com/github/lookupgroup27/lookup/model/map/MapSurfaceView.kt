package com.github.lookupgroup27.lookup.model.map

import android.annotation.SuppressLint
import android.content.Context
import android.opengl.GLSurfaceView
import android.view.MotionEvent
import android.view.ScaleGestureDetector
import android.widget.Toast
import com.github.lookupgroup27.lookup.ui.map.MapViewModel

/**
 * Our GLSurfaceView for rendering the map.
 *
 * @param context The context of the application.
 * @param renderer The renderer to use for drawing on the GLSurfaceView.
 */
class MapSurfaceView(context: Context, private val viewModel: MapViewModel) :
    GLSurfaceView(context) {

  private val scaleGestureDetector: ScaleGestureDetector

  init {
    // Create an OpenGL ES 2.0 context
    setEGLContextClientVersion(2)

    // Set the provided renderer for drawing on the GLSurfaceView
    viewModel.mapRenderer.updateContext(context)
    setRenderer(viewModel.mapRenderer)
    scaleGestureDetector = ScaleGestureDetector(context, viewModel)

    renderMode = RENDERMODE_CONTINUOUSLY

    viewTreeObserver.addOnGlobalLayoutListener {
      if (width > 0 && height > 0) {
        println("GLSurfaceView dimensions: ${width}x${height}")
        requestRender()
      } else {
        println("GLSurfaceView dimensions are still invalid!")
      }
    }
  }

  private fun showPlanetInfo(planetName: String) {
    Toast.makeText(context, "You clicked on $planetName!", Toast.LENGTH_SHORT).show()
    println("You clicked on $planetName!")
  }

  @SuppressLint("ClickableViewAccessibility")
  override fun onTouchEvent(event: MotionEvent): Boolean {
    scaleGestureDetector.onTouchEvent(event)
    if (event.action == MotionEvent.ACTION_DOWN) {
      println("Touch detected at: x=${event.x}, y=${event.y}")
      val planetName = viewModel.mapRenderer.getIntersectedPlanetName(event.x, event.y)
      if (planetName != null) {
        showPlanetInfo(planetName)
      } else {
        println("No planet was clicked.")
      }
      requestRender() // Force re-render after interaction
    }
    return true
  }
}
