package com.jwoolston.usb.webcam.interfaces.streaming;

import java.util.Arrays;

/**
 * @author Jared Woolston (jwoolston@idealcorp.com)
 */
public class MJPEGVideoFrame extends AVideoFrame {

    public MJPEGVideoFrame(byte[] descriptor) {
        super(descriptor);
    }

    @Override
    public String toString() {
        return "MJPEGVideoFrame{" +
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
