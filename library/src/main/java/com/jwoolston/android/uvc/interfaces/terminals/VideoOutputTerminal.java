package com.jwoolston.android.uvc.interfaces.terminals;

import com.jwoolston.android.uvc.interfaces.AVideoClassInterface;

/**
 * The Output Terminal (OT) is used as an interface between Units inside the video function and the "outside world".
 * It serves as an outlet for video information, flowing out of the video function. Its function is to represent a
 * sink of outgoing data. The video data stream enters the Output Terminal through a single Input Pin.
 * An Output Terminal can represent outputs from the video function other than USB IN endpoints. A Liquid Crystal
 * Display (LCD) screen built into a video device or a composite video out connector are examples of such an output.
 * However, if the video stream is leaving the video function by means of a USB IN endpoint, there is a one-to-one
 * relationship between that endpoint and its associated Output Terminal. The class-specific Input Header descriptor
 * contains a field that holds a direct reference to this Output Terminal (see section 3.9.2.1, “Input Header
 * Descriptor”). The Host needs to use both the endpoint descriptors and the Output Terminal descriptor to fully
 * understand the characteristics and capabilities of the Output Terminal. Stream-related parameters are stored in
 * the endpoint descriptors. Control-related parameters are stored in the Terminal descriptor.
 *
 * @author Jared Woolston (Jared.Woolston@gmail.com)
 * @see <a href=http://www.usb.org/developers/docs/devclass_docs/USB_Video_Class_1_5.zip>UVC 1.5 Class
 * Specification §2.3.2</a>
 */
public class VideoOutputTerminal extends VideoTerminal {

    protected static final int MIN_LENGTH = 9;

    protected static final int bSourceID = 7;
    protected static final int iTerminal = 8;

    private final int sourceID;

    public static boolean isOutputTerminal(byte[] descriptor) {
        return (descriptor.length >= MIN_LENGTH
                && descriptor[bDescriptorSubtype] == AVideoClassInterface.VC_INF_SUBTYPE.VC_OUTPUT_TERMINAL.subtype);
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
