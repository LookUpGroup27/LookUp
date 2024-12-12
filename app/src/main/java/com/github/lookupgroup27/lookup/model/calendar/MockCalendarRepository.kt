package com.github.lookupgroup27.lookup.model.calendar

/**
 * Mock implementation of CalendarRepository for testing purposes.
 *
 * @property mockData The mock data to return when fetching calendar data.
 */
class MockCalendarRepository(private val mockData: String?) : CalendarRepository {
  override suspend fun getData(): String? {
    return mockData
  }
}
