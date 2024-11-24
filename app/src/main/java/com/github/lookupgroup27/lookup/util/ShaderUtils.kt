package com.github.lookupgroup27.lookup.util

import android.content.Context
import android.opengl.GLES20
import android.util.Log

/** Utility class for loading and compiling shaders. */
object ShaderUtils {

  /**
   * Load and compile a shader.
   *
   * @param type the type of shader to load
   * @param shaderCode the source code of the shader
   * @return the compiled shader
   */
  fun loadShader(type: Int, shaderCode: String): Int {
    // create a vertex shader type (GLES20.GL_VERTEX_SHADER)
    // or a fragment shader type (GLES20.GL_FRAGMENT_SHADER)
    return GLES20.glCreateShader(type).also { shader ->

      // add the source code to the shader and compile it
      GLES20.glShaderSource(shader, shaderCode)
      GLES20.glCompileShader(shader)
      val compileStatus = IntArray(1)
      GLES20.glGetShaderiv(shader, GLES20.GL_COMPILE_STATUS, compileStatus, 0)
      if (compileStatus[0] == 0) {
        GLES20.glGetShaderInfoLog(shader)?.let { log ->
          android.util.Log.e("ShaderError", "Error compiling shader: $log")
        }
        GLES20.glDeleteShader(shader)
      }
    }
  }

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
