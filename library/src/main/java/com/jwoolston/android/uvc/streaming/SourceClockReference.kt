package com.jwoolston.android.uvc.streaming

import com.jwoolston.android.uvc.util.ArrayTools

/**
 * @author Jared Woolston (Jared.Woolston@gmail.com)
 */
class SourceClockReference internal constructor(data: ByteArray, offset: Int) {

    val sourceTime: Int
    val tokenCounter: Int

    init {
        tokenCounter = ArrayTools.shortLE(data, offset).toInt()
        sourceTime = ArrayTools.integerLE(data, offset + 2)
    }

    override fun toString(): String {
        val sb = StringBuilder("SourceClockReference{")
        sb.append("sourceTime=").append(sourceTime)
        sb.append(", tokenCounter=").append(tokenCounter)
        sb.append('}')
        return sb.toString()
    }
}
