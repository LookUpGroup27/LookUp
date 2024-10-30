package com.github.lookupgroup27.lookup.model.calendar

import java.io.IOException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request

class HttpIcalRepository(private val client: OkHttpClient) : IcalRepository {

  @Throws(IOException::class)
  override suspend fun fetchIcalData(url: String): String? =
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
