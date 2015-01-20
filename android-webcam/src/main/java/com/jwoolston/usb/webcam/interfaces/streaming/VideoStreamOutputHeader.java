package com.jwoolston.usb.webcam.interfaces.streaming;

import android.util.Log;

/**
 * @author Jared Woolston (jwoolston@tenkiv.com)
 * @see UVC 1.5 Class Specification Table 3-15
 */
public class VideoStreamOutputHeader extends AVideoStreamHeader {

    private static final String TAG = "VideoStreamOutputHeader";

    private static final int MIN_HEADER_LENGTH = 9;

    private static final int bTerminalLink = 7;
    private static final int bControlSize  = 8; //n
    private static final int bmaControls   = 9; // Last index is 9 + p*n - n

    private final int    terminalLink;
    private final byte[] controlsMask;

    public VideoStreamOutputHeader(byte[] descriptor) throws IllegalArgumentException {
        super(descriptor);
        Log.d(TAG, "Parsing VideoStreamOutputHeader");
        if (descriptor.length < MIN_HEADER_LENGTH) throw new IllegalArgumentException("The provided descriptor is not long enough to be a valid VideoStreamOutputHeader.");
        terminalLink = (0xFF & descriptor[bTerminalLink]);
        final int sizeControls = (0xFF & descriptor[bControlSize]);
        controlsMask = new byte[sizeControls];
        System.arraycopy(descriptor, bmaControls, controlsMask, 0, controlsMask.length);
    }
}
