package com.jwoolston.android.uvc.interfaces.streaming;

import java.util.Arrays;

/**
 * @author Jared Woolston (jwoolston@idealcorp.com)
 */
public class UncompressedVideoFrame extends AVideoFrame {

    private static final String TAG = "UncompressedVideoFrame";

    public UncompressedVideoFrame(byte[] descriptor) throws IllegalArgumentException {
        super(descriptor);
    }

    @Override
    public String toString() {
        return "UncompressedVideoFrame{" +
                "mStillImageSupported=" + getStillImageSupported() +
                ", mFixedFrameRateEnabled=" + getFixedFrameRateEnabled() +
                ", mWidth=" + getWidth() +
                ", mHeight=" + getHeight() +
                ", mMinBitRate=" + getMinBitRate() +
                ", mMaxBitRate=" + getMaxBitRate() +
                ", mDefaultFrameInterval=" + getDefaultFrameInterval() +
                ", mFrameIntervalType=" + getFrameIntervalType() +
                ", mMinFrameInterval=" + getMinFrameInterval() +
                ", mMaxFrameInterval=" + getMaxFrameInterval() +
                ", mFrameIntervalStep=" + getFrameIntervalStep() +
                ", mFrameIntervals=" + Arrays.toString(getFrameIntervals()) +
                '}';
    }
}
