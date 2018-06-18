package com.jwoolston.android.uvc.interfaces.streaming;

import java.util.Arrays;

/**
 * @author Jared Woolston (Jared.Woolston@gmail.com)
 */
public class UncompressedVideoFrame extends AVideoFrame {

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
