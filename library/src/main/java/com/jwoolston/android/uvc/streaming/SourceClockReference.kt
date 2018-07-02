package com.jwoolston.android.uvc.streaming

import java.nio.ByteBuffer

/**
 * @author Jared Woolston (Jared.Woolston@gmail.com)
 */
data class SourceClockReference (

    val sourceTime: Int,
    val tokenCounter: Int
)

val ByteBuffer.asSourceClockReference: SourceClockReference
    get() = SourceClockReference(tokenCounter = int, sourceTime = int)
