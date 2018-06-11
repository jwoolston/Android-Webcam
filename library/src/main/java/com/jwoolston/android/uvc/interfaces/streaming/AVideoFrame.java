package com.jwoolston.android.uvc.interfaces.streaming;

/**
 * @author Jared Woolston (jwoolston@idealcorp.com)
 */
class AVideoFrame {

    private static final int LENGTH_INTERVAL_TYPE_0         = 38;
    private static final int MIN_LENGTH_INTERVAL_TYPE_NOT_0 = 26; //26+4*n

    private static final int bFrameIndex                    = 3;
    private static final int bmCapabilites                  = 4;
    private static final int wWidth                         = 5;
    private static final int wHeight                        = 7;
    private static final int dwMinBitRate                   = 9;
    private static final int dwMaxBitRate                   = 13;

    private static final int dwMaxVideoFrameBufferSize = 17;
    private static final int dwDefaultFrameInterval    = 21;
    private static final int bFrameIntervalType        = 25; //n

    // Continuous frame intervals
    private static final int dwMinFrameInterval  = 26;
    private static final int dwMaxFrameInterval  = 30;
    private static final int dwFrameIntervalStep = 34;

    // Discrete frame intervals
    private static final int dwFrameInterval = 26;

    private final int     mFrameIndex;
    private final boolean mStillImageSupported;
    private final boolean mFixedFrameRateEnabled;
    private final int     mWidth;
    private final int     mHeight;
    private final int     mMinBitRate;
    private final int     mMaxBitRate;
    private final int     mDefaultFrameInterval;
    private final int     mFrameIntervalType;

    // Continuous frame intervals
    private final int mMinFrameInterval; // Shortest frame interval supported in 100 ns units.
    private final int mMaxFrameInterval; // Longest frame interval supported in 100 ns units.
    private final int mFrameIntervalStep; // Frame interval step in 100 ns units.

    // Discrete frame intervals
    private final int[] mFrameIntervals;

    AVideoFrame(byte[] descriptor) throws IllegalArgumentException {
        mFrameIntervalType = (0xFF & descriptor[bFrameIntervalType]);
        if (mFrameIntervalType == 0 && descriptor.length < LENGTH_INTERVAL_TYPE_0) throw new IllegalArgumentException("The provided descriptor is not long enough to be an Uncompressed Video Frame.");
        if (mFrameIntervalType != 0 && descriptor.length < (MIN_LENGTH_INTERVAL_TYPE_NOT_0 + 4 * mFrameIntervalType))
            throw new IllegalArgumentException("The provided descriptor is not long enough to be an Uncompressed Video Frame.");
        mFrameIndex = (0xFF & descriptor[bFrameIndex]);
        mStillImageSupported = (descriptor[bmCapabilites] & 0x01) != 0;
        mFixedFrameRateEnabled = (descriptor[bmCapabilites] & 0x02) != 0;

        mWidth = ((0xFF & descriptor[wWidth]) << 8) | (0xFF & descriptor[wWidth + 1]);
        mHeight = ((0xFF & descriptor[wHeight]) << 8) | (0xFF & descriptor[wHeight + 1]);

        mMinBitRate = ((0xFF & descriptor[dwMinBitRate]) << 24) | ((0xFF & descriptor[dwMinBitRate + 1]) << 16)
                | ((0xFF & descriptor[dwMinBitRate + 2]) << 8) | (0xFF & descriptor[dwMinBitRate + 3]);
        mMaxBitRate = ((0xFF & descriptor[dwMaxBitRate]) << 24) | ((0xFF & descriptor[dwMaxBitRate + 1]) << 16)
                | ((0xFF & descriptor[dwMaxBitRate + 2]) << 8) | (0xFF & descriptor[dwMaxBitRate + 3]);
        mDefaultFrameInterval = ((0xFF & descriptor[dwDefaultFrameInterval]) << 24) | ((0xFF & descriptor[dwDefaultFrameInterval + 1]) << 16)
                | ((0xFF & descriptor[dwDefaultFrameInterval + 2]) << 8) | (0xFF & descriptor[dwDefaultFrameInterval + 3]);

        if (mFrameIntervalType == 0) {
            mMinFrameInterval = ((0xFF & descriptor[dwMinFrameInterval]) << 24) | ((0xFF & descriptor[dwMinFrameInterval + 1]) << 16)
                    | ((0xFF & descriptor[dwMinFrameInterval + 2]) << 8) | (0xFF & descriptor[dwMinFrameInterval + 3]);
            mMaxFrameInterval = ((0xFF & descriptor[dwMaxFrameInterval]) << 24) | ((0xFF & descriptor[dwMaxFrameInterval + 1]) << 16)
                    | ((0xFF & descriptor[dwMaxFrameInterval + 2]) << 8) | (0xFF & descriptor[dwMaxFrameInterval + 3]);
            mFrameIntervalStep = ((0xFF & descriptor[dwFrameIntervalStep]) << 24) | ((0xFF & descriptor[dwFrameIntervalStep + 1]) << 16)
                    | ((0xFF & descriptor[dwFrameIntervalStep + 2]) << 8) | (0xFF & descriptor[dwFrameIntervalStep + 3]);
            mFrameIntervals = null;
        } else {
            mFrameIntervals = new int[mFrameIntervalType];
            mMinFrameInterval = 0;
            mMaxFrameInterval = 0;
            mFrameIntervalStep = 0;
            for (int i = 0; i < mFrameIntervalType; ++i) {
                final int index = dwFrameInterval + 4 * i - 4;
                mFrameIntervals[i] = ((0xFF & descriptor[index]) << 24) | ((0xFF & descriptor[index + 1]) << 16)
                        | ((0xFF & descriptor[index + 2]) << 8) | (0xFF & descriptor[index + 3]);
            }
        }
    }

    protected int[] getFrameIntervals() {
        return mFrameIntervals;
    }

    public boolean getStillImageSupported() {
        return mStillImageSupported;
    }

    public boolean getFixedFrameRateEnabled() {
        return mFixedFrameRateEnabled;
    }

    public int getFrameIndex() {
        return mFrameIndex;
    }

    public boolean isStillImageSupported() {
        return mStillImageSupported;
    }

    public boolean isFixedFrameRateEnabled() {
        return mFixedFrameRateEnabled;
    }

    public int getWidth() {
        return mWidth;
    }

    public int getHeight() {
        return mHeight;
    }

    public int getMinBitRate() {
        return mMinBitRate;
    }

    public int getMaxBitRate() {
        return mMaxBitRate;
    }

    public int getDefaultFrameInterval() {
        return mDefaultFrameInterval;
    }

    public int getFrameIntervalType() {
        return mFrameIntervalType;
    }

    public int getMinFrameInterval() {
        return mMinFrameInterval;
    }

    public int getMaxFrameInterval() {
        return mMaxFrameInterval;
    }

    public int getFrameIntervalStep() {
        return mFrameIntervalStep;
    }

    public int getFrameInterval(int index) {
        return mFrameIntervals[index];
    }
}
