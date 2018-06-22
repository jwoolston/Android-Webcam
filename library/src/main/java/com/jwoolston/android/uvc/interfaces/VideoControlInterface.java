package com.jwoolston.android.uvc.interfaces;

import com.jwoolston.android.libusb.UsbDevice;
import com.jwoolston.android.libusb.UsbInterface;
import com.jwoolston.android.uvc.interfaces.terminals.CameraTerminal;
import com.jwoolston.android.uvc.interfaces.terminals.VideoInputTerminal;
import com.jwoolston.android.uvc.interfaces.terminals.VideoOutputTerminal;
import com.jwoolston.android.uvc.interfaces.terminals.VideoTerminal;
import com.jwoolston.android.uvc.interfaces.units.AVideoExtensionUnit;
import com.jwoolston.android.uvc.interfaces.units.VideoEncodingUnit;
import com.jwoolston.android.uvc.interfaces.units.VideoProcessingUnit;
import com.jwoolston.android.uvc.interfaces.units.VideoSelectorUnit;
import com.jwoolston.android.uvc.interfaces.units.VideoUnit;
import java.util.Arrays;

import timber.log.Timber;

/**
 * @author Jared Woolston (Jared.Woolston@gmail.com)
 */
public class VideoControlInterface extends AVideoClassInterface {

    private static final int VIDEO_CLASS_HEADER_LENGTH = 12;

    private static final int bDescriptorSubType = 2;
    private static final int bcdUVC           = 3;
    private static final int wTotalLength     = 5;
    private static final int dwClockFrequency = 7;
    private static final int bInCollection    = 11;
    private static final int baInterfaceNr_1  = 12;

    private int   uvc;
    private int   numberStreamingInterfaces;
    private int[] streamingInterfaces;

    public static VideoControlInterface parseVideoControlInterface(UsbDevice device, byte[] descriptor) throws IllegalArgumentException {
        Timber.d("Parsing Video Class Interface header.");

        final UsbInterface usbInterface = AInterface.getUsbInterface(device, descriptor);
        return new VideoControlInterface(usbInterface, descriptor);
    }

    VideoControlInterface(UsbInterface usbInterface, byte[] descriptor) {
        super(usbInterface, descriptor);
    }

    @Override
    public void parseClassDescriptor(byte[] descriptor) {
        if (isClassInterfaceHeader(descriptor)) {
            parseClassInterfaceHeader(descriptor);
            Timber.d("%s", this);
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
        Timber.d("parseAlternateFunction() called for VideoControlInterface.");
    }

    @Override
    public String toString() {
        return "VideoControlInterface{" +
               "uvc=" + uvc +
               ", numberStreamingInterfaces=" + numberStreamingInterfaces +
               ", streamingInterfaces=" + Arrays.toString(streamingInterfaces) +
               ", USB Interface=" + getUsbInterface() +
               '}';
    }

    public int getNumberStreamingInterfaces() {
        return numberStreamingInterfaces;
    }

    public int getUVCVersion() {
        return uvc;
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
        Timber.d("Parsing Video Class Interface header.");
        if (descriptor.length < VIDEO_CLASS_HEADER_LENGTH) throw new IllegalArgumentException("The provided descriptor is not a valid Video Class Interface.");
        uvc = ((0xFF & descriptor[bcdUVC]) << 8) | (0xFF & descriptor[bcdUVC + 1]);
        numberStreamingInterfaces = descriptor[bInCollection];
        streamingInterfaces = new int[numberStreamingInterfaces];
        for (int i = 0; i < numberStreamingInterfaces; ++i) {
            streamingInterfaces[i] = (0xFF & descriptor[baInterfaceNr_1 + i]);
        }
    }

    public void parseTerminal(byte[] descriptor) throws IllegalArgumentException {
        Timber.d("Parsing Video Class Interface Terminal.");
        if (VideoInputTerminal.isInputTerminal(descriptor)) {
            if (CameraTerminal.isCameraTerminal(descriptor)) {
                // Parse as camera terminal
                final CameraTerminal cameraTerminal = new CameraTerminal(descriptor);
                Timber.d("%s", cameraTerminal);
            } else {
                // Parse as input terminal
                VideoInputTerminal inputTerminal = new VideoInputTerminal(descriptor);
                Timber.d("%s", inputTerminal);
            }
        } else if (VideoOutputTerminal.isOutputTerminal(descriptor)) {
            // Parse as output terminal
            VideoOutputTerminal outputTerminal = new VideoOutputTerminal(descriptor);
            Timber.d("%s", outputTerminal);
        } else {
            throw new IllegalArgumentException("The provided descriptor is not a valid Video Terminal.");
        }
    }

    public void parseUnit(byte[] descriptor) throws IllegalArgumentException {
        Timber.d("Parsing Video Class Interface Unit.");
        if (VideoSelectorUnit.isVideoSelectorUnit(descriptor)) {
            // Parse as video selector unit
            final VideoSelectorUnit selectorUnit = new VideoSelectorUnit(descriptor);
            Timber.d("%s", selectorUnit);
        } else if (VideoProcessingUnit.isVideoProcessingUnit(descriptor)) {
            // Parse as video processing unit
            final VideoProcessingUnit processingUnit = new VideoProcessingUnit(descriptor);
            Timber.d("%s", processingUnit);
        } else if (VideoEncodingUnit.isVideoEncodingUnit(descriptor)) {
            // Parse as video encoding unit
            final VideoEncodingUnit encodingUnit = new VideoEncodingUnit(descriptor);
            Timber.d("%s", encodingUnit);
        } else if (AVideoExtensionUnit.isVideoExtensionUnit(descriptor)) {
            // Parse as a video extension unit
            // TODO: Figure out how to handle extensions
        } else {
            throw new IllegalArgumentException("The provided descriptor is not a valid Video Unit");
        }
    }
}
