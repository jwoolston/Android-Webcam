package com.jwoolston.usb.webcam.interfaces;

import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbInterface;
import android.util.Log;

import com.jwoolston.usb.webcam.interfaces.endpoints.Endpoint;
import com.jwoolston.usb.webcam.util.Hexdump;

import static com.jwoolston.usb.webcam.interfaces.Descriptor.VIDEO_SUBCLASS;

/**
 * @author Jared Woolston (jwoolston@idealcorp.com)
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

    private final UsbInterface mUsbInterface;

    private final int mIndexInterface;

    private final Endpoint[] mEndpoints;

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
            if (descriptor[bInterfaceProtocol] != Descriptor.PROTOCOL.PC_PROTOCOL_15.protocol) {
                switch (VIDEO_SUBCLASS.getVIDEO_SUBCLASS(descriptor[bInterfaceSubClass])) {
                    // We could handle Interface Association Descriptors here, but they don't correspond to an accessable interface, so we
                    // treat them separately
                    case SC_VIDEOCONTROL:
                        Log.d(TAG, "Parsing VideoControlInterface.");
                        return VideoControlInterface.parseVideoControlInterface(device, descriptor);
                    case SC_VIDEOSTREAMING:
                        Log.d(TAG, "Parsing VideoStreamingInterface: " + Hexdump.dumpHexString(descriptor));
                        return null;
                        // return new VideoStreamingInterface(device, (int) descriptor[iInterface]);
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
        mUsbInterface = usbInterface;
        final int endpointCount = (0xFF & descriptor[bNumEndpoints]);
        mEndpoints = new Endpoint[endpointCount];
        mIndexInterface = (0xFF & descriptor[iInterface]);
    }

    public void addEndpoint(int index, Endpoint endpoint) {
        mEndpoints[index - 1] = endpoint;
    }

    public Endpoint getEndpoint(int index) {
        return mEndpoints[index - 1];
    }

    public int getInterfaceNumber() {
        return mUsbInterface.getId();
    }

    public UsbInterface getUsbInterface() {
        return mUsbInterface;
    }

    public int getIndexInterface() {
        return mIndexInterface;
    }

    @Override
    public String toString() {
        return "AInterface{" +
                "mUsbInterface=" + mUsbInterface +
                ", mIndexInterface=" + mIndexInterface +
                '}';
    }
}
