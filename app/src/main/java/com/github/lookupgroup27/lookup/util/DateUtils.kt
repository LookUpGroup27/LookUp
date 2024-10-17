package com.github.lookupgroup27.lookup.util

import java.util.*

object DateUtils {

  /**
   * Checks if two dates are on the same day.
   *
   * @param date1 the first date to compare.
   * @param date2 the second date to compare.
   * @return true if both dates are on the same day, false otherwise.
   */
  fun isSameDay(date1: Date, date2: Date): Boolean {
    val cal1 = Calendar.getInstance().apply { time = date1 }
    val cal2 = Calendar.getInstance().apply { time = date2 }
    return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
        cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR)
  }

  /**
   * Updates a given date by adding or subtracting months.
   *
   * @param date the initial date to modify.
   * @param months the number of months to add (can be negative to subtract months).
   * @return the updated date.
   */
  fun updateMonth(date: Date, months: Int): Date {
    return Calendar.getInstance()
        .apply {
          time = date
          add(Calendar.MONTH, months)
        }
        .time
  }
}
