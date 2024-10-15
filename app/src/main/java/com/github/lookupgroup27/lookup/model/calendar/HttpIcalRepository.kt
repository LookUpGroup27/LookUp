package com.github.lookupgroup27.lookup.model.calendar

import android.util.Log
import java.io.IOException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request

class HttpIcalRepository(private val client: OkHttpClient) : IcalRepository {
  override suspend fun fetchIcalData(url: String): String? =
      withContext(Dispatchers.IO) {
        try {
          val request = Request.Builder().url(url).build()
          val response = client.newCall(request).execute()
          response.body?.string()
        } catch (e: IOException) {
          Log.e("HttpIcalRepository", "Error fetching iCal data: ${e.localizedMessage}", e)
          null
        }
      }
}
