package com.github.lookupgroup27.lookup

import android.location.Location
import androidx.test.core.app.ApplicationProvider
import com.github.lookupgroup27.lookup.model.location.LocationProvider

/** TestLocationProvider allows for manual setting of location values. */
class TestLocationProvider : LocationProvider(ApplicationProvider.getApplicationContext()) {
  fun setLocation(latitude: Double?, longitude: Double?) {
    if (latitude != null && longitude != null) {
      currentLocation.value =
          Location("test").apply {
            this.latitude = latitude
            this.longitude = longitude
          }
    } else {
      currentLocation.value = null
    }
  }
}
