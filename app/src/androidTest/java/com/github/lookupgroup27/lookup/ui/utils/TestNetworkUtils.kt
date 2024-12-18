package com.github.lookupgroup27.lookup.ui.utils

import com.github.lookupgroup27.lookup.util.NetworkUtils
import io.mockk.every
import io.mockk.mockkObject

/**
 * Test utility class to simulate online/offline modes.
 *
 * This class provides a function to mock network connectivity states, allowing tests to simulate
 * conditions where the device is either online or offline.
 */
object TestNetworkUtils {

  /**
   * Simulates network connectivity state for tests.
   *
   * @param onlineState A boolean indicating the desired network connectivity state:
   *     - `true` to simulate online mode
   *     - `false` to simulate offline mode
   */
  fun simulateOnlineMode(onlineState: Boolean) {
    mockkObject(NetworkUtils)
    every { NetworkUtils.isNetworkAvailable(any()) } returns onlineState
  }
}
