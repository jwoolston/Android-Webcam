package com.jwoolston.android.uvc.interfaces.terminals;

import com.jwoolston.android.uvc.interfaces.AVideoClassInterface;

/**
 * @author Jared Woolston (jwoolston@idealcorp.com)
 */
public class VideoOutputTerminal extends VideoTerminal {

    protected static final int MIN_LENGTH = 9;

    protected static final int bSourceID = 7;
    protected static final int iTerminal = 8;

    private final int mSourceID;

    public static boolean isOutputTerminal(byte[] descriptor) {
        return (descriptor.length >= MIN_LENGTH && descriptor[bDescriptorSubtype] == AVideoClassInterface.VC_INF_SUBTYPE.VC_OUTPUT_TERMINAL.subtype);
    }

    public VideoOutputTerminal(byte[] descriptor) throws IllegalArgumentException {
        super(descriptor);
        if (!isOutputTerminal(descriptor)) throw new IllegalArgumentException("Provided descriptor is not a valid video input terminal.");
        mSourceID = descriptor[bSourceID];
    }

    public int getSourceID() {
        return mSourceID;
    }

    @Override
    public String toString() {
        return "OutputTerminal{" +
                "mTerminalType=" + getTerminalType() +
                ", mTerminalID=" + getTerminalID() +
                ", mAssociatedTerminalID=" + getAssociatedTerminalID() +
                ", mSourceID=" + mSourceID +
                '}';
    }
}
