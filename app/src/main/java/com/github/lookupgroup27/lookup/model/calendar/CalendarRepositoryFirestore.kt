package com.github.lookupgroup27.lookup.model.calendar

import java.io.IOException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request

/**
 * Implementation of CalendarRepository that fetches calendar data from a URL.
 *
 * @param client OkHttpClient instance for HTTP requests.
 * @param url The URL from which calendar data is fetched.
 */
class CalendarRepositoryFirestore(private val client: OkHttpClient, private val url: String) :
    CalendarRepository {

  @Throws(IOException::class)
  override suspend fun getData(): String? =
      withContext(Dispatchers.IO) {
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
