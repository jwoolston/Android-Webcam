package com.jwoolston.usb.webcam.interfaces.streaming;

/**
 * @author Jared Woolston (jwoolston@idealcorp.com)
 */
public class UncompressedVideoFrame extends AVideoFrame {

    private static final String TAG = "UncompressedVideoFrame";

    private static final int LENGTH_INTERVAL_TYPE_0 = 38;
    private static final int MIN_LENGTH_INTERVAL_TYPE_NOT_0 = 26; //26+4*n

    private static final int bFrameIndex = 3;
    private static final int bmCapabilites = 4;
    private static final int wWidth = 5;
    private static final int wHeight = 7;
    private static final int dwMinBitRate = 9;
    private static final int dwMaxBitRate = 13;
    private static final int dwMaxVideoFrameBufferSize = 17;
    private static final int dwDefaultFrameInterval = 21;
    private static final int bFrameIntervalType = 25; //n

    // Continuous frame intervals
    private static final int dwMinFrameInterval = 26;
    private static final int dwMaxFrameInterval = 30;
    private static final int dwFrameIntervalStep = 34;

    // Discrete frame intervals
    private static final int dwFrameInterval = 26;



    private int mFrameIntervalType;

    // Continuous frame intervals
    private final int mMinFrameInterval; // Shortest frame interval supported in 100 ns units.
    private final int mMaxFrameInterval; // Longest frame interval supported in 100 ns units.
    private final int mFrameIntervalStep; // Frame interval step in 100 ns units.

    // Discrete frame intervals
    private final int[] mFrameIntervals;

    UncompressedVideoFrame(byte[] descriptor) throws IllegalArgumentException {
        super(descriptor);
        if (descriptor.length < MIN_LENGTH_INTERVAL_TYPE_NOT_0) throw new IllegalArgumentException("The provided descriptor is not long enough to be an Uncompressed Video Frame.");
        mFrameIntervalType = (0xFF & descriptor[bFrameIntervalType]);
        if (mFrameIntervalType == 0 && descriptor.length < LENGTH_INTERVAL_TYPE_0) throw new IllegalArgumentException("The provided descriptor is not long enough to be an Uncompressed Video Frame.");
        if (mFrameIntervalType != 0 && descriptor.length < (MIN_LENGTH_INTERVAL_TYPE_NOT_0 + 4*mFrameIntervalType))
            throw new IllegalArgumentException("The provided descriptor is not long enough to be an Uncompressed Video Frame.");
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
                final int index = MIN_LENGTH_INTERVAL_TYPE_NOT_0 + 4*i - 4;
                mFrameIntervals[i] = ((0xFF & descriptor[index]) << 24) | ((0xFF & descriptor[index + 1]) << 16)
                        | ((0xFF & descriptor[index + 2]) << 8) | (0xFF & descriptor[index + 3]);
            }
        }
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
