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
          size = 0.01f,
          vertexShaderCode = "TODO",
          fragmentShaderCode = "TODO")
    }
  }
}
