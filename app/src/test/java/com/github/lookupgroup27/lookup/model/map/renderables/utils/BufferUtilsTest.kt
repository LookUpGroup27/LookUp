package com.github.lookupgroup27.lookup.model.map.renderables.utils

import com.github.lookupgroup27.lookup.util.opengl.BufferUtils.toBuffer
import java.nio.ShortBuffer
import org.junit.Assert.assertArrayEquals
import org.junit.Test

class BufferUtilsTest {

  @Test
  fun testShortArrayToBuffer() {
    // Input short array
    val inputArray = shortArrayOf(1, 2, 3, 4, 5)

    // Convert the array to a ShortBuffer
    val buffer: ShortBuffer = inputArray.toBuffer()

    // Verify the buffer's capacity
    assert(buffer.capacity() == inputArray.size)

    // Create an output array and read the buffer's contents into it
    val outputArray = ShortArray(inputArray.size)
    buffer.get(outputArray)

    // Verify the output array matches the input array
    assertArrayEquals(inputArray, outputArray)
  }
}
