package com.jwoolston.android.uvc.streaming

import java.io.IOException
import java.io.InputStream
import java.net.URL
import java.net.URLConnection

/**
 * @author Jared Woolston (Jared.Woolston@gmail.com)
 */
internal class VideoSampleUrlConnection(url: URL, private val stream: VideoSampleInputStream) : URLConnection(url) {

    @Throws(IOException::class)
    override fun connect() {

    }

    @Throws(IOException::class)
    override fun getInputStream(): InputStream {
        return stream
    }
}
