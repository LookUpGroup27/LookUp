package com.github.lookupgroup27.lookup.model.calendar

import java.io.IOException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request

class HttpIcalRepository(private val client: OkHttpClient) : IcalRepository {
  @Throws(IOException::class) // Declare that this method throws an IOException
  override suspend fun fetchIcalData(url: String): String? =
      withContext(Dispatchers.IO) {
        val request = Request.Builder().url(url).build()
        val response = client.newCall(request).execute()
        response.body?.string()
      }
}
