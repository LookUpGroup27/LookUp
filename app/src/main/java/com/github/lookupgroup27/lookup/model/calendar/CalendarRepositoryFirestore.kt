package com.github.lookupgroup27.lookup.model.calendar

import java.io.IOException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request

class HttpCalendarRepository(
  private val client: OkHttpClient,
  private val url: String // The repository now controls the data source.
) : CalendarRepository {

  @Throws(IOException::class)
  override suspend fun getData(): String? = withContext(Dispatchers.IO) {
    try {
      val request = Request.Builder().url(url).build()
      val response = client.newCall(request).execute()

      val responseBody = response.body?.string()

      if (!response.isSuccessful || responseBody.isNullOrEmpty()) {
        return@withContext null
      }

      return@withContext responseBody
    } catch (e: IllegalArgumentException) {
      return@withContext null
    }
  }
}
