package com.github.lookupgroup27.lookup.util

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities

/** Utility object for network-related operations. */
object NetworkUtils {
  /**
   * Checks whether the device has an active internet connection.
   *
   * @param context The application context.
   * @return `true` if the device is connected to a network with internet capability, `false`
   *   otherwise.
   *
   * This method uses the ConnectivityManager to get the current active network and its
   * capabilities. It checks whether the network has the NET_CAPABILITY_INTERNET capability,
   * ensuring that the device is actually connected to the internet and not just to a local network.
   */
  fun isNetworkAvailable(context: Context): Boolean {
    val connectivityManager =
        context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    val activeNetwork = connectivityManager.activeNetwork ?: return false
    val capabilities = connectivityManager.getNetworkCapabilities(activeNetwork) ?: return false
    return capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
  }
}
