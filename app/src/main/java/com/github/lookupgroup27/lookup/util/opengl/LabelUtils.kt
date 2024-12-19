package com.github.lookupgroup27.lookup.util.opengl

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint

/** Utility class for creating bitmaps for star labels. */
object LabelUtils {

  /**
   * Creates a bitmap with the given text.
   *
   * @param text The text to display on the label.
   * @return A bitmap with the given text drawn on it.
   */
  fun createLabelBitmap(text: String): Bitmap {
    val width = 256 // Width of the bitmap
    val height = 256 // Height of the bitmap
    val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)

    val paint = Paint()
    paint.color = android.graphics.Color.WHITE // Text color
    paint.textSize = 32f
    paint.isAntiAlias = true

    val x = width / 2f - paint.measureText(text) / 2
    val y = height / 2f - (paint.descent() + paint.ascent()) / 2

    val canvas = Canvas(bitmap)
    canvas.drawColor(android.graphics.Color.TRANSPARENT) // Background color
    canvas.drawText(text, x, y, paint)

    return bitmap
  }
}
