package com.github.lookupgroup27.lookup.model.calendar

interface IcalRepository {
  suspend fun fetchIcalData(url: String): String?
}
