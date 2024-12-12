package com.github.lookupgroup27.lookup.model.calendar

import java.io.IOException
import kotlinx.coroutines.test.runTest
import okhttp3.Interceptor
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Protocol
import okhttp3.Response
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test

/**
 * Unit tests for [CalendarRepositoryFirestore], ensuring that it correctly fetches iCal data under
 * various conditions.
 */
class CalendarRepositoryFirestoreTest {

  private lateinit var client: OkHttpClient

  companion object {
    private const val TEST_URL = "https://example.com/ical"
    private const val MOCK_DATA = "mock iCal data"
    private const val INVALID_URL = "htp://invalid-url"
    private const val EMPTY_URL = ""
  }

  @Before
  fun setup() {
    client = OkHttpClient.Builder().addInterceptor(CustomResponseInterceptor).build()
  }

  /**
   * Tests that [CalendarRepositoryFirestore.getData] returns data when the HTTP response is
   * successful.
   */
  @Test
  fun getDataReturnsDataOnSuccessfulResponse() = runTest {
    // Initialize repository with TEST_URL
    val repository = CalendarRepositoryFirestore(client, TEST_URL)

    // Set the interceptor to return MOCK_DATA with success=true
    CustomResponseInterceptor.setResponse(MOCK_DATA, success = true)

    val result = repository.getData()

    assertNotNull(result)
    assertEquals(MOCK_DATA, result)
  }

  /**
   * Tests that [CalendarRepositoryFirestore.getData] returns null when the HTTP response is
   * unsuccessful.
   */
  @Test
  fun getDataReturnsNullOnErrorResponse() = runTest {
    // Initialize repository with TEST_URL
    val repository = CalendarRepositoryFirestore(client, TEST_URL)

    // Set the interceptor to simulate an error response
    CustomResponseInterceptor.setResponse(success = false)

    val result = repository.getData()

    assertNull(result)
  }

  /**
   * Tests that [CalendarRepositoryFirestore.getData] returns null when the HTTP response body is
   * empty.
   */
  @Test
  fun getDataReturnsNullWhenBodyIsEmpty() = runTest {
    // Initialize repository with TEST_URL
    val repository = CalendarRepositoryFirestore(client, TEST_URL)

    // Set the interceptor to return an empty body with success=true
    CustomResponseInterceptor.setResponse(data = null, success = true)

    val result = repository.getData()

    assertNull(result)
  }

  /**
   * Tests that [CalendarRepositoryFirestore.getData] throws an [IOException] when a network failure
   * occurs.
   */
  @Test(expected = IOException::class)
  fun getDataThrowsIOExceptionOnNetworkFailure() = runTest {
    // Initialize repository with TEST_URL
    val repository = CalendarRepositoryFirestore(client, TEST_URL)

    // Set the interceptor to throw a network error
    CustomResponseInterceptor.throwNetworkError()

    repository.getData()
  }

  /**
   * Tests that [CalendarRepositoryFirestore.getData] returns null when initialized with an invalid
   * URL format.
   */
  @Test
  fun getDataReturnsNullOnInvalidUrlFormat() = runTest {
    // Initialize repository with INVALID_URL
    val repository = CalendarRepositoryFirestore(client, INVALID_URL)

    // Set the interceptor to simulate an error response
    CustomResponseInterceptor.setResponse(success = false)

    val result = repository.getData()

    assertNull(result)
  }

  /**
   * Tests that [CalendarRepositoryFirestore.getData] returns null when initialized with an empty
   * URL.
   */
  @Test
  fun getDataReturnsNullOnEmptyUrl() = runTest {
    // Initialize repository with EMPTY_URL
    val repository = CalendarRepositoryFirestore(client, EMPTY_URL)

    // Set the interceptor to simulate an error response
    CustomResponseInterceptor.setResponse(success = false)

    val result = repository.getData()

    assertNull(result)
  }
}

/**
 * [CustomResponseInterceptor] is a test utility that simulates HTTP responses for testing
 * `CalendarRepositoryFirestore` in a controlled environment. (Tried using MockWebServer but it was
 * not working as I wanted it to)
 */
object CustomResponseInterceptor : Interceptor {

  private var shouldThrowError = false
  private var responseData: String? = null
  private var isSuccess = true

  /**
   * Sets the response data and success status for the interceptor to simulate HTTP responses.
   *
   * @param data The string content to return in the response body.
   * @param success If `true`, the interceptor will simulate a successful (200) HTTP response. If
   *   `false`, it will simulate an unsuccessful response (404).
   */
  fun setResponse(data: String? = null, success: Boolean) {
    responseData = data
    isSuccess = success
    shouldThrowError = false
  }

  /**
   * Simulates a network error by setting a flag. When this flag is true, an [IOException] will be
   * thrown in `intercept()`, simulating a network failure.
   */
  fun throwNetworkError() {
    shouldThrowError = true
  }

  /**
   * Intercepts HTTP requests and provides a custom response based on [setResponse] or
   * [throwNetworkError] configuration.
   * - If `shouldThrowError` is true, an [IOException] is thrown to simulate a network error.
   * - Otherwise, it returns an HTTP response:
   *     - If `isSuccess` is true, it returns a 200 response with `responseData` as the body.
   *     - If `isSuccess` is false, it returns a 404 response with an empty body.
   */
  override fun intercept(chain: Interceptor.Chain): Response {
    if (shouldThrowError) {
      throw IOException("Simulated network error")
    }

    val request = chain.request()
    val responseCode = if (isSuccess) 200 else 404

    return Response.Builder()
        .request(request)
        .protocol(Protocol.HTTP_1_1)
        .code(responseCode)
        .message("Mock Response")
        .body(
            if (isSuccess && responseData != null) {
              // Create a ResponseBody with the mock data if the response is successful
              responseData!!.toResponseBody("text/plain".toMediaTypeOrNull())
            } else {
              // Empty ResponseBody for unsuccessful responses
              ByteArray(0).toResponseBody("text/plain".toMediaTypeOrNull())
            })
        .build()
  }
}
