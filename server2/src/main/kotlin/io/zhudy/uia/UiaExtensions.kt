package io.zhudy.uia

import java.nio.ByteBuffer

/**
 * @author Kevin Zou (kevinz@weghst.com)
 */
inline fun String.toByteBuffer() = ByteBuffer.wrap(this.toByteArray())
