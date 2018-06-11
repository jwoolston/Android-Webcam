package com.jwoolston.android.uvc.interfaces.units;

import com.jwoolston.android.uvc.interfaces.AVideoClassInterface;

import java.util.Arrays;

/**
 * @author Jared Woolston (jwoolston@idealcorp.com)
 */
public class VideoSelectorUnit extends VideoUnit {

    private static final int MIN_LENGTH = 6;

    private static final int bNrInPins = 4;
    private static final int baSourceID = 5;
    private static final int iSelector = 5;

    private final int mNumInPins;
    private final int[] mSourceIDs;
    private final int iSelectorValue;

    public static boolean isVideoSelectorUnit(byte[] descriptor) {
        return (descriptor.length >= MIN_LENGTH & descriptor[bDescriptorSubtype] == AVideoClassInterface.VC_INF_SUBTYPE.VC_SELECTOR_UNIT.subtype);
    }

    public VideoSelectorUnit(byte[] descriptor) throws IllegalArgumentException {
        super(descriptor);
        if (descriptor.length < MIN_LENGTH) throw new IllegalArgumentException("The provided descriptor is not long enough to be a video selector unit.");
        mNumInPins = descriptor[bNrInPins];
        mSourceIDs = new int[mNumInPins];
        for (int i = 0; i < mNumInPins; ++i) {
            mSourceIDs[i] = descriptor[baSourceID + i];
        }
        iSelectorValue = descriptor[iSelector + mNumInPins];
    }

    @Override
    public String toString() {
        return "VideoSelectorUnit{" +
                "mNumInPins=" + mNumInPins +
                ", mSourceIDs=" + Arrays.toString(mSourceIDs) +
                ", iSelectorValue=" + iSelectorValue +
                '}';
    }
}
