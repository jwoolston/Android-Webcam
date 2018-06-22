package com.jwoolston.android.uvc.interfaces.terminals;

import com.jwoolston.android.uvc.interfaces.AVideoClassInterface;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * @author Jared Woolston (Jared.Woolston@gmail.com)
 */
public class CameraTerminal extends VideoInputTerminal {

    private static final int LENGTH_DESCRIPTOR = 18;

    private static final int wObjectiveFocalLengthMin = 8;
    private static final int wObjectiveFocalLengthMax = 10;
    private static final int wOcularFocalLength       = 12;
    private static final int bControlSize             = 14;
    private static final int bmControls               = 15;

    private final int objectiveFocalLengthMin;
    private final int objectiveFocalLengthMax;
    private final int objectiveFocalLength;
    private final int ocularFocalLength;

    private final Set<Control> controlSet;

    public static boolean isCameraTerminal(byte[] descriptor) {
        if (descriptor.length != LENGTH_DESCRIPTOR) {
            return false;
        }
        if (descriptor[bDescriptorSubtype] != AVideoClassInterface.VC_INF_SUBTYPE.VC_INPUT_TERMINAL.subtype) {
            return false;
        }
        final TerminalType type = TerminalType.toTerminalType(descriptor[wTerminalType], descriptor[wTerminalType + 1]);
        return (type == TerminalType.ITT_CAMERA);
    }

    public CameraTerminal(byte[] descriptor) throws IllegalArgumentException {
        super(descriptor);
        if (!isCameraTerminal(descriptor)) {
            throw new IllegalArgumentException("The provided descriptor is not a valid Camera Terminal descriptor.");
        }
        objectiveFocalLengthMin = descriptor[wObjectiveFocalLengthMin] | (descriptor[wObjectiveFocalLengthMin + 1]
                                                                          << 8);
        objectiveFocalLengthMax = descriptor[wObjectiveFocalLengthMax] | (descriptor[wObjectiveFocalLengthMax + 1]
                                                                          << 8);
        objectiveFocalLength = descriptor[wOcularFocalLength] | (descriptor[wOcularFocalLength + 1] << 8);
        ocularFocalLength = descriptor[wOcularFocalLength] | (descriptor[wOcularFocalLength + 1] << 8);
        final int bitMap = descriptor[bmControls] | (descriptor[bmControls + 1] << 8) | (descriptor[bmControls + 2]
                                                                                         << 8);
        final Set<Control> controlSet = new HashSet<>();
        for (int i = 0; i < 24; ++i) {
            if ((bitMap & (0x01 << i)) != 0) {
                // The specified flag is present
                final Control control = Control.controlFromIndex(i);
                if (control == null) {
                    throw new IllegalArgumentException("Unknown camera control from index: " + i);
                }
                controlSet.add(control);
            }
        }
        // The contents of this set should never change
        this.controlSet = Collections.unmodifiableSet(controlSet);
    }

    public int getObjectiveFocalLengthMin() {
        return objectiveFocalLengthMin;
    }

    public int getObjectiveFocalLengthMax() {
        return objectiveFocalLengthMax;
    }

    public int getObjectiveFocalLength() {
        return objectiveFocalLength;
    }

    public int getOcularFocalLength() {
        return ocularFocalLength;
    }

    public boolean hasControl(Control control) {
        return controlSet.contains(control);
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
        for (Control control : controlSet) {
            builder.append(control).append(',');
        }
        builder.deleteCharAt(builder.length() - 1);
        builder.append('}');
        return builder.toString();
    }

    public static enum Control {
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

        public static Control controlFromIndex(int i) {
            final Control[] values = Control.values();
            if (i > values.length || i < 0) {
                return null;
            }
            for (Control control : values) {
                if (control.ordinal() == i) {
                    return control;
                }
            }
            return null;
        }
    }
}
