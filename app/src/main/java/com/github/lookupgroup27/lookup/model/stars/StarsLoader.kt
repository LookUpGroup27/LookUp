package com.github.lookupgroup27.lookup.model.loader

import android.content.Context
import com.github.lookupgroup27.lookup.model.map.renderables.Star
import com.github.lookupgroup27.lookup.model.stars.StarDataRepository

/** StarsLoader fetches data from ObjectDatabase and prepares it for OpenGL rendering. */
class StarsLoader(private val repository: StarDataRepository) {

  /** Fetches stars from the repository and converts them into renderable Star objects. */
  fun loadStars(context: Context, fileName: String): List<Star> {
    val stars = repository.getStars(context, fileName)
    return stars.map { starData ->
      Star(
          context = context,
          position =
              floatArrayOf(
                  starData.position.first, starData.position.second, starData.position.third),
          color = starData.color,
          vertexShaderCode =
              """
            attribute vec4 vPosition;
            attribute vec4 vColor;
            uniform mat4 uMVPMatrix;
            varying vec4 vInterpolatedColor;
            void main() {
                gl_Position = uMVPMatrix * vPosition;
                vInterpolatedColor = vColor;
            }
            """
                  .trimIndent(),
          fragmentShaderCode =
              """
            precision mediump float;
            varying vec4 vInterpolatedColor;
            void main() {
                gl_FragColor = vInterpolatedColor;
            }
            """
                  .trimIndent())
    }
  }
}
