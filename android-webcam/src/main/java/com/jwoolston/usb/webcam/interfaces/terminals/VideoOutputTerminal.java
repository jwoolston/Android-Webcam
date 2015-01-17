package com.jwoolston.usb.webcam.interfaces.terminals;

import com.jwoolston.usb.webcam.interfaces.VideoClassInterface;

/**
 * @author Jared Woolston (jwoolston@idealcorp.com)
 */
public class VideoOutputTerminal extends VideoTerminal {

    protected static final int MIN_LENGTH = 9;

    protected static final int bSourceID = 7;
    protected static final int iTerminal = 8;

    public static boolean isOutputTerminal(byte[] descriptor) {
        return (descriptor.length >= MIN_LENGTH && descriptor[bDescriptorSubtype] == VideoClassInterface.VC_INF_SUBTYPE.VC_OUTPUT_TERMINAL.subtype);
    }
}
