package com.jwoolston.android.uvc.streaming

import java.io.IOException
import java.net.URL
import java.net.URLConnection
import java.net.URLStreamHandler

/**
 * @author Jared Woolston (Jared.Woolston@gmail.com)
 */
internal class VideoSampleUrlHandler(private val stream: VideoSampleInputStream) : URLStreamHandler() {

    @Throws(IOException::class)
    override fun openConnection(url: URL): URLConnection {
        return VideoSampleUrlConnection(url, stream)
    }
}
