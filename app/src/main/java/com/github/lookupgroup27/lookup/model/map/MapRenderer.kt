package com.github.lookupgroup27.lookup.model.map

import android.content.Context
import android.opengl.GLES20
import android.opengl.GLSurfaceView
import com.github.lookupgroup27.lookup.R
import com.github.lookupgroup27.lookup.model.map.renderables.Object
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
class MapRenderer : GLSurfaceView.Renderer {

  companion object {
    private const val VERTEX_SHADER_FILE = "vertex_shader.glsl"
    private const val FRAGMENT_SHADER_FILE = "fragment_shader.glsl"
  }

  private lateinit var shapes: List<Object>
  private lateinit var skyBox: SkyBox
  private lateinit var textureManager: TextureManager

  private var skyBoxTextureHandle: Int = -1 // Handle for the skybox texture

  private lateinit var context: Context

  /** The camera used to draw the shapes on the screen. */
  val camera = Camera()

  override fun onSurfaceCreated(unused: GL10, config: EGLConfig) {
    // Set the background frame color
    GLES20.glClearColor(0.0f, 0.0f, 0.0f, 1.0f)

    GLES20.glEnable(GLES20.GL_DEPTH_TEST)

    // Load the shaders
    val vertexShaderCode = readShader(context, VERTEX_SHADER_FILE)
    val fragmentShaderCode = readShader(context, FRAGMENT_SHADER_FILE)

    // Create the shapes (Make sure you always create the shapes after the OpenGL context is
    // created)
    shapes =
        listOf(
            Star(
                0.0f,
                0f,
                -1f,
                floatArrayOf(1.0f, 1.0f, 1.0f),
                vertexShaderCode,
                fragmentShaderCode),
            Star(
                0.24f,
                0f,
                -0.97f,
                floatArrayOf(1.0f, 1.0f, 1.0f),
                vertexShaderCode,
                fragmentShaderCode),
            Star(
                1f,
                0f,
                0f,
                color = floatArrayOf(1.0f, 0.0f, 0.0f),
                vertexShaderCode,
                fragmentShaderCode),
            Star(
                0f,
                1f,
                0f,
                color = floatArrayOf(0.0f, 1.0f, 0.0f),
                vertexShaderCode,
                fragmentShaderCode),
            Star(
                0f,
                0f,
                1f,
                color = floatArrayOf(0.0f, 0.0f, 1.0f),
                vertexShaderCode,
                fragmentShaderCode))

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


    // Bind the texture and render the SkyBox
    textureManager.bindTexture(skyBoxTextureHandle)
    skyBox.draw(camera)

    // Render the objects
    shapes.forEach { it.draw(camera) }
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
}
