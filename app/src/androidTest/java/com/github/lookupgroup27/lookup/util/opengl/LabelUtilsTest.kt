package com.github.lookupgroup27.lookup.util.opengl

import android.graphics.Bitmap
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith

/** Instrumented tests for LabelUtils and the Label class. */
@RunWith(AndroidJUnit4::class)
class LabelUtilsTest {

  /** Tests that the bitmap is created successfully. */
  @Test
  fun testBitmapIsCreatedSuccessfully() {
    val text = "Star A"
    val bitmap: Bitmap = LabelUtils.createLabelBitmap(text)

    assertNotNull("Bitmap should not be null", bitmap)
    assertEquals("Bitmap width should be 256", 256, bitmap.width)
    assertEquals("Bitmap height should be 256", 256, bitmap.height)
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
