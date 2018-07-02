package com.jwoolston.android.uvc.interfaces.streaming

import com.jwoolston.android.uvc.streaming.FrameSampleFactory
import com.jwoolston.android.uvc.streaming.VideoSampleFactory
import com.jwoolston.android.uvc.util.Hexdump
import timber.log.Timber

/**
 * The Uncompressed Video Format descriptor defines the characteristics of a specific video stream. It is used for
 * formats that carry uncompressed video information, including all YUV variants.
 * A Terminal corresponding to a USB IN or OUT endpoint, and the interface it belongs to, supports one or more format
 * definitions. To select a particular format, host software sends control requests to the corresponding interface.
 *
 * This specification defines uncompressed streams in YUV color spaces. Each frame is independently sent by the
 * device to the host.
 *
 * The vertical and horizontal dimensions of the image are constrained by the color component subsampling; image size
 * must be a multiple of macropixel block size. No padding is allowed. This Uncompressed video payload specification
 * supports any YUV format. The recommended YUV formats are one packed 4:2:2 YUV format (YUY2), one packed 4:2:0 YUV
 * format (M420), and two planar 4:2:0 YUV formats (NV12, I420).
 *
 * @author Jared Woolston (Jared.Woolston@gmail.com)
 * @see [USB Video Payload
 * Uncompressed 1.5 Specification §2.2 Table 2-1](http://www.usb.org/developers/docs/devclass_docs/USB_Video_Class_1_5.zip)
 */
class UncompressedVideoFormat @Throws(IllegalArgumentException::class)
constructor(descriptor: ByteArray) : VideoFormat<UncompressedVideoFrame>(descriptor) {

    val guid: String
    val bitsPerPixel: Int

    override val displayName: String
        get() = "Uncompressed"

    fun addUncompressedVideoFrame(frame: UncompressedVideoFrame) {
        Timber.d("Adding video frame: %s", frame)
        videoFrames.add(frame)
    }

    override fun getSampleFactory(maxPayload: Int, maxFrameSize: Int): VideoSampleFactory {
        return FrameSampleFactory(maxPayload, maxFrameSize)
    }

    init {
        if (descriptor.size < LENGTH) {
            throw IllegalArgumentException(
                    "The provided discriptor is not long enough for an Uncompressed Video Format.")
        }
        formatIndex = 0xFF and descriptor[bFormatIndex].toInt()
        numberFrames = 0xFF and descriptor[bNumFrameDescriptors].toInt()
        val GUIDBytes = ByteArray(16)
        System.arraycopy(descriptor, guidFormat, GUIDBytes, 0, GUIDBytes.size)
        bitsPerPixel = 0xFF and descriptor[bBitsPerPixel].toInt()
        defaultFrameIndex = 0xFF and descriptor[bDefaultFrameIndex].toInt()
        aspectRatioX = 0xFF and descriptor[bAspectRatioX].toInt()
        aspectRatioY = 0xFF and descriptor[bAspectRatioY].toInt()
        interlaceFlags = descriptor[bmInterlaceFlags]
        isCopyProtect = descriptor[bCopyProtect].toInt() != 0

        // Parse the GUID bytes to String
        val builder = StringBuilder()
        builder.append(Hexdump.toHexString(GUIDBytes[3])).append(Hexdump.toHexString(GUIDBytes[2]))
                .append(Hexdump.toHexString(GUIDBytes[1])).append(Hexdump.toHexString(GUIDBytes[0]))
        builder.append('-').append(Hexdump.toHexString(GUIDBytes[5])).append(Hexdump.toHexString(GUIDBytes[4]))
        builder.append('-').append(Hexdump.toHexString(GUIDBytes[7])).append(Hexdump.toHexString(GUIDBytes[6]))
        builder.append('-')
        for (i in 8..9) {
            builder.append(Hexdump.toHexString(GUIDBytes[i]))
        }
        builder.append('-')
        for (i in 10..15) {
            builder.append(Hexdump.toHexString(GUIDBytes[i]))
        }
        guid = builder.toString()
    }

    override fun toString(): String {
        return "UncompressedVideoFormat{" +
                "formatIndex=" + formatIndex +
                ", numberFrames=" + numberFrames +
                ", GUID=" + guid +
                ", bitsPerPixel=" + bitsPerPixel +
                ", defaultFrameIndex=" + defaultFrameIndex +
                ", AspectRatio=" + aspectRatioX + ":" + aspectRatioY +
                ", interlaceFlags=0x" + Hexdump.toHexString(interlaceFlags) +
                ", copyProtect=" + isCopyProtect +
                '}'.toString()
    }

    companion object {

        private val LENGTH = 27

        //|-----------------------------------------------|
        //| Format | GUID                                 |
        //|-----------------------------------------------|
        //| YUY2   | 32595559-0000-0010-8000-00AA00389B71 |
        //| NV12   | 3231564E-0000-0010-8000-00AA00389B71 |
        //| M420   | 3032344D-0000-0010-8000-00AA00389B71 |
        //| I420   | 30323449-0000-0010-8000-00AA00389B71 |
        //|-----------------------------------------------|

        private val YUY2_GUID = "32595559-0000-0010-8000-00AA00389B71"
        private val NV12_GUID = "3231564E-0000-0010-8000-00AA00389B71"
        private val M420_GUID = "3032344D-0000-0010-8000-00AA00389B71"
        private val I420_GUID = "30323449-0000-0010-8000-00AA00389B71"

        private val bFormatIndex = 3
        private val bNumFrameDescriptors = 4
        private val guidFormat = 5
        private val bBitsPerPixel = 21
        private val bDefaultFrameIndex = 22
        private val bAspectRatioX = 23
        private val bAspectRatioY = 24
        private val bmInterlaceFlags = 25
        private val bCopyProtect = 26
    }
}
