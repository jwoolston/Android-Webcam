package com.jwoolston.android.uvc.interfaces.units;

import com.jwoolston.android.uvc.interfaces.AVideoClassInterface;
import com.jwoolston.android.uvc.interfaces.Descriptor.Type;

/**
 * @author Jared Woolston (Jared.Woolston@gmail.com)
 */
public class VideoUnit {

    private static final String TAG = "VideoUnit";

    protected static final int bLength = 0;
    protected static final int bDescriptorType = 1;
    protected static final int bDescriptorSubtype = 2;
    protected static final int bUnitID = 3;

    private final int mUnitID;

    public static boolean isVideoUnit(byte[] descriptor) {
        if (descriptor[bDescriptorType] != Type.CS_INTERFACE.type) return false;
        final byte subtype = descriptor[bDescriptorSubtype];
        return (subtype == AVideoClassInterface.VC_INF_SUBTYPE.VC_SELECTOR_UNIT.subtype ||
            subtype == AVideoClassInterface.VC_INF_SUBTYPE.VC_PROCESSING_UNIT.subtype ||
            subtype == AVideoClassInterface.VC_INF_SUBTYPE.VC_ENCODING_UNIT.subtype ||
            subtype == AVideoClassInterface.VC_INF_SUBTYPE.VC_EXTENSION_UNIT.subtype);
    }

    protected VideoUnit(byte[] descriptor) throws IllegalArgumentException {
        if (descriptor.length < 4) throw new IllegalArgumentException("The provided descriptor is not long enough for an arbitrary video unit.");
        mUnitID = descriptor[bUnitID];
    }

    public int getUnitID() {
        return mUnitID;
    }
}
