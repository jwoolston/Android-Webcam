package com.jwoolston.android.uvc.interfaces.units;

import com.jwoolston.android.uvc.interfaces.AVideoClassInterface;

import java.util.Set;

import timber.log.Timber;

/**
 * @author Jared Woolston (Jared.Woolston@gmail.com)
 */
public class VideoProcessingUnit extends VideoUnit {

    private static final String TAG = "VideoProcessingUnit";

    private static final int LENGTH = 11; //TODO: Spec says 13?

    private static final int bSourceID      = 4;
    private static final int wMaxMultiplier = 5;
    private static final int bmControls     = 8;
    private static final int iProcessing = 11;
    private static final int bmVideoStandards = 12;

    private final int mSourceID;

    /**
     * Represented multiplier is x100. For example, 4.5 is represented as 450.
     */
    private final int mMaxMultiplier;
    private final int mIndexProcessing = 0;

    private final Set<CONTROL>  mControlSet = null;
    private final Set<STANDARD> mStandardSet = null;

    public static boolean isVideoProcessingUnit(byte[] descriptor) {
        return (descriptor.length >= LENGTH & descriptor[bDescriptorSubtype] == AVideoClassInterface.VC_INF_SUBTYPE.VC_PROCESSING_UNIT.subtype);
    }

    public VideoProcessingUnit(byte[] descriptor) throws IllegalArgumentException {
        super(descriptor);
        Timber.d("Parsing video processing unit.");
        if (!isVideoProcessingUnit(descriptor)) { throw new IllegalArgumentException("The provided descriptor is not a valid Video Processing Unit descriptor."); }
        mSourceID = descriptor[bSourceID];
        mMaxMultiplier = descriptor[wMaxMultiplier] | (descriptor[wMaxMultiplier + 1] << 8);

        return; //FIXME: This is to deal with the discrepancy with the standard for testing

        /*mIndexProcessing = descriptor[iProcessing];

        final int controlBitMap = descriptor[bmControls] | (descriptor[bmControls + 1] << 8) | (descriptor[bmControls + 2] << 8);
        final Set<Control> controlSet = new HashSet<>();
        for (int i = 0; i < 24; ++i) {
            if ((controlBitMap & (0x01 << i)) != 0) {
                // The specified flag is present
                final Control control = Control.controlFromIndex(i);
                if (control == null) { throw new IllegalArgumentException("Unknown processing unit control from index: " + i); }
                controlSet.add(control);
            }
        }
        // The contents of this set should never change
        mControlSet = Collections.unmodifiableSet(controlSet);

        final int standardsBitMap = descriptor[bmVideoStandards];
        final Set<STANDARD> standardsSet = new HashSet<>();
        for (int i = 0; i < 8; ++i) {
            if ((standardsBitMap & (0x01 << i)) != 0) {
                // The specified flag is present
                final STANDARD standard = STANDARD.standardFromIndex(i);
                if (standard == null) { throw new IllegalArgumentException("Unknown video standard from index: " + i); }
                standardsSet.add(standard);
            }
        }
        // The contents of this set should never change
        mStandardSet = Collections.unmodifiableSet(standardsSet);*/
    }

    public int getSourceID() {
        return mSourceID;
    }

    public int getMaxMultiplier() {
        return mMaxMultiplier;
    }

    public int getIndexProcessing() {
        return mIndexProcessing;
    }

    public boolean hasControl(CONTROL control) {
        return mControlSet.contains(control);
    }

    public boolean supportsStandard(STANDARD standard) {
        return mStandardSet.contains(standard);
    }

    @Override
    public String toString() {
        final String base = "VideoProcessingUnit{" +
                ", Unit ID: " + getUnitID() +
                ", Source ID: " + getSourceID() +
                ", Max Multiplier: " + getMaxMultiplier() +
                ", Index Processing: " + getIndexProcessing() +
                ", Available Controls: ";
        StringBuilder builder = new StringBuilder(base);
        /*for (Control control : mControlSet) {
            builder.append(control).append(',');
        }
        builder.deleteCharAt(builder.length() - 1);
        builder.append(", Supported Standards: ");
        for (STANDARD standard : mStandardSet) {
            builder.append(standard).append(',');
        }
        builder.deleteCharAt(builder.length() - 1);*/
        builder.append('}');
        return builder.toString();
    }

    public static enum CONTROL {
        BRIGHTNESS,
        CONTRAST,
        HUE,
        SATURATION,
        SHARPNESS,
        GAMMA,
        WHITE_BALANCE_TEMPERATURE,
        WHITE_BALANCE_COMPONENT,
        BACKLIGHT_COMPENSATION,
        GAIN,
        POWER_LINE_FREQUENCY,
        HUE_AUTO,
        WHITE_BALANCE_TEMPERATURE_AUTO,
        WHITE_BALANCE_COMPONENT_AUTO,
        DIGITAL_MULTIPLIER,
        DIGITAL_MULTIPLIER_LIMIT,
        ANALOG_VIDEO_STANDARD,
        ANALOG_VIDEO_LOCK_STATUS,
        CONTRAST_AUTO,
        RESERVED_0,
        RESERVED_1,
        RESERVED_2,
        RESERVED_3,
        RESERVED_4;

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

    public static enum STANDARD {
        NONE,
        NTSC_525_60,
        PAL_625_50,
        SECAM_625_50,
        NTSC_625_50,
        PAL_525_60,
        RESERVED_0,
        RESERVED_1;

        public static STANDARD standardFromIndex(int i) {
            final STANDARD[] values = STANDARD.values();
            if (i > values.length || i < 0) {
                return null;
            }
            for (STANDARD control : values) {
                if (control.ordinal() == i) {
                    return control;
                }
            }
            return null;
        }
    }
}
