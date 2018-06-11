package com.jwoolston.android.uvc.interfaces.terminals;

import com.jwoolston.android.uvc.interfaces.AVideoClassInterface;

/**
 * @author Jared Woolston (jwoolston@idealcorp.com)
 */
public abstract class VideoTerminal {

    protected static final int bLength            = 0;
    protected static final int bDescriptorType    = 1;
    protected static final int bDescriptorSubtype = 2;
    protected static final int bTerminalID    = 3;
    protected static final int wTerminalType  = 4;
    protected static final int bAssocTerminal = 6;

    private final TERMINAL_TYPE mTerminalType;
    private final int mTerminalID;
    private final int mAssociatedTerminalID;

    public static boolean isVideoTerminal(byte[] descriptor) {
        return (descriptor[bDescriptorSubtype] == AVideoClassInterface.VC_INF_SUBTYPE.VC_INPUT_TERMINAL.subtype ||
                descriptor[bDescriptorSubtype] == AVideoClassInterface.VC_INF_SUBTYPE.VC_OUTPUT_TERMINAL.subtype);
    }

    protected VideoTerminal(byte[] descriptor) {
        mTerminalType = TERMINAL_TYPE.toTerminalType(descriptor[wTerminalType], descriptor[wTerminalType + 1]);
        mTerminalID = descriptor[bTerminalID];
        mAssociatedTerminalID = descriptor[bAssocTerminal];
    }

    public TERMINAL_TYPE getTerminalType() {
        return mTerminalType;
    }

    public int getTerminalID() {
        return mTerminalID;
    }

    public int getAssociatedTerminalID() {
        return mAssociatedTerminalID;
    }

    /**
     * @see <a href=http://www.usb.org/developers/docs/devclass_docs/USB_Video_Class_1_5.zip>UVC Class specification v1.5</a> Table B-1 through B-4.
     */
    public static enum TERMINAL_TYPE {
        /**
         * A terminal dealing with a signal carried over a vendor-specific interface. The vendor-specfic interface descriptor must contain a field that
         * references the terminal.
         */
        TT_VENDOR_SPECIFIC(0x0100),

        /**
         * A terminal dealing with a signal carried over an endpoint in a VideoStreaming interface. The VideoStreaming interface descriptor points to the
         * associated terminal through the bTerminalLink field.
         */
        TT_STREAMING(0x0101),

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
        ITT_MEDIA_TRANSPORT_INPUT(0x0202),

        /**
         * Vendor specific output terminal.
         */
        OTT_VENDOR_SPECIFIC(0x0300),

        /**
         * Generic display (LCD, CRT, etc.).
         */
        OTT_DISPLAY(0x0301),

        /**
         * Sequential media. To be used only in Media Transport Terminal descriptors.
         */
        OTT_MEDIA_TRANSPORT_OUTPUT(0x0302),

        /**
         * Vendor-Specific External Terminal.
         */
        EXTERNAL_VENDOR_SPECIFIC(0x0400),

        /**
         * Composite video connector.
         */
        COMPOSITE_CONNECTOR(0x0401),

        /**
         * S-video connector.
         */
        SVIDEO_CONNECTOR(0x0402),

        /**
         * Component video connector.
         */
        COMPONENT_CONNECTOR(0x0403);

        public int code;

        private TERMINAL_TYPE(int code) {
            this.code = (code & 0xFFFF);
        }

        public static TERMINAL_TYPE toTerminalType(byte low, byte high) {
            final int code = ((high << 8) | low);
            for (TERMINAL_TYPE terminal : TERMINAL_TYPE.values()) {
                if (terminal.code == code) {
                    return terminal;
                }
            }
            return null;
        }
    }
}
