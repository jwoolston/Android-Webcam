package com.jwoolston.android.uvc.interfaces.endpoints;

import android.hardware.usb.UsbEndpoint;
import android.hardware.usb.UsbInterface;
import android.util.Log;

/**
 * @author Jared Woolston (Jared.Woolston@gmail.com)
 */
public class InterruptEndpoint {

    private static final String TAG = "InterruptEndpoint";

    private static final int LENGTH_STANDARD_DESCRIPTOR = 7;
    private static final int LENGTH_CLASS_DESCRIPTOR = 5;

    private static final int bLength = 0;
    private static final int bDescriptorType = 1;
    private static final int bEndpointAddress = 2;
    private static final int bmAttributes = 3;
    private static final int wMaxPacketSize = 4;
    private static final int bInterval = 6; // Interval is 2^(value-1) ms

    private static final int bDescriptorSubType = 2;
    private static final int wMaxTransferSize = 3;

    private UsbEndpoint endpoint; // This would be final but intelli-sense doesn't like it not being initialized

    private final byte rawAttributes;
    private final int  endpointAddress;
    private final int  interval; // ms

    private int maxTransferSize;

    public static InterruptEndpoint parseDescriptor(UsbInterface usbInterface, byte[] descriptor) throws IllegalArgumentException {
        Log.d(TAG, "Parsing standard endpoint.");
        if (descriptor.length < LENGTH_STANDARD_DESCRIPTOR) throw new IllegalArgumentException("Descriptor is not long enough to be a standard endpoint descriptor.");
        return new InterruptEndpoint(usbInterface, descriptor);
    }

    protected InterruptEndpoint(UsbInterface usbInterface, byte[] descriptor) throws IllegalArgumentException {
        if (descriptor.length < LENGTH_STANDARD_DESCRIPTOR) throw new IllegalArgumentException("The provided descriptor is not a valid standard endpoint descriptor.");

        endpointAddress = 0xFF & descriptor[bEndpointAddress]; // Masking to deal with Java's signed bytes
        final int count = usbInterface.getEndpointCount();
        for (int i = 0; i < count; ++i) {
            final UsbEndpoint endpoint = usbInterface.getEndpoint(i);
            if (endpoint.getAddress() == endpointAddress) {
                this.endpoint = endpoint;
                break;
            }
        }

        rawAttributes = descriptor[bmAttributes];
        interval = descriptor[bInterval] * 2; // Interval is 2^(value-1) ms
    }

    public void parseClassDescriptor(byte[] descriptor) throws IllegalArgumentException {
        Log.d(TAG, "Parsing Class Specific InterruptEndpoint Descriptor.");
        if (descriptor.length < LENGTH_CLASS_DESCRIPTOR || descriptor[bDescriptorSubType] != VideoEndpoint.EP_INTERRUPT.code) {
            throw new IllegalArgumentException("The provided descriptor is not a valid class endpoint descriptor.");
        }
        maxTransferSize = descriptor[wMaxTransferSize] | (descriptor[wMaxTransferSize + 1] << 8);
    }

    public int getInterval() {
        return interval;
    }

    public UsbEndpoint getEndpoint() {
        return endpoint;
    }

    protected byte getRawAttributes() {
        return rawAttributes;
    }

    @Override
    public String toString() {
        return "InterruptEndpoint{" +
               "endpoint=" + endpoint +
               '}';
    }

    public static enum VideoEndpoint {
        EP_UNDEFINED(0x00),
        EP_GENERAL(0x01),
        EP_ENDPOINT(0x02),
        EP_INTERRUPT(0x03);

        public final byte code;

        private VideoEndpoint(int code) {
            this.code = (byte) (code & 0xFF);
        }

    }
}
