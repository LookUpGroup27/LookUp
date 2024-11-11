package com.github.lookupgroup27.lookup.model.location

import android.content.Context

/**
 * Singleton object to manage a single instance of LocationProvider. This ensures only one instance
 * of LocationProvider is created and shared across the app.
 */
object LocationProviderSingleton {

  // Holds the single instance of LocationProvider, initially null
  private var instance: LocationProvider? = null

  /**
   * Retrieves the single instance of LocationProvider. If it hasn't been created yet, a new
   * instance is created using the application context.
   *
   * @param context Application context to initialize LocationProvider if needed.
   * @return The singleton instance of LocationProvider.
   */
  fun getInstance(context: Context): LocationProvider {
    // Initialize instance if it's null, otherwise return the existing instance
    if (instance == null) {
      instance = LocationProvider(context.applicationContext)
    }
    return instance!!
  }
}
