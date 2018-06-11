package com.jwoolston.android.uvc.interfaces;

import static com.jwoolston.android.uvc.interfaces.Descriptor.VideoSubclass;

import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbInterface;
import android.util.Log;
import android.util.SparseArray;
import com.jwoolston.android.uvc.interfaces.Descriptor.Protocol;
import com.jwoolston.android.uvc.interfaces.endpoints.Endpoint;
import com.jwoolston.android.uvc.util.Hexdump;

/**
 * @author Jared Woolston (Jared.Woolston@gmail.com)
 */
public abstract class AInterface {

    private static final String TAG = "AInterface";

    private static final int LENGTH_STANDARD_DESCRIPTOR = 9;

    protected static final int bLength            = 0;
    protected static final int bDescriptorType    = 1;
    protected static final int bInterfaceNumber   = 2;
    protected static final int bAlternateSetting  = 3;
    protected static final int bNumEndpoints      = 4;
    protected static final int bInterfaceClass    = 5;
    protected static final int bInterfaceSubClass = 6;
    protected static final int bInterfaceProtocol = 7;
    protected static final int iInterface         = 8;

    private final UsbInterface usbInterface;

    private final int indexInterface;

    protected final SparseArray<Endpoint[]> endpoints;

    protected int currentSetting = 0;

    protected static UsbInterface getUsbInterface(UsbDevice device, byte[] descriptor) {
        final int index = (0xFF & descriptor[bInterfaceNumber]);
        return device.getInterface(index);
    }

    public static AInterface parseDescriptor(UsbDevice device, byte[] descriptor) throws IllegalArgumentException {
        // Check the length
        if (descriptor.length < LENGTH_STANDARD_DESCRIPTOR) throw new IllegalArgumentException("Descriptor is not long enough to be a standard interface descriptor.");
        // Check the class
        if (descriptor[bInterfaceClass] == Descriptor.VIDEO_CLASS_CODE) {
            // For video class, only PC_PROTOCOL_15 is permitted
            if (descriptor[bInterfaceProtocol] != Protocol.PC_PROTOCOL_15.protocol) {
                switch (VideoSubclass.getVideoSubclass(descriptor[bInterfaceSubClass])) {
                    // We could handle Interface Association Descriptors here, but they don't correspond to an accessable interface, so we
                    // treat them separately
                    case SC_VIDEOCONTROL:
                        Log.d(TAG, "Parsing VideoControlInterface.");
                        return VideoControlInterface.parseVideoControlInterface(device, descriptor);
                    case SC_VIDEOSTREAMING:
                        Log.d(TAG, "Parsing VideoStreamingInterface: " + Hexdump.dumpHexString(descriptor));
                        return VideoStreamingInterface.parseVideoStreamingInterface(device, descriptor);
                    default:
                        throw new IllegalArgumentException("The provided descriptor has an invalid video interface subclass.");
                }
            } else {
                throw new IllegalArgumentException("The provided descriptor has an invalid protocol: " + descriptor[bInterfaceProtocol]);
            }
        } else if (descriptor[bInterfaceClass] == Descriptor.AUDIO_CLASS_CODE) {
            // TODO: Something with the audio class
            return null;
        } else {
            throw new IllegalArgumentException("The provided descriptor has an invalid interface class: " + descriptor[bInterfaceClass]);
        }
    }

    protected AInterface(UsbInterface usbInterface, byte[] descriptor) {
        this.usbInterface = usbInterface;
        final int endpointCount = (0xFF & descriptor[bNumEndpoints]);
        endpoints = new SparseArray<>();
        currentSetting = 0xFF & descriptor[bAlternateSetting];
        endpoints.put(currentSetting, new Endpoint[endpointCount]);
        indexInterface = (0xFF & descriptor[iInterface]);
    }

    public void addEndpoint(int index, Endpoint endpoint) {
        endpoints.get(currentSetting)[index - 1] = endpoint;
    }

    public Endpoint getEndpoint(int index) {
        return endpoints.get(currentSetting)[index - 1];
    }

    public int getInterfaceNumber() {
        return usbInterface.getId();
    }

    public UsbInterface getUsbInterface() {
        return usbInterface;
    }

    public int getIndexInterface() {
        return indexInterface;
    }

    public abstract void parseClassDescriptor(byte[] descriptor);

    public abstract void parseAlternateFunction(byte[] descriptor);

    @Override
    public String toString() {
        return "AInterface{" +
               "usbInterface=" + usbInterface +
               ", indexInterface=" + indexInterface +
               '}';
    }
}
