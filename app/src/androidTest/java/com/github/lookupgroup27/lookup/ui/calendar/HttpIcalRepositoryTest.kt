package com.github.lookupgroup27.lookup.ui.calendar

import com.github.lookupgroup27.lookup.model.calendar.HttpIcalRepository
import java.io.IOException
import kotlinx.coroutines.test.runTest
import okhttp3.Interceptor
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Protocol
import okhttp3.Response
import okhttp3.ResponseBody
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test

class HttpIcalRepositoryTest {

  private lateinit var client: OkHttpClient
  private lateinit var repository: HttpIcalRepository

  companion object {
    private const val TEST_URL = "https://example.com/ical"
    private const val MOCK_DATA = "mock iCal data"
    private const val INVALID_URL = "htp://invalid-url"
    private const val EMPTY_URL = ""
  }

  @Before
  fun setup() {
    client = OkHttpClient.Builder().addInterceptor(CustomResponseInterceptor).build()
    repository = HttpIcalRepository(client)
  }

  @Test
  fun fetchIcalDataReturnsDataOnSuccessfulResponse() = runTest {
    CustomResponseInterceptor.setResponse(MOCK_DATA, success = true)

    val result = repository.fetchIcalData(TEST_URL)

    assertNotNull(result)
    assertEquals(MOCK_DATA, result)
  }

  @Test
  fun fetchIcalDataReturnsNullOnErrorResponse() = runTest {
    CustomResponseInterceptor.setResponse(success = false)

    val result = repository.fetchIcalData(TEST_URL)

    assertNull(result)
  }

  @Test
  fun fetchIcalDataReturnsNullWhenBodyIsEmpty() = runTest {
    CustomResponseInterceptor.setResponse(data = null, success = true)

    val result = repository.fetchIcalData(TEST_URL)

    assertNull(result)
  }

  @Test(expected = IOException::class)
  fun fetchIcalDataThrowsIOExceptionOnNetworkFailure() = runTest {
    CustomResponseInterceptor.throwNetworkError()

    repository.fetchIcalData(TEST_URL)
  }

  @Test
  fun fetchIcalDataReturnsNullOnInvalidUrlFormat() = runTest {
    // Simulate an invalid URL format, which should result in an error response
    CustomResponseInterceptor.setResponse(success = false)

    val result = repository.fetchIcalData(INVALID_URL)

    assertNull(result)
  }

  @Test
  fun fetchIcalDataReturnsNullOnEmptyUrl() = runTest {
    // Simulate an empty URL, which should result in an error response
    CustomResponseInterceptor.setResponse(success = false)

    val result = repository.fetchIcalData(EMPTY_URL)

    assertNull(result)
  }
}

/**
 * CustomResponseInterceptor is a test utility that simulates HTTP responses for testing
 * `HttpIcalRepository` in a controlled environment. (Tried using MockWebServer but it was not
 * working as I wanted it to)
 */
object CustomResponseInterceptor : Interceptor {

  private var shouldThrowError = false
  private var responseData: String? = null
  private var isSuccess = true

  /**
   * Sets the response data and success status for the interceptor to simulate HTTP responses.
   * - `data`: The string content to return in the response body.
   * - `success`: If `true`, the interceptor will simulate a successful (200) HTTP response. If
   *   `false`, it will simulate an unsuccessful response (404).
   */
  fun setResponse(data: String? = null, success: Boolean) {
    responseData = data
    isSuccess = success
    shouldThrowError = false
  }

  /**
   * Simulates a network error by setting a flag. When this flag is true, an IOException will be
   * thrown in `intercept()`, simulating a network failure.
   */
  fun throwNetworkError() {
    shouldThrowError = true
  }

  /**
   * Intercepts HTTP requests and provides a custom response based on `setResponse` or
   * `throwNetworkError` configuration.
   * - If `shouldThrowError` is true, an `IOException` is thrown to simulate a network error.
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
              ResponseBody.create("text/plain".toMediaTypeOrNull(), responseData!!)
            } else {
              // Empty ResponseBody for unsuccessful responses
              ResponseBody.create("text/plain".toMediaTypeOrNull(), ByteArray(0))
            })
        .build()
  }
}
