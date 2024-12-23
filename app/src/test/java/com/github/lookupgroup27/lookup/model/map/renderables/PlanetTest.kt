package com.github.lookupgroup27.lookup.model.map.renderables

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.github.lookupgroup27.lookup.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.resetMain
import org.junit.After
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(RobolectricTestRunner::class)
class PlanetTest {

  private lateinit var context: Context

  @Before
  fun setUp() {
    // Initialize the context
    context = ApplicationProvider.getApplicationContext()
  }

  @After
  fun tearDown() {
    Dispatchers.resetMain()
  }

  @Test
  fun `checkHit should return true when ray intersects the planet's bounding sphere`() {
    // Arrange
    val planet =
        Planet(
            context = context, // Replace with a mock or test context
            name = "TestPlanet",
            position = floatArrayOf(0f, 0f, -5f),
            textureId = R.drawable.planet_texture,
            scale = 1f)

    val rayOrigin = floatArrayOf(0f, 0f, 0f)
    val rayDirection = floatArrayOf(0f, 0f, -1f) // Directly toward the planet

    // Act
    val intersects = planet.checkHit(rayOrigin, rayDirection)

    // Assert
    assertTrue("Ray should intersect the planet's bounding sphere", intersects)
  }

  @Test
  fun `checkHit should return false when ray misses the planet's bounding sphere`() {
    // Arrange
    val planet =
        Planet(
            context = context,
            name = "TestPlanet",
            position = floatArrayOf(5f, 5f, -5f),
            textureId = R.drawable.planet_texture,
            scale = 1f)

    val rayOrigin = floatArrayOf(0f, 0f, 0f)
    val rayDirection = floatArrayOf(0f, 0f, -1f) // Directed away from the planet's position

    // Act
    val intersects = planet.checkHit(rayOrigin, rayDirection)

    // Assert
    assertFalse("Ray should not intersect the planet's bounding sphere", intersects)
  }

  @Test
  fun `checkHit should return true when ray grazes the planet's bounding sphere`() {
    // Arrange
    val planet =
        Planet(
            context = context,
            name = "TestPlanet",
            position = floatArrayOf(1f, 0f, -5f),
            textureId = R.drawable.planet_texture,
            scale = 1f)

    val rayOrigin = floatArrayOf(0f, 0f, 0f)
    val rayDirection = floatArrayOf(1f, 0f, -5f)

    // Normalize ray direction
    val magnitude =
        Math.sqrt(
                (rayDirection[0] * rayDirection[0] +
                        rayDirection[1] * rayDirection[1] +
                        rayDirection[2] * rayDirection[2])
                    .toDouble())
            .toFloat()
    rayDirection[0] /= magnitude
    rayDirection[1] /= magnitude
    rayDirection[2] /= magnitude

    // Act
    val intersects = planet.checkHit(rayOrigin, rayDirection)

    // Assert
    assertTrue("Ray should graze the planet's bounding sphere", intersects)
  }

  @Test
  fun `checkHit should return true when ray origin is inside the planet's bounding sphere`() {
    // Arrange
    val planet =
        Planet(
            context = context,
            name = "TestPlanet",
            position = floatArrayOf(0f, 0f, -5f),
            textureId = R.drawable.planet_texture,
            scale = 1f)

    val rayOrigin = floatArrayOf(0f, 0f, -5f) // Inside the sphere
    val rayDirection = floatArrayOf(0f, 0f, -1f)

    // Act
    val intersects = planet.checkHit(rayOrigin, rayDirection)

    // Assert
    assertTrue("Ray should intersect even when the origin is inside the sphere", intersects)
  }

  @Test
  fun `updateRotation should update rotationAngle based on deltaTime and rotationSpeed`() {
    // Arrange
    val planet =
        Planet(
            context = context,
            name = "TestPlanet",
            position = floatArrayOf(0f, 0f, -5f),
            textureId = R.drawable.planet_texture,
            scale = 1f)

    val initialRotationAngle = 0f
    val rotationSpeed = 30f // degrees per second
    val deltaTime = 1f // 1 second

    // Set rotationSpeed manually for testing (assuming rotationSpeed is accessible or made
    // testable)
    val rotationSpeedField = planet.javaClass.getDeclaredField("rotationSpeed")
    rotationSpeedField.isAccessible = true
    rotationSpeedField.setFloat(planet, rotationSpeed)

    // Act
    planet.updateRotation(deltaTime)

    // Assert
    val expectedRotationAngle = (initialRotationAngle + rotationSpeed * deltaTime) % 360f
    val rotationAngleField = planet.javaClass.getDeclaredField("rotationAngle")
    rotationAngleField.isAccessible = true
    val actualRotationAngle = rotationAngleField.getFloat(planet)

    assertTrue(
        "Rotation angle should be updated correctly", actualRotationAngle == expectedRotationAngle)
  }

  @Test
  fun `updateRotation should wrap rotationAngle around 360 degrees`() {
    // Arrange
    val planet =
        Planet(
            context = context,
            name = "TestPlanet",
            position = floatArrayOf(0f, 0f, -5f),
            textureId = R.drawable.planet_texture,
            scale = 1f)

    val initialRotationAngle = 350f
    val rotationSpeed = 30f // degrees per second
    val deltaTime = 1f // 1 second

    // Set rotationSpeed manually for testing
    val rotationSpeedField = planet.javaClass.getDeclaredField("rotationSpeed")
    rotationSpeedField.isAccessible = true
    rotationSpeedField.setFloat(planet, rotationSpeed)

    // Manually set the initial rotationAngle
    val rotationAngleField = planet.javaClass.getDeclaredField("rotationAngle")
    rotationAngleField.isAccessible = true
    rotationAngleField.setFloat(planet, initialRotationAngle)

    // Act
    planet.updateRotation(deltaTime)

    // Assert
    val expectedRotationAngle = (initialRotationAngle + rotationSpeed * deltaTime) % 360f
    val actualRotationAngle = rotationAngleField.getFloat(planet)

    assertTrue(
        "Rotation angle should wrap around correctly at 360 degrees",
        actualRotationAngle == expectedRotationAngle)
  }
}
