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
          x = starData.position.first,
          y = starData.position.second,
          z = starData.position.third,
          color = starData.color,
          vertexShaderCode = "TODO",
          fragmentShaderCode = "TODO")
    }
  }
}
