package com.github.lookupgroup27.lookup.model.map

import android.content.Context
import android.opengl.GLES20
import android.opengl.GLSurfaceView
import com.github.lookupgroup27.lookup.R
import com.github.lookupgroup27.lookup.model.map.skybox.SkyBox
import com.github.lookupgroup27.lookup.util.opengl.TextureManager
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

/**
 * Provides the OpenGL rendering logic for the GLSurfaceView. This class is responsible for drawing
 * the shapes on the screen. It is called by the GLSurfaceView when it is time to redraw the screen.
 */
class Renderer(private val context: Context) : GLSurfaceView.Renderer {

  private lateinit var textureManager: TextureManager
  private lateinit var skyBox: SkyBox

  private var skyBoxTextureHandle: Int = -1 // Handle for the skybox texture

  /** The camera used to draw the shapes on the screen. */
  val camera = Camera()

  override fun onSurfaceCreated(unused: GL10, config: EGLConfig) {
    // Set the background frame color
    GLES20.glClearColor(0.0f, 0.0f, 0.0f, 1.0f)

    GLES20.glEnable(GLES20.GL_DEPTH_TEST)

    // Initialize TextureManager
    textureManager = TextureManager(context)

    // Load the skybox texture (replace with your texture resource ID)
    skyBoxTextureHandle = textureManager.loadTexture(R.drawable.skybox_texture)

    // Initialize the SkyBox
    skyBox = SkyBox()
  }

  override fun onDrawFrame(unused: GL10) {

    // Clear the screen
    GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT or GLES20.GL_DEPTH_BUFFER_BIT)

    GLES20.glDepthMask(false) // Disable depth writing

    // Bind the texture and render the SkyBox
    textureManager.bindTexture(skyBoxTextureHandle)

    // Use this MVP matrix to render the skybox
    skyBox.draw(camera)

    GLES20.glDepthMask(true) // Re-enable depth writing for other objects
  }

  override fun onSurfaceChanged(unused: GL10, width: Int, height: Int) {
    // Specify the size of the rendering window
    GLES20.glViewport(0, 0, width, height)

    val ratio: Float = width.toFloat() / height.toFloat()

    camera.updateProjectionMatrix(ratio)
  }
}
