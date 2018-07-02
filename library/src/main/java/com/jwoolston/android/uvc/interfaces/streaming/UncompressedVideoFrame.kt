package com.jwoolston.android.uvc.interfaces.streaming

import java.util.*

/**
 * Uncompressed Video Frame descriptors (or Frame descriptors for short) are used to describe the decoded video and
 * still-image frame dimensions and other frame-specific characteristics supported by a particular stream. One or
 * more Frame descriptors follow the Uncompressed Video Format descriptor they correspond to. The Frame descriptor is
 * also used to determine the range of frame intervals supported for the frame size specified.
 * The Uncompressed Video Frame descriptor is used only for video formats for which the Uncompressed Video Format
 * descriptor applies (see section 3.1.1, "Uncompressed Video Format Descriptor").
 *
 * @author Jared Woolston (Jared.Woolston@gmail.com)
 * @see [USB Video Payload
 * Uncompressed 1.5 Specification §2.2 Table 2-1](http://www.usb.org/developers/docs/devclass_docs/USB_Video_Class_1_5.zip)
 */
class UncompressedVideoFrame @Throws(IllegalArgumentException::class)
constructor(descriptor: ByteArray) : VideoFrame(descriptor) {

    override fun toString(): String {
        return "UncompressedVideoFrame{" +
                "Frame Index=" + frameIndex +
                ", StillImageSupported=" + stillImageSupported +
                ", FixedFrameRateEnabled=" + fixedFrameRateEnabled +
                ", Width=" + width +
                ", Height=" + height +
                ", MinBitRate=" + minBitRate +
                ", MaxBitRate=" + maxBitRate +
                ", DefaultFrameInterval=" + defaultFrameInterval +
                ", FrameIntervalType=" + frameIntervalType +
                ", MinFrameInterval=" + minFrameInterval +
                ", MaxFrameInterval=" + maxFrameInterval +
                ", FrameIntervalStep=" + frameIntervalStep +
                ", FrameIntervals=" + Arrays.toString(frameIntervals) +
                '}'.toString()
    }
}
