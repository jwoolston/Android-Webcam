package com.jwoolston.usb.webcam.interfaces.terminals;

import com.jwoolston.usb.webcam.interfaces.AVideoClassInterface;

/**
 * @author Jared Woolston (jwoolston@idealcorp.com)
 */
public class VideoInputTerminal extends VideoTerminal {

    protected static final int MIN_LENGTH = 8;

    protected static final int iTerminal = 7;

    public static boolean isInputTerminal(byte[] descriptor) {
        return (descriptor.length >= MIN_LENGTH && descriptor[bDescriptorSubtype] == AVideoClassInterface.VC_INF_SUBTYPE.VC_INPUT_TERMINAL.subtype);
    }

    public VideoInputTerminal(byte[] descriptor) throws IllegalArgumentException {
        super(descriptor);
        if (!isInputTerminal(descriptor)) throw new IllegalArgumentException("Provided descriptor is not a valid video input terminal.");
    }

    @Override
    public String toString() {
        return "InputTerminal{" +
                "mTerminalType=" + getTerminalType() +
                ", mTerminalID=" + getTerminalID() +
                ", mAssociatedTerminalID=" + getAssociatedTerminalID() +
                '}';
    }
}
