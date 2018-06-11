package com.jwoolston.android.uvc.interfaces.units;

import com.jwoolston.android.uvc.interfaces.AVideoClassInterface;

/**
 * @author Jared Woolston (Jared.Woolston@gmail.com)
 */
public class AVideoExtensionUnit extends VideoUnit {

    private static final int MIN_LENGTH = 24;

    private static final int guidExtensionCode = 4;
    private static final int bNumControls = 20;
    private static final int bNrInPins = 21; // p
    private static final int baSourceID = 22;
    private static final int bControlSize = 22; // + p = n
    private static final int bmControls = 23; // + p
    private static final int iExtension = 23; // + descriptor[bNrInPins] + p + n

    private final byte[] mGUID = new byte[bNumControls - guidExtensionCode];
    private final int mNumControls;
    private final int mNumInputPins;
    private final int[] mSourceIDs;
    private final byte[] mRawControlMask;
    private final int mIndexExtension;

    protected final int mControlSize;

    public static boolean isVideoExtensionUnit(byte[] descriptor) {
        return (descriptor.length >= MIN_LENGTH & descriptor[bDescriptorSubtype] == AVideoClassInterface.VC_INF_SUBTYPE.VC_EXTENSION_UNIT.subtype);
    }

    protected AVideoExtensionUnit(byte[] descriptor) throws IllegalArgumentException {
        super(descriptor);

        if (descriptor.length < MIN_LENGTH) throw new IllegalArgumentException("The provided descriptor is not long enough to be a video extension unit.");

        System.arraycopy(descriptor, guidExtensionCode, mGUID, 0, mGUID.length);
        mNumControls = descriptor[bNumControls];
        mNumInputPins = descriptor[bNrInPins];
        mSourceIDs = new int[mNumInputPins];
        for (int i = 0; i < mNumInputPins; ++i) {
            mSourceIDs[i] = descriptor[baSourceID + i];
        }
        mControlSize = descriptor[bControlSize + mNumInputPins];
        mRawControlMask = new byte[mControlSize];
        for (int i = 0; i < mControlSize; ++i) {
            mRawControlMask[i] = descriptor[bmControls + mNumInputPins];
        }
        mIndexExtension = descriptor[iExtension + mNumInputPins + mNumControls];
    }
}
