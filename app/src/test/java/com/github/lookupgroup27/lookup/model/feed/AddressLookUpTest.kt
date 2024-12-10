package com.github.lookupgroup27.lookup.model.feed

import com.github.lookupgroup27.lookup.ui.feed.components.getAddressFromLatLngUsingNominatim
import java.io.IOException
import java.net.SocketTimeoutException
import kotlinx.coroutines.test.runTest
import okhttp3.Call
import okhttp3.OkHttpClient
import okhttp3.Protocol
import okhttp3.Request
import okhttp3.Response
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever

class AddressLookUpTest {
  private lateinit var mockOkHttpClient: OkHttpClient
  private lateinit var mockCall: Call

  @Before
  fun setup() {
    mockCall = mock()
    mockOkHttpClient = mock()
    whenever(mockOkHttpClient.newCall(any())).thenReturn(mockCall)
  }

  private fun createMockResponse(
      code: Int = 200,
      body: String? = null,
      message: String = "OK"
  ): Response {
    return Response.Builder()
        .code(code)
        .message(message)
        .protocol(Protocol.HTTP_1_1)
        .request(Request.Builder().url("http://test.com").build())
        .body(body?.toResponseBody())
        .build()
  }

  @Test
  fun `test valid address fetching`() = runTest {
    // Given
    val expectedAddress = "123 Test Street, Test City, 12345"
    val jsonResponse =
        """
            {
                "display_name": "$expectedAddress"
            }
        """
            .trimIndent()

    whenever(mockCall.execute()).thenReturn(createMockResponse(200, jsonResponse))

    // When
    val result = getAddressFromLatLngUsingNominatim(37.7749, -122.4194, mockOkHttpClient)

    // Then
    assertEquals(expectedAddress, result)
  }

  @Test
  fun `test empty API response`() = runTest {
    // Given
    whenever(mockCall.execute()).thenReturn(createMockResponse(200, "{}"))

    // When
    val result = getAddressFromLatLngUsingNominatim(37.7749, -122.4194, mockOkHttpClient)

    // Then
    assertEquals("Address not found", result)
  }

  @Test
  fun `test null response body`() = runTest {
    // Given
    whenever(mockCall.execute()).thenReturn(createMockResponse(200, null))

    // When
    val result = getAddressFromLatLngUsingNominatim(37.7749, -122.4194, mockOkHttpClient)

    // Then
    assertEquals("Error: Empty response body", result)
  }

  @Test
  fun `test HTTP 404 error`() = runTest {
    // Given
    whenever(mockCall.execute()).thenReturn(createMockResponse(404, null, "Not Found"))

    // When
    val result = getAddressFromLatLngUsingNominatim(37.7749, -122.4194, mockOkHttpClient)

    // Then
    assertEquals("Error fetching address: HTTP 404 - Not Found", result)
  }

  @Test
  fun `test HTTP 500 error`() = runTest {
    // Given
    whenever(mockCall.execute()).thenReturn(createMockResponse(500, null, "Internal Server Error"))

    // When
    val result = getAddressFromLatLngUsingNominatim(37.7749, -122.4194, mockOkHttpClient)

    // Then
    assertEquals("Error fetching address: HTTP 500 - Internal Server Error", result)
  }

  @Test
  fun `test invalid coordinates`() = runTest {
    // Test coordinates outside valid range
    val invalidLatitudes = listOf(-91.0, 91.0)
    val invalidLongitudes = listOf(-181.0, 181.0)

    for (lat in invalidLatitudes) {
      val result = getAddressFromLatLngUsingNominatim(lat, 0.0, mockOkHttpClient)
      assertEquals("Error fetching address: Invalid latitude: $lat", result)
    }

    for (lon in invalidLongitudes) {
      val result = getAddressFromLatLngUsingNominatim(0.0, lon, mockOkHttpClient)
      assertEquals("Error fetching address: Invalid longitude: $lon", result)
    }
  }

  @Test
  fun `test network timeout`() = runTest {
    // Given
    whenever(mockCall.execute()).thenThrow(SocketTimeoutException("Connection timed out"))

    // When
    val result = getAddressFromLatLngUsingNominatim(37.7749, -122.4194, mockOkHttpClient)

    // Then
    assertEquals("Error fetching address: Connection timed out", result)
  }

  @Test
  fun `test network unreachable`() = runTest {
    // Given
    whenever(mockCall.execute()).thenThrow(IOException("Network is unreachable"))

    // When
    val result = getAddressFromLatLngUsingNominatim(37.7749, -122.4194, mockOkHttpClient)

    // Then
    assertEquals("Error fetching address: Network is unreachable", result)
  }
}
