package com.github.lookupgroup27.lookup.model.map

import android.annotation.SuppressLint
import android.content.Context
import android.opengl.GLSurfaceView
import android.view.MotionEvent
import android.view.ScaleGestureDetector
import com.github.lookupgroup27.lookup.ui.map.MapViewModel

/**
 * Our GLSurfaceView for rendering the map.
 *
 * @param context The context of the application.
 * @param renderer The renderer to use for drawing on the GLSurfaceView.
 */
class MapSurfaceView(context: Context, viewModel: MapViewModel) : GLSurfaceView(context) {

  private val scaleGestureDetector: ScaleGestureDetector

  init {
    // Create an OpenGL ES 2.0 context
    setEGLContextClientVersion(2)

    // Set the provided renderer for drawing on the GLSurfaceView
    viewModel.mapRenderer.updateContext(context)
    setRenderer(viewModel.mapRenderer)

    scaleGestureDetector = ScaleGestureDetector(context, viewModel)
  }

  @SuppressLint("ClickableViewAccessibility")
  override fun onTouchEvent(event: MotionEvent): Boolean {
    scaleGestureDetector.onTouchEvent(event)
    return true
  }
}
