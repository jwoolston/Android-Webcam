package com.jwoolston.android.uvc.interfaces.terminals;

import com.jwoolston.android.uvc.interfaces.VideoClassInterface;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * The Camera Terminal (CT) controls mechanical (or equivalent digital) features of the device component that
 * transmits the video stream. As such, it is only applicable to video capture devices with controllable lens or
 * sensor characteristics. A Camera Terminal is always represented as an Input Terminal with a single output pin. It
 * provides support for the following features.
 *
 * <br>-Scanning Mode (Progressive or Interlaced)
 * <br>-Auto-Exposure Mode
 * <br>-Auto-Exposure Priority
 * <br>-Exposure Time
 * <br>-Focus
 * <br>-Auto-Focus
 * <br>-Simple Focus
 * <br>-Iris
 * <br>-Zoom
 * <br>-Pan
 * <br>-Roll
 * <br>-Tilt
 * <br>-Digital Windowing
 * <br>-Region of Interest
 *
 * Support for any particular control is optional. The Focus control can optionally provide support for an auto
 * setting (with an on/off state). If the auto setting is supported and set to the on state, the device will provide
 * automatic focus adjustment, and read requests will reflect the automatically set value. Attempts to
 * programmatically set the Focus control when in auto mode shall result in protocol STALL with an error code of
 * <b>bRequestErrorCode</b> = “Wrong State”. When leaving Auto-Focus mode (entering manual focus mode), the control
 * shall remain at the value that was in effect just before the transition.
 *
 * @author Jared Woolston (Jared.Woolston@gmail.com)
 * @see <a href=http://www.usb.org/developers/docs/devclass_docs/USB_Video_Class_1_5.zip>UVC 1.5 Class
 * Specification §2.3.3</a>
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
        if (descriptor[bDescriptorSubtype] != VideoClassInterface.VC_INF_SUBTYPE.VC_INPUT_TERMINAL.subtype) {
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
                            "terminalType=" + getTerminalType() +
                            ", terminalID=" + getTerminalID() +
                            ", associatedTerminalID=" + getAssociatedTerminalID() +
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
