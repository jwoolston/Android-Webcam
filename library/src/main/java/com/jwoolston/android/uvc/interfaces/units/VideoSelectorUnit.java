package com.jwoolston.android.uvc.interfaces.units;

import com.jwoolston.android.uvc.interfaces.VideoClassInterface;
import java.util.Arrays;

/**
 * The Selector Unit (SU) selects from n input data streams and routes them unaltered to the single output stream. It
 * represents a source selector, capable of selecting among a number of sources. It has an Input Pin for each source
 * stream and a single Output Pin
 *
 * @author Jared Woolston (Jared.Woolston@gmail.com)
 * @see <a href=http://www.usb.org/developers/docs/devclass_docs/USB_Video_Class_1_5.zip>UVC 1.5 Class
 * Specification §2.3.4</a>
 */
public class VideoSelectorUnit extends VideoUnit {

    private static final int MIN_LENGTH = 6;

    private static final int bNrInPins  = 4;
    private static final int baSourceID = 5;
    private static final int iSelector  = 5;

    private final int   numInPins;
    private final int[] sourceIDs;
    private final int   selectorValue;

    public static boolean isVideoSelectorUnit(byte[] descriptor) {
        return (descriptor.length >= MIN_LENGTH
                & descriptor[bDescriptorSubtype] == VideoClassInterface.VC_INF_SUBTYPE.VC_SELECTOR_UNIT.subtype);
    }

    public VideoSelectorUnit(byte[] descriptor) throws IllegalArgumentException {
        super(descriptor);
        if (descriptor.length < MIN_LENGTH) {
            throw new IllegalArgumentException(
                    "The provided descriptor is not long enough to be a video selector unit.");
        }
        numInPins = descriptor[bNrInPins];
        sourceIDs = new int[numInPins];
        for (int i = 0; i < numInPins; ++i) {
            sourceIDs[i] = descriptor[baSourceID + i];
        }
        selectorValue = descriptor[iSelector + numInPins];
    }

    @Override
    public String toString() {
        return "VideoSelectorUnit{" +
               "Unit ID: " + getUnitID() +
               ", numInPins=" + numInPins +
               ", sourceIDs=" + Arrays.toString(sourceIDs) +
               ", selectorValue=" + selectorValue +
               '}';
    }
}
