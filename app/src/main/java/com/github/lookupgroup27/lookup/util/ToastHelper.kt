package com.github.lookupgroup27.lookup.util

import android.content.Context
import android.widget.Toast

/**
 * Helper class for displaying Toast messages throughout the application. Encapsulates Toast
 * creation logic for better testability and reusability.
 *
 * @property context The Android Context used to display Toasts
 */
open class ToastHelper(private val context: Context) {
  /**
   * Displays a short Toast message indicating no internet connection. Used when attempting network
   * operations while offline.
   */
  open fun showNoInternetToast() {
    Toast.makeText(context, "You're not connected to the internet", Toast.LENGTH_SHORT).show()
  }
}
