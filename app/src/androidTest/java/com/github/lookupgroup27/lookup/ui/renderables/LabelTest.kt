package com.github.lookupgroup27.lookup.ui.renderables

import android.graphics.Bitmap
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.lookupgroup27.lookup.model.map.renderables.label.Label
import com.github.lookupgroup27.lookup.model.map.renderables.label.LabelUtils
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith

/** Instrumented tests for LabelUtils and the Label class. */
@RunWith(AndroidJUnit4::class)
class LabelTest {

  /** Tests that a Label object can be created successfully. */
  @Test
  fun testLabelCreation() {
    val text = "Star A"
    val position = floatArrayOf(1.0f, 2.0f, 3.0f)
    val label = Label(text, position)

    assertEquals("Label text should be 'Star A'", "Star A", label.text)
    assertArrayEquals(
        "Label position should match", floatArrayOf(1.0f, 2.0f, 3.0f), label.position, 0.0f)
    assertNull("Label textureId should be null by default", label.textureId)
  }

  /** Tests that the bitmap is created successfully. */
  @Test
  fun testBitmapIsCreatedSuccessfully() {
    val text = "Star A"
    val bitmap: Bitmap = LabelUtils.createLabelBitmap(text)

    assertNotNull("Bitmap should not be null", bitmap)
    assertEquals("Bitmap width should be 256", 256, bitmap.width)
    assertEquals("Bitmap height should be 128", 128, bitmap.height)
  }

  /** Tests that the bitmap contains non-transparent pixels. */
  @Test
  fun testBitmapContent() {
    val text = "Star A"
    val bitmap: Bitmap = LabelUtils.createLabelBitmap(text)

    var nonTransparentPixelFound = false
    for (x in 0 until bitmap.width) {
      for (y in 0 until bitmap.height) {
        if (bitmap.getPixel(x, y) != 0) { // Non-transparent pixel
          nonTransparentPixelFound = true
          break
        }
      }
      if (nonTransparentPixelFound) break
    }

    assertTrue("Bitmap should contain non-transparent pixels", nonTransparentPixelFound)
  }
}
