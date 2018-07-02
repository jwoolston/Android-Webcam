package com.jwoolston.android.uvc.streaming

import java.io.IOException

/**
 * @author Jared Woolston (Jared.Woolston@gmail.com)
 */
interface VideoSampleFactory {

    @Throws(IOException::class)
    fun addPayload(payload: Payload): VideoSample?
}
