package com.github.lookupgroup27.lookup.util

import android.content.Context
import android.util.Log

/** Utility class for loading and compiling shaders. */
object ShaderUtils {

  /**
   * Read a shader located in the res/shaders folder.
   *
   * @param context the context to use to read the shader
   * @param shaderFileName the name of the shader file
   * @return the shader code
   */
  fun readShader(context: Context, shaderFileName: String): String {
    return try {
      context.assets.open("shaders/$shaderFileName").bufferedReader().use { it.readText() }
    } catch (e: Exception) {
      Log.e("ShaderError", "Error reading shader: $shaderFileName", e)
      ""
    }
  }
}
