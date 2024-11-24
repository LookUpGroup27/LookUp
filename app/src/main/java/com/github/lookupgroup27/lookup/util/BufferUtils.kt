import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer
import java.nio.ShortBuffer

/** Utility functions for working with buffers. */
object BufferUtils {

  /**
   * Converts a [FloatArray] to a [FloatBuffer].
   *
   * @return A [FloatBuffer] containing the same elements as this array.
   */
  fun FloatArray.toBuffer(): FloatBuffer {
    return ByteBuffer.allocateDirect(this.size * Float.SIZE_BYTES).run {
      order(ByteOrder.nativeOrder())
      asFloatBuffer().apply {
        put(this@toBuffer)
        position(0)
      }
    }
  }

  /**
   * Converts a [ShortArray] to a [ShortBuffer].
   *
   * @return A [ShortBuffer] containing the same elements as this array.
   */
  fun ShortArray.toBuffer(): ShortBuffer {
    return ByteBuffer.allocateDirect(this.size * Short.SIZE_BYTES).run {
      order(ByteOrder.nativeOrder())
      asShortBuffer().apply {
        put(this@toBuffer)
        position(0)
      }
    }
  }
}
