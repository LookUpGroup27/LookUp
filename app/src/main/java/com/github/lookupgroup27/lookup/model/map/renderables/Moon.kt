package com.github.lookupgroup27.lookup.model.map.renderables

import android.content.Context
import androidx.annotation.VisibleForTesting
import com.github.lookupgroup27.lookup.R
import com.github.lookupgroup27.lookup.util.CelestialObjectsUtils
import java.util.Calendar
import java.util.TimeZone

/**
 * Represents the Moon as a specialized Planet with dynamic phase textures.
 *
 * This class dynamically selects the appropriate moon phase texture based on the current date. Moon
 * phases are represented by 8 distinct texture states, corresponding to the lunar cycle.
 *
 * @property context The Android context used for resource access.
 * @property position The moon's position in 3D space (default is slightly offset from the center).
 * @property numBands Number of latitude bands for sphere tessellation (default from
 *   SphereRenderer).
 * @property stepsPerBand Number of longitude steps per band (default from SphereRenderer).
 */
class Moon(
    context: Context,
    position: FloatArray = floatArrayOf(0.5f, 0.5f, -2.0f),
    numBands: Int = SphereRenderer.DEFAULT_NUM_BANDS,
    stepsPerBand: Int = SphereRenderer.DEFAULT_STEPS_PER_BAND
) :
    Planet(
        context = context,
        name = "Moon",
        position = position,
        textureId = getCurrentMoonPhaseTextureId(),
        numBands = numBands,
        stepsPerBand = stepsPerBand,
        scale = 0.05f,
        rotationSpeed = 0.0f) {
  /** Companion object containing moon phase calculation and texture mapping logic. */
  companion object {
    /**
     * Calculates the current moon phase and returns the corresponding texture resource ID.
     *
     * @return Resource ID for the current moon phase texture.
     */
    private fun getCurrentMoonPhaseTextureId(): Int {
      val calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"))
      return getMoonPhaseTextureId(calendar)
    }

    /**
     * Determines the moon phase texture based on the lunar cycle.
     *
     * @param calendar The calendar instance to calculate the moon phase from.
     * @return Resource ID for the moon phase texture.
     */
    @VisibleForTesting
    fun getMoonPhaseTextureId(calendar: Calendar): Int {
      // Approximate lunar cycle calculation
      val year = calendar.get(Calendar.YEAR)
      val month = calendar.get(Calendar.MONTH)
      val day = calendar.get(Calendar.DAY_OF_MONTH)

      // Simplified lunar phase calculation (Astronomical algorithms)
      val julianDay = CelestialObjectsUtils.getJulianDay(year, month + 1, day)
      val lunationNumber = (julianDay - 2451550.1) / 29.530588853
      val moonPhase = lunationNumber - lunationNumber.toInt()

      // Map moon phase to 8 distinct texture states
      return when {
        moonPhase < 0.0625 -> R.drawable.new_moon
        moonPhase < 0.1875 -> R.drawable.waxing_crescent
        moonPhase < 0.3125 -> R.drawable.first_quarter
        moonPhase < 0.4375 -> R.drawable.waxing_gibbous
        moonPhase < 0.5625 -> R.drawable.full_moon
        moonPhase < 0.6875 -> R.drawable.waning_gibbous
        moonPhase < 0.8125 -> R.drawable.last_quarter
        moonPhase < 0.9375 -> R.drawable.waning_crescent
        else -> R.drawable.new_moon
      }
    }
  }
}
