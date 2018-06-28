package com.jwoolston.android.uvc

import android.content.Context
import android.hardware.usb.UsbDevice
import android.net.Uri
import com.jwoolston.android.libusb.DevicePermissionDenied
import com.jwoolston.android.uvc.interfaces.streaming.VideoFormat

/**
 * @author Jared Woolston (Jared.Woolston@gmail.com)
 */
internal class WebcamImpl @Throws(UnknownDeviceException::class, DevicePermissionDenied::class)
constructor(private val context: Context, override val device: UsbDevice) : Webcam {
    private val webcamConnection: WebcamConnection

    override val isConnected: Boolean
        get() = webcamConnection.isConnected

    /**
     * Retrieves the list of available [VideoFormat]s.
     *
     * @return The available [VideoFormat]s on the device.
     */
    override val availableFormats: List<VideoFormat<*>>
        get() = webcamConnection.availableFormats

    init {

        webcamConnection = WebcamConnection(context.applicationContext, device)
    }

    @Throws(StreamCreationException::class)
    override fun beginStreaming(context: Context, format: VideoFormat<*>): Uri {
        return webcamConnection.beginConnectionStreaming(context, format)
    }

    override fun terminateStreaming(context: Context) {
        webcamConnection.terminateConnection(context)
    }
}
