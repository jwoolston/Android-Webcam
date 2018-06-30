package com.jwoolston.android.uvc.streaming

/**
 * @author Jared Woolston (Jared.Woolston@gmail.com)
 */
interface VideoSampleFactory {

    fun addPayload(payload: Payload): VideoSample?
}
