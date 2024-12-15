package com.github.lookupgroup27.lookup.model.map.renderables

import com.github.lookupgroup27.lookup.model.map.Camera

/** Represents an object in the OpenGL world. */
abstract class Object {

  /**
   * Draw the object on the screen.
   *
   * @param camera the camera to use to draw the object
   */
  abstract fun draw(camera: Camera)
}
