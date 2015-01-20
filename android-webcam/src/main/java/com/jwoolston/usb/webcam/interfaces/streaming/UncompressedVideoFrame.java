package com.jwoolston.usb.webcam.interfaces.streaming;

/**
 * @author Jared Woolston (jwoolston@idealcorp.com)
 */
public class UncompressedVideoFrame extends AVideoFrame {

    private static final String TAG = "UncompressedVideoFrame";

    private static final int LENGTH_INTERVAL_TYPE_0 = 38;
    private static final int MIN_LENGTH_INTERVAL_TYPE_NOT_0 = 26;

    private static final int bFrameIndex = 3;
    private static final int bmCapabilites = 4;
    private static final int wWidth = 5;
    private static final int wHeight = 7;
    private static final int dwMinBitRate = 9;
    private static final int dwMaxBitRate = 13;
    private static final int dwMaxVideoFrameBufferSize = 17;
    private static final int dwDefaultFrameInterval = 21;
    private static final int bFrameIntervalType = 25;

    UncompressedVideoFrame(byte[] descriptor) throws IllegalArgumentException {
        super(descriptor);

    }
}
