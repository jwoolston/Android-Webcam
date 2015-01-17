package com.jwoolston.usb.webcam.interfaces.terminals;

import com.jwoolston.usb.webcam.interfaces.VideoClassInterface;

/**
 * @author Jared Woolston (jwoolston@idealcorp.com)
 */
public class VideoInputTerminal extends VideoTerminal {

    protected static final int MIN_LENGTH = 8;

    protected static final int iTerminal = 7;

    private final INPUT_TERMINAL_TYPE mTerminalType;

    public static boolean isInputTerminal(byte[] descriptor) {
        return (descriptor.length >= MIN_LENGTH && descriptor[bDescriptorSubtype] == VideoClassInterface.VC_INF_SUBTYPE.VC_INPUT_TERMINAL.subtype);
    }

    public VideoInputTerminal(byte[] descriptor) throws IllegalArgumentException {
        if (!isInputTerminal(descriptor)) throw new IllegalArgumentException("Provided descriptor is not a valid video input terminal.");
        mTerminalType = INPUT_TERMINAL_TYPE.toInputTerminalType(descriptor[wTerminalType], descriptor[wTerminalType + 1]);
    }

    public INPUT_TERMINAL_TYPE getTerminalType() {
        return mTerminalType;
    }

    @Override
    public String toString() {
        return "InputTerminal{" +
                "mTerminalType=" + mTerminalType +
                '}';
    }

    /**
     * @see <a href=http://www.usb.org/developers/docs/devclass_docs/USB_Video_Class_1_5.zip>UVC Class specification v1.5</a> Table B-2.
     */
    public static enum INPUT_TERMINAL_TYPE {
        /**
         * Vendor specific input terminal.
         */
        ITT_VENDOR_SPECIFIC(0x0200),

        /**
         * Camera sensor. To be used only in Camera Terminal descriptors.
         */
        ITT_CAMERA(0x0201),

        /**
         * Sequential media. To be used only in Media Transport Terminal descriptors.
         */
        ITT_MEDIA_TRANSPORT_INPUT(0x0202);

        public int code;

        private INPUT_TERMINAL_TYPE(int code) {
            this.code = code;
        }

        public static INPUT_TERMINAL_TYPE toInputTerminalType(byte low, byte high) {
            final int code = ((high << 8) | low);
            for (INPUT_TERMINAL_TYPE terminal : INPUT_TERMINAL_TYPE.values()) {
                if (terminal.code == code) {
                    return terminal;
                }
            }
            return null;
        }
    }
}
