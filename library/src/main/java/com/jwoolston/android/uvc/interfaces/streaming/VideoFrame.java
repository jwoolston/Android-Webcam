package com.jwoolston.android.uvc.interfaces.streaming;

import com.jwoolston.android.uvc.util.ArrayTools;

/**
 * @author Jared Woolston (Jared.Woolston@gmail.com)
 */
public class VideoFrame {

    private static final int LENGTH_INTERVAL_TYPE_0         = 38;
    private static final int MIN_LENGTH_INTERVAL_TYPE_NOT_0 = 26; //26+4*n

    private static final int bFrameIndex   = 3;
    private static final int bmCapabilites = 4;
    private static final int wWidth        = 5;
    private static final int wHeight       = 7;
    private static final int dwMinBitRate  = 9;
    private static final int dwMaxBitRate  = 13;

    private static final int dwMaxVideoFrameBufferSize = 17;
    private static final int dwDefaultFrameInterval    = 21;
    private static final int bFrameIntervalType        = 25;

    // Continuous frame intervals
    private static final int dwMinFrameInterval  = 26;
    private static final int dwMaxFrameInterval  = 30;
    private static final int dwFrameIntervalStep = 34;

    // Discrete frame intervals
    private static final int dwFrameInterval = 26;

    private final int     frameIndex;
    private final boolean stillImageSupported;
    private final boolean fixedFrameRateEnabled;
    private final int     width;
    private final int     height;
    private final int     minBitRate;
    private final int     maxBitRate;
    private final int     defaultFrameInterval;
    private final int     frameIntervalType;

    // Continuous frame intervals
    private final int minFrameInterval; // Shortest frame interval supported in 100 ns units.
    private final int maxFrameInterval; // Longest frame interval supported in 100 ns units.
    private final int frameIntervalStep; // Frame interval step in 100 ns units.

    // Discrete frame intervals
    private final int[] frameIntervals;

    VideoFrame(byte[] descriptor) throws IllegalArgumentException {
        frameIntervalType = (0xFF & descriptor[bFrameIntervalType]);
        if (frameIntervalType == 0 && descriptor.length < LENGTH_INTERVAL_TYPE_0) {
            throw new IllegalArgumentException(
                    "The provided descriptor is not long enough to be an Uncompressed Video Frame.");
        }
        if (frameIntervalType != 0 && descriptor.length < (MIN_LENGTH_INTERVAL_TYPE_NOT_0 + 4 * frameIntervalType)) {
            throw new IllegalArgumentException(
                    "The provided descriptor is not long enough to be an Uncompressed Video Frame.");
        }
        frameIndex = (0xFF & descriptor[bFrameIndex]);
        stillImageSupported = (descriptor[bmCapabilites] & 0x01) != 0;
        fixedFrameRateEnabled = (descriptor[bmCapabilites] & 0x02) != 0;

        width = ArrayTools.shortLE(descriptor, wWidth);
        height = ArrayTools.shortLE(descriptor, wHeight);

        minBitRate = ArrayTools.integerLE(descriptor , dwMinBitRate);
        maxBitRate = ArrayTools.integerLE(descriptor, dwMaxBitRate);
        defaultFrameInterval = ((0xFF & descriptor[dwDefaultFrameInterval + 3]) << 24)
                               | ((0xFF & descriptor[dwDefaultFrameInterval + 2]) << 16)
                               | ((0xFF & descriptor[dwDefaultFrameInterval + 1]) << 8)
                               | (0xFF & descriptor[dwDefaultFrameInterval]);

        if (frameIntervalType == 0) {
            minFrameInterval = ArrayTools.integerLE(descriptor, dwMinFrameInterval);
            maxFrameInterval = ArrayTools.integerLE(descriptor, dwMaxFrameInterval);
            frameIntervalStep = ArrayTools.integerLE(descriptor, dwFrameIntervalStep);
            frameIntervals = null;
        } else {
            frameIntervals = new int[frameIntervalType];
            minFrameInterval = 0;
            maxFrameInterval = 0;
            frameIntervalStep = 0;
            for (int i = 0; i < frameIntervalType; ++i) {
                final int index = dwFrameInterval + 4 * i - 4;
                frameIntervals[i] = ArrayTools.integerLE(descriptor, index);
            }
        }
    }

    protected int[] getFrameIntervals() {
        return frameIntervals;
    }

    public boolean getStillImageSupported() {
        return stillImageSupported;
    }

    public boolean getFixedFrameRateEnabled() {
        return fixedFrameRateEnabled;
    }

    public int getFrameIndex() {
        return frameIndex;
    }

    public boolean isStillImageSupported() {
        return stillImageSupported;
    }

    public boolean isFixedFrameRateEnabled() {
        return fixedFrameRateEnabled;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public int getMinBitRate() {
        return minBitRate;
    }

    public int getMaxBitRate() {
        return maxBitRate;
    }

    public int getDefaultFrameInterval() {
        return defaultFrameInterval;
    }

    public int getFrameIntervalType() {
        return frameIntervalType;
    }

    public int getMinFrameInterval() {
        return minFrameInterval;
    }

    public int getMaxFrameInterval() {
        return maxFrameInterval;
    }

    public int getFrameIntervalStep() {
        return frameIntervalStep;
    }

    public int getFrameInterval(int index) {
        return frameIntervals[index];
    }
}
