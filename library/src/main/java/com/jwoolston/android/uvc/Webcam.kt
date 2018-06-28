package com.jwoolston.android.uvc

import android.content.Context
import android.hardware.usb.UsbDevice
import android.net.Uri
import com.jwoolston.android.uvc.interfaces.streaming.VideoFormat

/**
 * @author Jared Woolston (Jared.Woolston@gmail.com)
 */
interface Webcam {

    /**
     * The [UsbDevice] interface to this camera.
     *
     * @return camera [UsbDevice]
     */
    val device: UsbDevice

    /**
     * Determine if an active connection to the camera exists.
     *
     * @return true if connected to the camera
     */
    val isConnected: Boolean

    /**
     * Retrieves the list of available [VideoFormat]s.
     *
     * @return The available [VideoFormat]s on the device.
     */
    val availableFormats: List<VideoFormat<*>>

    /**
     * Begin streaming from the device and retrieve the [Uri] for the data stream for this [Webcam].
     *
     * @param context [Context] The application context.
     * @param format  The [VideoFormat] to stream in.
     *
     * @return [Uri] The data source [Uri].
     *
     * @throws StreamCreationException Thrown if there is a problem establishing the stream buffer.
     */
    @Throws(StreamCreationException::class)
    fun beginStreaming(context: Context, format: VideoFormat<*>): Uri

    /**
     * Terminates streaming from the device.
     *
     * @param context [Context] The application context.
     */
    fun terminateStreaming(context: Context)
}
