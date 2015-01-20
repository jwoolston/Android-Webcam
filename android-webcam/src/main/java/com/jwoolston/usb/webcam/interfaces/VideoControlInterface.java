package com.jwoolston.usb.webcam.interfaces;

import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbInterface;
import android.util.Log;

import com.jwoolston.usb.webcam.interfaces.terminals.CameraTerminal;
import com.jwoolston.usb.webcam.interfaces.terminals.VideoInputTerminal;
import com.jwoolston.usb.webcam.interfaces.terminals.VideoOutputTerminal;
import com.jwoolston.usb.webcam.interfaces.terminals.VideoTerminal;
import com.jwoolston.usb.webcam.interfaces.units.AVideoExtensionUnit;
import com.jwoolston.usb.webcam.interfaces.units.VideoEncodingUnit;
import com.jwoolston.usb.webcam.interfaces.units.VideoProcessingUnit;
import com.jwoolston.usb.webcam.interfaces.units.VideoSelectorUnit;
import com.jwoolston.usb.webcam.interfaces.units.VideoUnit;

import java.util.Arrays;

/**
 * @author Jared Woolston (jwoolston@idealcorp.com)
 */
public class VideoControlInterface extends AVideoClassInterface {

    private static final String TAG = "VideoControlInterface";

    private static final int VIDEO_CLASS_HEADER_LENGTH = 12;

    private static final int bDescriptorSubType = 2;
    private static final int bcdUVC           = 3;
    private static final int wTotalLength     = 5;
    private static final int dwClockFrequency = 7;
    private static final int bInCollection    = 11;
    private static final int baInterfaceNr_1  = 12;

    private int   mUVC;
    private int   mNumberStreamingInterfaces;
    private int[] mStreamingInterfaces;

    public static VideoControlInterface parseVideoControlInterface(UsbDevice device, byte[] descriptor) throws IllegalArgumentException {
        Log.d(TAG, "Parsing Video Class Interface header.");

        final UsbInterface usbInterface = AInterface.getUsbInterface(device, descriptor);
        if (usbInterface == null) throw new IllegalArgumentException("The provided descriptor refers to a non-existant interface.");
        return new VideoControlInterface(usbInterface, descriptor);
    }

    VideoControlInterface(UsbInterface usbInterface, byte[] descriptor) {
        super(usbInterface, descriptor);
    }

    @Override
    public void parseClassDescriptor(byte[] descriptor) {
        if (isClassInterfaceHeader(descriptor)) {
            parseClassInterfaceHeader(descriptor);
            Log.d(TAG, "" + this);
        } else if (isTerminal(descriptor)) {
            parseTerminal(descriptor);
        } else if (isUnit(descriptor)) {
            parseUnit(descriptor);
        } else {
            throw new IllegalArgumentException("Unknown class specific interface type.");
        }
    }

    @Override
    public void parseAlternateFunction(byte[] descriptor) {
        // Do nothing
        Log.d(TAG, "parseAlternateFunction() called for VideoControlInterface.");
    }

    @Override
    public String toString() {
        return "VideoControlInterface{" +
                "mUVC=" + mUVC +
                ", mNumberStreamingInterfaces=" + mNumberStreamingInterfaces +
                ", mStreamingInterfaces=" + Arrays.toString(mStreamingInterfaces) +
                '}';
    }

    public int getNumberStreamingInterfaces() {
        return mNumberStreamingInterfaces;
    }

    public int getUVCVersion() {
        return mUVC;
    }

    public boolean isClassInterfaceHeader(byte[] descriptor) {
        return (descriptor.length >= VIDEO_CLASS_HEADER_LENGTH && (descriptor[bDescriptorSubType] == VC_INF_SUBTYPE.VC_HEADER.subtype));
    }

    public boolean isTerminal(byte[] descriptor) {
        return VideoTerminal.isVideoTerminal(descriptor);
    }

    public boolean isUnit(byte[] descriptor) {
        return VideoUnit.isVideoUnit(descriptor);
    }

    public void parseClassInterfaceHeader(byte[] descriptor) throws IllegalArgumentException {
        Log.d(TAG, "Parsing Video Class Interface header.");
        if (descriptor.length < VIDEO_CLASS_HEADER_LENGTH) throw new IllegalArgumentException("The provided descriptor is not a valid Video Class Interface.");
        mUVC = ((0xFF & descriptor[bcdUVC]) << 8) | (0xFF & descriptor[bcdUVC + 1]);
        mNumberStreamingInterfaces = descriptor[bInCollection];
        mStreamingInterfaces = new int[mNumberStreamingInterfaces];
        for (int i = 0; i < mNumberStreamingInterfaces; ++i) {
            mStreamingInterfaces[i] = (0xFF & descriptor[baInterfaceNr_1 + i]);
        }
    }

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
        } else if (VideoEncodingUnit.isVideoEncodingUnit(descriptor)) {
            // Parse as video encoding unit
            final VideoEncodingUnit encodingUnit = new VideoEncodingUnit(descriptor);
            Log.d(TAG, "" + encodingUnit);
        } else if (AVideoExtensionUnit.isVideoExtensionUnit(descriptor)) {
            // Parse as a video extension unit
            // TODO: Figure out how to handle extensions
        } else {
            throw new IllegalArgumentException("The provided descriptor is not a valid Video Unit");
        }
    }
}
