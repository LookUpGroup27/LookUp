package com.github.lookupgroup27.lookup.model.map.skybox

import android.opengl.Matrix
import com.github.lookupgroup27.lookup.model.map.Camera
import com.github.lookupgroup27.lookup.model.map.renderables.SphereRenderer

/**
 * Represents a skybox in the OpenGL rendering environment.
 *
 * The skybox consists of a set of vertices, colors, and indices to simulate the sky using a
 * spherical or cubic geometry.
 *
 * @param numBands The number of vertex bands in the skybox.
 * @param stepsPerBand The number of steps in each band.
 */
class SkyBox(numBands: Int = DEFAULT_NUM_BANDS, stepsPerBand: Int = DEFAULT_STEPS_PER_BAND) :
    SphereRenderer(numBands, stepsPerBand) {

  /** Initializes buffers and shaders for rendering the skybox. */
  init {
    initializeBuffers()
    initializeShaders()
  }

  /**
   * Draws the skybox using the provided camera's view and projection matrices.
   *
   * The skybox view matrix ignores translations to ensure the skybox appears static relative to the
   * camera's position, simulating a distant sky.
   *
   * @param camera The camera providing the view and projection matrices.
   */
  fun draw(camera: Camera) {
    // Create a modified view matrix for the skybox that ignores translations
    val skyboxViewMatrix = FloatArray(16)
    System.arraycopy(camera.viewMatrix, 0, skyboxViewMatrix, 0, 16)
    skyboxViewMatrix[12] = 0f // X translation
    skyboxViewMatrix[13] = 0f // Y translation
    skyboxViewMatrix[14] = 0f // Z translation

    // Compute the skybox MVP matrix
    val skyboxMvpMatrix = FloatArray(16)
    Matrix.multiplyMM(skyboxMvpMatrix, 0, camera.projMatrix, 0, skyboxViewMatrix, 0)

    bindShaderAttributes(skyboxMvpMatrix)
    drawSphere()
    unbindShaderAttributes()
  }
}
