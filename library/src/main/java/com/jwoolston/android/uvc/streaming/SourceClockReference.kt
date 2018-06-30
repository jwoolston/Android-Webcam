package com.jwoolston.android.uvc.streaming

import java.nio.ByteBuffer

/**
 * @author Jared Woolston (Jared.Woolston@gmail.com)
 */
class SourceClockReference internal constructor(data: ByteBuffer) {

    val sourceTime: Int
    val tokenCounter: Int

    init {
        tokenCounter = data.int
        sourceTime = data.int
    }

    override fun toString(): String {
        val sb = StringBuilder("SourceClockReference{")
        sb.append("sourceTime=").append(sourceTime)
        sb.append(", tokenCounter=").append(tokenCounter)
        sb.append('}')
        return sb.toString()
    }
}
