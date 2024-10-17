package com.github.lookupgroup27.lookup.model.calendar

class MockIcalRepository(private val mockData: String?) : IcalRepository {
  override suspend fun fetchIcalData(url: String): String? {
    return mockData
  }
}
