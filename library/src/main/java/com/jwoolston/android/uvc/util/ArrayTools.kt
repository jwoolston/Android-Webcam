package com.jwoolston.android.uvc.util

import android.support.annotation.IntRange

/**
 * @author Jared Woolston (Jared.Woolston@gmail.com)
 */
object ArrayTools {

    fun extractShort(array: ByteArray, @IntRange(from = 0) offset: Int): Short {
        return (((0xFF and array[offset + 1].toInt()) shl 8) or (0xFF and array[offset].toInt())).toShort()
    }

    fun extractInteger(array: ByteArray, @IntRange(from = 0) offset: Int): Int {
        return (((0xFF and array[offset + 3].toInt()) shl 24) or ((0xFF and array[offset + 2].toInt()) shl 16)
                or ((0xFF and array[offset + 1].toInt()) shl 8) or (0xFF and array[offset].toInt()))
    }
}
