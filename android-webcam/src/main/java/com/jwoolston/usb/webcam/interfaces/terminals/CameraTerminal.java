package com.jwoolston.usb.webcam.interfaces.terminals;

import com.jwoolston.usb.webcam.interfaces.AVideoClassInterface;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * @author Jared Woolston (jwoolston@idealcorp.com)
 */
public class CameraTerminal extends VideoInputTerminal {

    private static final String TAG = "CameraTerminal";

    private static final int LENGTH_DESCRIPTOR = 18;

    private static final int wObjectiveFocalLengthMin = 8;
    private static final int wObjectiveFocalLengthMax = 10;
    private static final int wOcularFocalLength       = 12;
    private static final int bControlSize             = 14;
    private static final int bmControls               = 15;

    private final int mObjectiveFocalLengthMin;
    private final int mObjectiveFocalLengthMax;
    private final int mObjectiveFocalLength;
    private final int mOcularFocalLength;

    private final Set<CONTROL> mControlSet;

    public static boolean isCameraTerminal(byte[] descriptor) {
        if (descriptor.length != LENGTH_DESCRIPTOR) { return false; }
        if (descriptor[bDescriptorSubtype] != AVideoClassInterface.VC_INF_SUBTYPE.VC_INPUT_TERMINAL.subtype) { return false; }
        final TERMINAL_TYPE type = TERMINAL_TYPE.toTerminalType(descriptor[wTerminalType], descriptor[wTerminalType + 1]);
        return (type == TERMINAL_TYPE.ITT_CAMERA);
    }

    public CameraTerminal(byte[] descriptor) throws IllegalArgumentException {
        super(descriptor);
        if (!isCameraTerminal(descriptor)) { throw new IllegalArgumentException("The provided descriptor is not a valid Camera Terminal descriptor."); }
        mObjectiveFocalLengthMin = descriptor[wObjectiveFocalLengthMin] | (descriptor[wObjectiveFocalLengthMin + 1] << 8);
        mObjectiveFocalLengthMax = descriptor[wObjectiveFocalLengthMax] | (descriptor[wObjectiveFocalLengthMax + 1] << 8);
        mObjectiveFocalLength = descriptor[wOcularFocalLength] | (descriptor[wOcularFocalLength + 1] << 8);
        mOcularFocalLength = descriptor[wOcularFocalLength] | (descriptor[wOcularFocalLength + 1] << 8);
        final int bitMap = descriptor[bmControls] | (descriptor[bmControls + 1] << 8) | (descriptor[bmControls + 2] << 8);
        final Set<CONTROL> controlSet = new HashSet<>();
        for (int i = 0; i < 24; ++i) {
            if ((bitMap & (0x01 << i)) != 0) {
                // The specified flag is present
                final CONTROL control = CONTROL.controlFromIndex(i);
                if (control == null) { throw new IllegalArgumentException("Unknown camera control from index: " + i); }
                controlSet.add(control);
            }
        }
        // The contents of this set should never change
        mControlSet = Collections.unmodifiableSet(controlSet);
    }

    public int getObjectiveFocalLengthMin() {
        return mObjectiveFocalLengthMin;
    }

    public int getObjectiveFocalLengthMax() {
        return mObjectiveFocalLengthMax;
    }

    public int getObjectiveFocalLength() {
        return mObjectiveFocalLength;
    }

    public int getOcularFocalLength() {
        return mOcularFocalLength;
    }

    public boolean hasControl(CONTROL control) {
        return mControlSet.contains(control);
    }

    @Override
    public String toString() {
        final String base = "CameraTerminal{" +
                "Terminal Type=" + getTerminalType() +
                ", Min Objective Focal Length: " + getObjectiveFocalLengthMin() +
                ", Max Objective Focal Length: " + getObjectiveFocalLengthMax() +
                ", Objective Focal Length: " + getObjectiveFocalLength() +
                ", Ocular Focal Length: " + getOcularFocalLength() +
                ", Available Controls: ";
        final StringBuilder builder = new StringBuilder(base);
        for (CONTROL control : mControlSet) {
            builder.append(control).append(',');
        }
        builder.deleteCharAt(builder.length() - 1);
        builder.append('}');
        return builder.toString();
    }

    public static enum CONTROL {
        SCANNING_MODE,
        AUTO_EXPOSURE_MODE,
        AUTO_EXPOSURE_PRIORITY,
        EXPOSURE_TIME_ABSOLUTE,
        EXPOSURE_TIME_RELATIVE,
        FOCUS_ABSOLUTE,
        FOCUS_RELATIVE,
        IRIS_ABSOLUTE,
        IRIS_RELATIVE,
        ZOOM_ABSOLUTE,
        ZOOM_RELATIVE,
        PAN_TILT_ABSOLUTE,
        PAN_TILT_RELATIVE,
        ROLL_ABSOLUTE,
        ROLL_RELATIVE,
        RESERVED_0,
        RESERVED_1,
        FOCUS_AUTO,
        PRIVACY,
        FOCUS_SIMPLE,
        WINDOW,
        REGION_OF_INTEREST,
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
