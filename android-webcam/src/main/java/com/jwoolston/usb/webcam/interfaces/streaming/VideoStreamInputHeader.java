package com.jwoolston.usb.webcam.interfaces.streaming;

import android.util.Log;

/**
 * @author Jared Woolston (jwoolston@tenkiv.com)
 * @see UVC 1.5 Class Specification Table 3-14
 */
public class VideoStreamInputHeader extends AVideoStreamHeader {

    private static final String TAG = "VideoStreamInputHeader";

    private static final int MIN_HEADER_LENGTH = 13;

    private static final int bmInfo              = 7;
    private static final int bTerminalLink       = 8;
    private static final int bStillCaptureMethod = 9;
    private static final int bTriggerSupport     = 10;
    private static final int bTriggerUsage       = 11;
    private static final int bControlSize        = 12; //n
    private static final int bmaControls         = 13; // Last index is 13 + p*n - n

    private final byte    infoMask;
    private final int     terminalLink;
    private final int     stillCaptureMethod;
    private final boolean hardwareTriggerSupported;
    private final boolean triggerStillImageCapture;
    private final byte[]  controlsMask;

    public VideoStreamInputHeader(byte[] descriptor) throws IllegalArgumentException {
        super(descriptor);
        Log.d(TAG, "Parsing VideoStreamInputHeader");
        if (descriptor.length < MIN_HEADER_LENGTH) throw new IllegalArgumentException("The provided descriptor is not long enough to be a valid VideoStreamInputHeader.");
        infoMask = descriptor[bmInfo];
        terminalLink = (0xFF & descriptor[bTerminalLink]);
        stillCaptureMethod = (0xFF & descriptor[bStillCaptureMethod]);
        hardwareTriggerSupported = ((0xFF & descriptor[bTriggerSupport]) == 1);
        triggerStillImageCapture = ((0xFF & descriptor[bTriggerUsage]) == 0); // True means still image should be captured (opposite of spec)
        final int sizeControls = (0xFF & descriptor[bControlSize]);
        controlsMask = new byte[sizeControls];
        System.arraycopy(descriptor, bmaControls, controlsMask, 0, controlsMask.length);
    }
}
