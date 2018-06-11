package com.jwoolston.android.uvc.interfaces.units;

import android.util.Log;

import com.jwoolston.android.uvc.interfaces.AVideoClassInterface;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * @author Jared Woolston (jwoolston@idealcorp.com)
 */
public class VideoEncodingUnit extends VideoUnit {

    private static final String TAG = "VideoEncodingUnit";

    private static final int LENGTH = 13;

    private static final int bSourceID = 4;
    private static final int iEncoding = 5;
    private static final int bmControls = 7;
    private static final int bmControlsRuntime = 10;

    private final int mSourceID;
    private final int mIndexEncoding;

    private final Set<CONTROL>  mControlSet;
    private final Set<CONTROL> mRuntimeControlSet;

    public static boolean isVideoEncodingUnit(byte[] descriptor) {
        return (descriptor.length >= LENGTH & descriptor[bDescriptorSubtype] == AVideoClassInterface.VC_INF_SUBTYPE.VC_ENCODING_UNIT.subtype);
    }

    public VideoEncodingUnit(byte[] descriptor) throws IllegalArgumentException {
        super(descriptor);
        Log.d(TAG, "Parsing video processing unit.");
        if (!isVideoEncodingUnit(descriptor)) { throw new IllegalArgumentException("The provided descriptor is not a valid Video Encoding Unit descriptor."); }
        mSourceID = descriptor[bSourceID];
        mIndexEncoding = descriptor[iEncoding];

        final int controlBitMap = descriptor[bmControls] | (descriptor[bmControls + 1] << 8) | (descriptor[bmControls + 2] << 8);
        final Set<CONTROL> controlSet = new HashSet<>();
        for (int i = 0; i < 24; ++i) {
            if ((controlBitMap & (0x01 << i)) != 0) {
                // The specified flag is present
                final CONTROL control = CONTROL.controlFromIndex(i);
                if (control == null) { throw new IllegalArgumentException("Unknown processing unit control from index: " + i); }
                controlSet.add(control);
            }
        }
        // The contents of this set should never change
        mControlSet = Collections.unmodifiableSet(controlSet);

        final int runtimeControlBitMap = descriptor[bmControlsRuntime] | (descriptor[bmControlsRuntime + 1] << 8) | (descriptor[bmControlsRuntime + 2] << 8);
        final Set<CONTROL> runtimeControlSet = new HashSet<>();
        for (int i = 0; i < 24; ++i) {
            if ((runtimeControlBitMap & (0x01 << i)) != 0) {
                // The specified flag is present
                final CONTROL control = CONTROL.controlFromIndex(i);
                if (control == null) { throw new IllegalArgumentException("Unknown processing unit control from index: " + i); }
                runtimeControlSet.add(control);
            }
        }
        // The contents of this set should never change
        mRuntimeControlSet = Collections.unmodifiableSet(runtimeControlSet);
    }

    public int getSourceID() {
        return mSourceID;
    }

    public int getIndexEncoding() {
        return mIndexEncoding;
    }

    public boolean hasControl(CONTROL control) {
        return mControlSet.contains(control);
    }

    public boolean hasRuntimeControl(CONTROL control) {
        return mRuntimeControlSet.contains(control);
    }

    @Override
    public String toString() {
        final String base = "VideoProcessingUnit{" +
                ", Unit ID: " + getUnitID() +
                ", Source ID: " + getSourceID() +
                ", Index Encoding: " + getIndexEncoding() +
                ", Available Controls: ";
        StringBuilder builder = new StringBuilder(base);
        for (CONTROL control : mControlSet) {
            builder.append(control).append(',');
        }
        builder.deleteCharAt(builder.length() - 1);
        builder.append(", Runtime Controls: ");
        for (CONTROL control : mRuntimeControlSet) {
            builder.append(control).append(',');
        }
        builder.deleteCharAt(builder.length() - 1);
        builder.append('}');
        return builder.toString();
    }

    public static enum CONTROL {
        SELECT_LAYER,
        PROFILE_AND_TOOLSET,
        VIDEO_RESOLUTION,
        MINIMUM_FRAME_INTERVAL,
        SLICE_MODE,
        RATE_CONTROL_MODE,
        AVERAGE_BIT_RATE,
        CPB_SIZE,
        PEAK_BIT_RATE,
        QUANTIZATION_PARAMETER,
        SYNCH_AND_LONG_TERM_REF_FRAME,
        LONG_TERM_BUFFER,
        PICTURE_LONG_TERM_BUFFER,
        LTR_VALIDATION,
        LEVEL_IDC,
        SEI_MESSAGE,
        QP_RANGE,
        PRIORITY_ID,
        START_STOP_LAYER_VIEW,
        ERROR_RESILIENCY,
        RESERVED_0,
        RESERVED_1,
        RESERVED_2,
        RESERVED_3;

        public static CONTROL controlFromIndex(int i) {
            final CONTROL[] values = CONTROL.values();
            if (i > values.length || i < 0) {
                return null;
            }
            for (CONTROL control : values) {
                if (control.ordinal() == i) {
                    return control;
                }
            }
            return null;
        }
    }
}
