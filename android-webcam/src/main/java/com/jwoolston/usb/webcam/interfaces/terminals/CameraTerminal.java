package com.jwoolston.usb.webcam.interfaces.terminals;

import com.jwoolston.usb.webcam.interfaces.VideoClassInterface;

/**
 * @author Jared Woolston (jwoolston@idealcorp.com)
 */
public class CameraTerminal extends VideoInputTerminal {

    private static final int LENGTH_DESCRIPTOR = 18;

    private static final int wObjectiveFocalLengthMin = 8;
    private static final int wObjectiveFocalLengthMax = 10;
    private static final int wOcularFocalLength       = 12;
    private static final int bControlSize             = 14;
    private static final int bmControls               = 15;

    public static boolean isCameraTerminal(byte[] descriptor) {
        if (descriptor.length != LENGTH_DESCRIPTOR) { return false; }
        if (descriptor[bDescriptorSubtype] != VideoClassInterface.VC_INF_SUBTYPE.VC_OUTPUT_TERMINAL.subtype) { return false; }
        final INPUT_TERMINAL_TYPE type = INPUT_TERMINAL_TYPE.toInputTerminalType(descriptor[wTerminalType], descriptor[wTerminalType + 1]);
        return (type == INPUT_TERMINAL_TYPE.ITT_CAMERA);
    }

    public CameraTerminal(byte[] descriptor) throws IllegalArgumentException {
        super(descriptor);
        if (!isCameraTerminal(descriptor)) throw new IllegalArgumentException("The provided descriptor is not a valid CameraTerminal descriptor.");
    }

    @Override
    public String toString() {
        return "CameraTerminal{" +
                "mTerminalType=" + getTerminalType() +
                '}';
    }

    public static enum CONTROLS {
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
        RESERVED_3
    }
}
