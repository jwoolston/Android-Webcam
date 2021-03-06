package com.jwoolston.android.uvc.interfaces;

import com.jwoolston.android.libusb.UsbInterface;

/**
 * @author Jared Woolston (Jared.Woolston@gmail.com)
 */
public abstract class VideoClassInterface extends UvcInterface {

    VideoClassInterface(UsbInterface usbInterface, byte[] descriptor) {
        super(usbInterface, descriptor);
    }

    public static enum VC_INF_SUBTYPE {
        VC_DESCRIPTOR_UNDEFINED(0x00),
        VC_HEADER(0x01),
        VC_INPUT_TERMINAL(0x02),
        VC_OUTPUT_TERMINAL(0x03),
        VC_SELECTOR_UNIT(0x04),
        VC_PROCESSING_UNIT(0x05),
        VC_EXTENSION_UNIT(0x06),
        VC_ENCODING_UNIT(0x07);

        public final byte subtype;

        private VC_INF_SUBTYPE(int subtype) {
            this.subtype = (byte) (subtype & 0xFF);
        }

        public static VC_INF_SUBTYPE getSubtype(byte subtype) {
            for (VC_INF_SUBTYPE s : VC_INF_SUBTYPE.values()) {
                if (s.subtype == subtype) {
                    return s;
                }
            }
            return null;
        }
    }
}
