package com.jwoolston.android.uvc.interfaces;

import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbInterface;
import android.util.Log;
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
import java.util.LinkedList;
import java.util.List;

/**
 * @author Jared Woolston (Jared.Woolston@gmail.com)
 */
public class VideoControlInterface extends AVideoClassInterface {

    private static final String TAG = "VideoControlInterface";

    private static final int VIDEO_CLASS_HEADER_LENGTH = 12;
    private static final int INTERRUPT_ENDPOINT = 0x3;

    private static final int bDescriptorSubType = 2;
    private static final int bcdUVC           = 3;
    private static final int wTotalLength     = 5;
    private static final int dwClockFrequency = 7;
    private static final int bInCollection    = 11;
    private static final int baInterfaceNr_1  = 12;

    private int   uvc;
    private int   numberStreamingInterfaces;
    private int[] streamingInterfaces;

    private List<VideoInputTerminal> inputTerminals = new LinkedList<>();
    private List<VideoOutputTerminal> outputTerminals = new LinkedList<>();
    private List<VideoUnit> units = new LinkedList<>();

    public static VideoControlInterface parseVideoControlInterface(UsbDevice device, byte[] descriptor) throws IllegalArgumentException {
        Log.d(TAG, "Parsing Video Class Interface header.");

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
        StringBuilder builder = new StringBuilder("\tVideoControlInterface{" +
               "\n\t\t\tuvc=" + uvc +
               "\n\t\t\tnumberStreamingInterfaces=" + numberStreamingInterfaces +
               "\n\t\t\tstreamingInterfaces=" + Arrays.toString(streamingInterfaces) +
               "\n\t\t\tUSB Interface=" + getUsbInterface() +
               "\n\t\t\tEndpoints=" + Arrays.toString(getCurrentEndpoints()) +
               "\n\t\t\tinput terminals=" + inputTerminals +
               "\n\t\t\toutput terminals=" + outputTerminals);
        builder.append("\n\t\t\tVideo Units:");
        for (VideoUnit unit : units) {
            builder.append("\n\t\t\t\t").append(unit);
        }
        builder.append('}');
        return builder.toString();
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
        if (descriptor.length < VIDEO_CLASS_HEADER_LENGTH) throw new IllegalArgumentException("The provided descriptor is not a valid Video Class Interface.");
        uvc = ((0xFF & descriptor[bcdUVC]) << 8) | (0xFF & descriptor[bcdUVC + 1]);
        numberStreamingInterfaces = descriptor[bInCollection];
        streamingInterfaces = new int[numberStreamingInterfaces];
        for (int i = 0; i < numberStreamingInterfaces; ++i) {
            streamingInterfaces[i] = (0xFF & descriptor[baInterfaceNr_1 + i]);
        }
    }

    public void parseTerminal(byte[] descriptor) throws IllegalArgumentException {
        if (VideoInputTerminal.isInputTerminal(descriptor)) {
            if (CameraTerminal.isCameraTerminal(descriptor)) {
                // Parse as camera terminal
                final CameraTerminal cameraTerminal = new CameraTerminal(descriptor);
                inputTerminals.add(cameraTerminal);
            } else {
                // Parse as input terminal
                VideoInputTerminal inputTerminal = new VideoInputTerminal(descriptor);
                inputTerminals.add(inputTerminal);
            }
        } else if (VideoOutputTerminal.isOutputTerminal(descriptor)) {
            // Parse as output terminal
            VideoOutputTerminal outputTerminal = new VideoOutputTerminal(descriptor);
            outputTerminals.add(outputTerminal);
        } else {
            throw new IllegalArgumentException("The provided descriptor is not a valid Video Terminal.");
        }
    }

    public void parseUnit(byte[] descriptor) throws IllegalArgumentException {
        if (VideoSelectorUnit.isVideoSelectorUnit(descriptor)) {
            // Parse as video selector unit
            final VideoSelectorUnit selectorUnit = new VideoSelectorUnit(descriptor);
            units.add(selectorUnit);
        } else if (VideoProcessingUnit.isVideoProcessingUnit(descriptor)) {
            // Parse as video processing unit
            final VideoProcessingUnit processingUnit = new VideoProcessingUnit(descriptor);
            units.add(processingUnit);
        } else if (VideoEncodingUnit.isVideoEncodingUnit(descriptor)) {
            // Parse as video encoding unit
            final VideoEncodingUnit encodingUnit = new VideoEncodingUnit(descriptor);
            units.add(encodingUnit);
        } else if (AVideoExtensionUnit.isVideoExtensionUnit(descriptor)) {
            // Parse as a video extension unit
            Log.d(TAG, "Parsing video extension unit.");
            // TODO: Figure out how to handle extensions
        } else {
            throw new IllegalArgumentException("The provided descriptor is not a valid Video Unit");
        }
    }
}
