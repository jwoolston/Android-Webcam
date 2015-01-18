package com.jwoolston.usb.webcam.interfaces;

import android.hardware.usb.UsbInterface;
import android.util.Log;

import com.jwoolston.usb.webcam.interfaces.terminals.CameraTerminal;
import com.jwoolston.usb.webcam.interfaces.terminals.VideoInputTerminal;
import com.jwoolston.usb.webcam.interfaces.terminals.VideoOutputTerminal;
import com.jwoolston.usb.webcam.interfaces.terminals.VideoTerminal;
import com.jwoolston.usb.webcam.interfaces.units.VideoProcessingUnit;
import com.jwoolston.usb.webcam.interfaces.units.VideoSelectorUnit;
import com.jwoolston.usb.webcam.interfaces.units.VideoUnit;

/**
 * @author Jared Woolston (jwoolston@idealcorp.com)
 */
public class VideoClassInterface extends AInterface {

    private static final String TAG = "VideoClassInterface";

    private static final int VIDEO_CLASS_HEADER_LENGTH = 12;

    private static final int bLength = 0;
    private static final int bDescriptorType = 1;
    private static final int bDescriptorSubType = 2;
    private static final int bcdUVC = 3;
    private static final int wTotlaLength = 5;
    private static final int dwClockFrequency = 7;
    private static final int bInCollection = 11;
    private static final int baInterfaceNr_1 = 12;

    private int mNumberStreamingInterfaces;
    private int[] mStreamingInterfaces;

    public static VideoClassInterface parseInterface(byte[] descriptor) throws IllegalArgumentException {
        Log.d(TAG, "Parsing Video Class Interface header.");
        if (descriptor.length < VIDEO_CLASS_HEADER_LENGTH) throw new IllegalArgumentException("The provided descriptor is not a valid Video Class Interface.");
        final int numStreamInf = descriptor[bInCollection];
        Log.d(TAG, "There " + (numStreamInf == 1 ? "is" : "are") + numStreamInf + " VideoStreaming interface" + (numStreamInf == 1 ? "" : "s"));
        for (int i = 0; i < numStreamInf; ++i) {
            Log.d(TAG, "VideoStreaming interface " + i + ": " + descriptor[baInterfaceNr_1 + i]);
        }
        return null;
    }

    protected VideoClassInterface(UsbInterface usbInterface, int indexInterface) {
        super(usbInterface, indexInterface);
    }

    @Override
    public boolean isClassInterfaceHeader(byte[] descriptor) {
        return (descriptor.length >= VIDEO_CLASS_HEADER_LENGTH && (descriptor[bDescriptorSubType] == VC_INF_SUBTYPE.VC_HEADER.subtype));
    }

    @Override
    public boolean isTerminal(byte[] descriptor) {
        return VideoTerminal.isVideoTerminal(descriptor);
    }

    @Override
    public boolean isUnit(byte[] descriptor) {
        return VideoUnit.isVideoUnit(descriptor);
    }

    @Override
    public void parseClassInterfaceHeader(byte[] descriptor) throws IllegalArgumentException {
        Log.d(TAG, "Parsing Video Class Interface header.");
        if (descriptor.length < VIDEO_CLASS_HEADER_LENGTH) throw new IllegalArgumentException("The provided descriptor is not a valid Video Class Interface.");
        mNumberStreamingInterfaces = descriptor[bInCollection];
        mStreamingInterfaces = new int[mNumberStreamingInterfaces];
        Log.d(TAG, "There " + (mNumberStreamingInterfaces == 1 ? "is " : "are ") + mNumberStreamingInterfaces + " VideoStreaming interface" + (mNumberStreamingInterfaces == 1 ? "" : "s"));
        for (int i = 0; i < mNumberStreamingInterfaces; ++i) {
            mStreamingInterfaces[i] = descriptor[baInterfaceNr_1 + i];
            Log.d(TAG, "VideoStreaming interface " + i + ": " + mStreamingInterfaces[i]);
        }
    }

    @Override
    public void parseTerminal(byte[] descriptor) throws IllegalArgumentException {
        Log.d(TAG, "Parsing Video Class Interface Terminal.");
        if (VideoInputTerminal.isInputTerminal(descriptor)) {
            if (CameraTerminal.isCameraTerminal(descriptor)) {
                // Parse as camera terminal
                final CameraTerminal cameraTerminal = new CameraTerminal(descriptor);
                Log.d(TAG, "" + cameraTerminal);
            } else {
                // Parse as input terminal
                VideoInputTerminal inputTerminal = new VideoInputTerminal(descriptor);
                Log.d(TAG, "" + inputTerminal);
            }
        } else if (VideoOutputTerminal.isOutputTerminal(descriptor)) {
            // Parse as output terminal
            VideoOutputTerminal outputTerminal = new VideoOutputTerminal(descriptor);
            Log.d(TAG, "" + outputTerminal);
        } else {
            throw new IllegalArgumentException("The provided descriptor is not a valid Video Terminal.");
        }
    }

    @Override
    public void parseUnit(byte[] descriptor) throws IllegalArgumentException {
        Log.d(TAG, "Parsing Video Class Interface Unit.");
        if (VideoSelectorUnit.isVideoSelectorUnit(descriptor)) {
            // Parse as video selector unit
            final VideoSelectorUnit selectorUnit = new VideoSelectorUnit(descriptor);
            Log.d(TAG, "" + selectorUnit);
        } else if (VideoProcessingUnit.isVideoProcessingUnit(descriptor)) {
            // Parse as video processing unit
            final VideoProcessingUnit processingUnit = new VideoProcessingUnit(descriptor);
            Log.d(TAG, "" + processingUnit);
        } else {
            throw new IllegalArgumentException("The provided descriptor is not a valid Video Unit");
        }
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
            this.subtype = (byte) subtype;
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
