package com.github.lookupgroup27.lookup.util.opengl

import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer
import java.nio.ShortBuffer

object BufferUtils {
  fun FloatArray.toBuffer(): FloatBuffer {
    return ByteBuffer.allocateDirect(this.size * Float.SIZE_BYTES).run {
      order(ByteOrder.nativeOrder())
      asFloatBuffer().apply {
        put(this@toBuffer)
        position(0)
      }
    }
  }

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
