package com.jwoolston.android.uvc.interfaces.streaming

import com.jwoolston.android.uvc.streaming.FrameSampleFactory
import com.jwoolston.android.uvc.streaming.VideoSampleFactory
import com.jwoolston.android.uvc.util.Hexdump

import timber.log.Timber

/**
 * @author Jared Woolston (Jared.Woolston@gmail.com)
 */
class MJPEGVideoFormat @Throws(IllegalArgumentException::class)
constructor(descriptor: ByteArray) : VideoFormat<MJPEGVideoFrame>(descriptor) {

    val fixedSampleSize: Boolean

    override val displayName: String
        get() = "MJPEG"

    override fun getSampleFactory(maxPayload: Int, maxFrameSize: Int): VideoSampleFactory {
        return FrameSampleFactory(maxPayload, maxFrameSize)
    }

    init {
        if (descriptor.size < LENGTH) {
            throw IllegalArgumentException("The provided descriptor is not long enough for an MJPEG Video Format.")
        }
        formatIndex = 0xFF and descriptor[bFormatIndex].toInt()
        numberFrames = 0xFF and descriptor[bNumFrameDescriptors].toInt()
        fixedSampleSize = descriptor[bmFlags].toInt() != 0
        defaultFrameIndex = 0xFF and descriptor[bDefaultFrameIndex].toInt()
        aspectRatioX = 0xFF and descriptor[bAspectRatioX].toInt()
        aspectRatioY = 0xFF and descriptor[bAspectRatioY].toInt()
        interlaceFlags = (0xFF and descriptor[bmInterlaceFlags].toInt()).toByte()
        isCopyProtect = (descriptor[bCopyProtect].toInt() != 0)
    }

    fun addMJPEGVideoFrame(frame: MJPEGVideoFrame) {
        Timber.d("Adding video frame: %s", frame)
        videoFrames.add(frame)
    }

    override fun toString(): String {
        return "MJPEGVideoFormat{" +
                "formatIndex=" + formatIndex +
                ", numberFrames=" + numberFrames +
                ", fixedSampleSize=" + fixedSampleSize +
                ", defaultFrameIndex=" + defaultFrameIndex +
                ", AspectRatio=" + aspectRatioX + ":" + aspectRatioY +
                ", interlaceFlags=0x" + Hexdump.toHexString(interlaceFlags) +
                ", copyProtect=" + isCopyProtect +
                '}'.toString()
    }

    companion object {

        private val LENGTH = 11

        private val bFormatIndex = 3
        private val bNumFrameDescriptors = 4
        private val bmFlags = 5
        private val bDefaultFrameIndex = 6
        private val bAspectRatioX = 7
        private val bAspectRatioY = 8
        private val bmInterlaceFlags = 9
        private val bCopyProtect = 10
    }
}
