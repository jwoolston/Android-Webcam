package com.jwoolston.android.uvc.interfaces.terminals;

import com.jwoolston.android.uvc.interfaces.VideoClassInterface;

/**
 * The Input Terminal (IT) is used as an interface between the video function’s "outside world" and other Units
 * inside the video function. It serves as a receptacle for data flowing into the video function. Its function is to
 * represent a source of incoming data after this data has been extracted from the data source. The data may include
 * audio and metadata associated with a video stream. These physical streams are grouped into a cluster of logical
 * streams, leaving the Input Terminal through a single Output Pin.
 * An Input Terminal can represent inputs to the video function other than USB OUT endpoints. A CCD sensor on a video
 * camera or a composite video input is an example of such a non-USB input. However, if the video stream is entering
 * the video function by means of a USB OUT endpoint, there is a one-to-one relationship between that endpoint and
 * its associated Input Terminal. The class-specific Output Header descriptor contains a field that holds a direct
 * reference to this Input Terminal (see section 3.9.2.2, “Output Header Descriptor”). The Host needs to use both the
 * endpoint descriptors and the Input Terminal descriptor to get a full understanding of the characteristics and
 * capabilities of the Input Terminal. Stream-related parameters are stored in the endpoint descriptors.
 * Control-related parameters are stored in the Terminal descriptor.
 *
 * @author Jared Woolston (Jared.Woolston@gmail.com)
 * @see <a href=http://www.usb.org/developers/docs/devclass_docs/USB_Video_Class_1_5.zip>UVC 1.5 Class
 * Specification §2.3.2</a>
 */
public class VideoInputTerminal extends VideoTerminal {

    protected static final int MIN_LENGTH = 8;

    protected static final int iTerminal = 7;

    public static boolean isInputTerminal(byte[] descriptor) {
        return (descriptor.length >= MIN_LENGTH
                && descriptor[bDescriptorSubtype] == VideoClassInterface.VC_INF_SUBTYPE.VC_INPUT_TERMINAL.subtype);
    }

    public VideoInputTerminal(byte[] descriptor) throws IllegalArgumentException {
        super(descriptor);
        if (!isInputTerminal(descriptor)) {
            throw new IllegalArgumentException("Provided descriptor is not a valid video input terminal.");
        }
    }

    @Override
    public String toString() {
        return "InputTerminal{" +
               "terminalType=" + getTerminalType() +
               ", terminalID=" + getTerminalID() +
               ", associatedTerminalID=" + getAssociatedTerminalID() +
               '}';
    }
}
