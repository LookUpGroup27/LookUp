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
    val height = 128 // Height of the bitmap
    val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)

    val canvas = Canvas(bitmap)
    canvas.drawColor(android.graphics.Color.BLACK) // Background color

    val paint = Paint()
    paint.color = android.graphics.Color.WHITE // Text color
    paint.textSize = 32f
    paint.isAntiAlias = true

    val x = 20f
    val y = height / 2f
    canvas.drawText(text, x, y, paint)

    return bitmap
  }
}
