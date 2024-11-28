package com.github.lookupgroup27.lookup.model.map

import android.content.Context
import android.opengl.GLES20
import android.opengl.GLSurfaceView
import com.github.lookupgroup27.lookup.R
import com.github.lookupgroup27.lookup.model.map.renderables.Star
import com.github.lookupgroup27.lookup.model.map.skybox.SkyBox
import com.github.lookupgroup27.lookup.util.ShaderUtils.readShader
import com.github.lookupgroup27.lookup.util.opengl.TextureManager
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

/**
 * Provides the OpenGL rendering logic for the GLSurfaceView. This class is responsible for drawing
 * the shapes on the screen. It is called by the GLSurfaceView when it is time to redraw the screen.
 */
class Renderer : GLSurfaceView.Renderer {

  companion object {
    private const val VERTEX_SHADER_FILE = "vertex_shader.glsl"
    private const val FRAGMENT_SHADER_FILE = "fragment_shader.glsl"
  }

  private lateinit var skyBox: SkyBox
  private lateinit var star: Star
  private lateinit var textureManager: TextureManager

  private var skyBoxTextureHandle: Int = -1 // Handle for the skybox texture

  private lateinit var context: Context
  private lateinit var vertexShaderCode: String
  private lateinit var fragmentShaderCode: String

  /** The camera used to draw the shapes on the screen. */
  val camera = Camera()

  override fun onSurfaceCreated(unused: GL10, config: EGLConfig) {
    // Set the background frame color
    GLES20.glClearColor(0.0f, 0.0f, 0.0f, 1.0f)

    GLES20.glEnable(GLES20.GL_DEPTH_TEST)

    // Load the shaders
    vertexShaderCode = readShader(context, VERTEX_SHADER_FILE)
    fragmentShaderCode = readShader(context, FRAGMENT_SHADER_FILE)

    // Initialize TextureManager
    textureManager = TextureManager(context)

    // Load the skybox texture (replace with your texture resource ID)
    skyBoxTextureHandle = textureManager.loadTexture(R.drawable.skybox_texture)

    // Initialize the SkyBox
    skyBox = SkyBox()
    initializeStar()
  }

  override fun onDrawFrame(unused: GL10) {
    // Clear the screen
    GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT or GLES20.GL_DEPTH_BUFFER_BIT)

    // Render skybox first
    GLES20.glDepthMask(false)
    textureManager.bindTexture(skyBoxTextureHandle)
    skyBox.draw(camera)

    // Re-enable depth writing for other objects
    GLES20.glDepthMask(true)
    GLES20.glEnable(GLES20.GL_DEPTH_TEST)

    drawStar()
  }

  override fun onSurfaceChanged(unused: GL10, width: Int, height: Int) {
    // Specify the size of the rendering window
    GLES20.glViewport(0, 0, width, height)

    val ratio: Float = width.toFloat() / height.toFloat()

    camera.updateProjectionMatrix(ratio)
  }

  fun updateContext(context: Context) {
    this.context = context
  }

  private fun initializeStar() {
    // Try a more central position and potentially larger size
    val position = floatArrayOf(0f, 0f, -2f) // Move closer to the camera
    val color = floatArrayOf(1.0f, 0.0f, 0.0f, 1.0f)
    star =
        Star(
            context,
            position,
            color,
            size = 1f,
            vertexShaderCode = vertexShaderCode,
            fragmentShaderCode = fragmentShaderCode)
  }

  private fun drawStar() {
    // Draw the star object
    star.draw(camera)
  }
}
