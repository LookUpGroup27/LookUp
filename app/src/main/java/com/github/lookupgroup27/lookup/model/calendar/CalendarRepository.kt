package com.github.lookupgroup27.lookup.model.calendar

/** Interface defining the contract for fetching calendar data. */
interface CalendarRepository {
  /**
   * Fetches calendar data as a raw string.
   *
   * @return A string containing calendar data or null if the fetch fails.
   */
  suspend fun getData(): String?
}
