package com.github.lookupgroup27.lookup.util.opengl

import android.content.Context
import android.graphics.BitmapFactory
import android.opengl.GLES20
import android.opengl.GLUtils
import android.util.Log

class TextureManager(private val context: Context) {
  private val textureMap = mutableMapOf<Int, Int>() // Maps resource IDs to texture handles

  /**
   * Loads a texture from the given resource ID.
   *
   * @param resourceId The resource ID of the texture image.
   * @return The OpenGL texture handle.
   */
  fun loadTexture(resourceId: Int): Int {
    // Check if the texture is already loaded
    textureMap[resourceId]?.let {
      return it
    }

    // Load the bitmap from resources
    val bitmap =
        BitmapFactory.decodeResource(context.resources, resourceId)
            ?: throw RuntimeException("Failed to decode texture resource: $resourceId")

    // Generate a new OpenGL texture
    val textureHandle = IntArray(1)
    GLES20.glGenTextures(1, textureHandle, 0)

    if (textureHandle[0] == 0) {
      throw RuntimeException("Failed to generate OpenGL texture")
    }

    // Bind the texture and set parameters
    GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureHandle[0])
    GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR)
    GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR)
    GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE)
    GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE)

    // Load the bitmap into the texture
    GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bitmap, 0)

    // Clean up
    bitmap.recycle()
    GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0)

    // Store the texture handle in the map
    textureMap[resourceId] = textureHandle[0]

    Log.d("TextureManager", "Texture Handle: $textureHandle[0]")

    return textureHandle[0]
  }

  /**
   * Binds the texture for rendering.
   *
   * @param textureHandle The OpenGL texture handle to bind.
   */
  fun bindTexture(textureHandle: Int) {
    GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureHandle)
  }

  /** Deletes all loaded textures. */
  fun releaseAllTextures() {
    val handles = textureMap.values.toIntArray()
    GLES20.glDeleteTextures(handles.size, handles, 0)
    textureMap.clear()
  }
}
