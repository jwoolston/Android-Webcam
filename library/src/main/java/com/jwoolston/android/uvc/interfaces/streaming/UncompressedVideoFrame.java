package com.jwoolston.android.uvc.interfaces.streaming;

import java.util.Arrays;

/**
 * Uncompressed Video Frame descriptors (or Frame descriptors for short) are used to describe the decoded video and
 * still-image frame dimensions and other frame-specific characteristics supported by a particular stream. One or
 * more Frame descriptors follow the Uncompressed Video Format descriptor they correspond to. The Frame descriptor is
 * also used to determine the range of frame intervals supported for the frame size specified.
 * The Uncompressed Video Frame descriptor is used only for video formats for which the Uncompressed Video Format
 * descriptor applies (see section 3.1.1, "Uncompressed Video Format Descriptor").
 *
 * @author Jared Woolston (Jared.Woolston@gmail.com)
 * @see <a href=http://www.usb.org/developers/docs/devclass_docs/USB_Video_Class_1_5.zip>USB Video Payload
 * Uncompressed 1.5 Specification §2.2 Table 2-1</a>
 */
public class UncompressedVideoFrame extends AVideoFrame {

    public UncompressedVideoFrame(byte[] descriptor) throws IllegalArgumentException {
        super(descriptor);
    }

    @Override
    public String toString() {
        return "UncompressedVideoFrame{" +
               "Frame Index=" + getFrameIndex() +
               ", StillImageSupported=" + getStillImageSupported() +
               ", FixedFrameRateEnabled=" + getFixedFrameRateEnabled() +
               ", Width=" + getWidth() +
               ", Height=" + getHeight() +
               ", MinBitRate=" + getMinBitRate() +
               ", MaxBitRate=" + getMaxBitRate() +
               ", DefaultFrameInterval=" + getDefaultFrameInterval() +
               ", FrameIntervalType=" + getFrameIntervalType() +
               ", MinFrameInterval=" + getMinFrameInterval() +
               ", MaxFrameInterval=" + getMaxFrameInterval() +
               ", FrameIntervalStep=" + getFrameIntervalStep() +
               ", FrameIntervals=" + Arrays.toString(getFrameIntervals()) +
               '}';
    }
}
