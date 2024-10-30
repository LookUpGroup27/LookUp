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
        val request = Request.Builder().url(url).build()
        val response = client.newCall(request).execute()

        // Read the response body once and store it in a variable
        val responseBody = response.body?.string()

        // Check if the response is unsuccessful or if the body is null/empty
        if (!response.isSuccessful || responseBody.isNullOrEmpty()) {
          return@withContext null
        }

        // Return the body content if everything is successful
        return@withContext responseBody
      }
}
