package com.jwoolston.android.uvc.interfaces.terminals;

import com.jwoolston.android.uvc.interfaces.VideoClassInterface;

/**
 * @author Jared Woolston (Jared.Woolston@gmail.com)
 */
public class VideoOutputTerminal extends VideoTerminal {

    protected static final int MIN_LENGTH = 9;

    protected static final int bSourceID = 7;
    protected static final int iTerminal = 8;

    private final int sourceID;

    public static boolean isOutputTerminal(byte[] descriptor) {
        return (descriptor.length >= MIN_LENGTH
                && descriptor[bDescriptorSubtype] == VideoClassInterface.VC_INF_SUBTYPE.VC_OUTPUT_TERMINAL.subtype);
    }

    public VideoOutputTerminal(byte[] descriptor) throws IllegalArgumentException {
        super(descriptor);
        if (!isOutputTerminal(descriptor)) {
            throw new IllegalArgumentException("Provided descriptor is not a valid video input terminal.");
        }
        sourceID = descriptor[bSourceID];
    }

    public int getSourceID() {
        return sourceID;
    }

    @Override
    public String toString() {
        return "OutputTerminal{" +
               "terminalType=" + getTerminalType() +
               ", terminalID=" + getTerminalID() +
               ", associatedTerminalID=" + getAssociatedTerminalID() +
               ", sourceID=" + sourceID +
               '}';
    }
}
