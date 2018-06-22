package com.jwoolston.android.uvc.interfaces.endpoints;

import android.hardware.usb.UsbEndpoint;
import android.hardware.usb.UsbInterface;

import timber.log.Timber;

/**
 * @author Jared Woolston (Jared.Woolston@gmail.com)
 */
public abstract class Endpoint {

    private static final int LENGTH_STANDARD_DESCRIPTOR = 7;
    private static final int LENGTH_CLASS_DESCRIPTOR = 5;

    private static final int bLength = 0;
    private static final int bDescriptorType = 1;
    private static final int bEndpointAddress = 2;
    private static final int bmAttributes = 3;
    private static final int wMaxPacketSize = 4;
    private static final int bInterval = 6; // Interval is 2^(value-1) ms

    private UsbEndpoint endpoint; // Often this will be null after initial parsing, the endpoint wont enumerate until
    // we activate the alternate setting.

    private final VideoEndpoint type;
    private final byte rawAttributes;
    private final int endpointAddress;
    private final int interval; // USB Frames

    private int maxPacketSize;

    public static Endpoint parseDescriptor(UsbInterface usbInterface, byte[] descriptor) throws IllegalArgumentException {
        if (descriptor.length < LENGTH_STANDARD_DESCRIPTOR) {
            throw new IllegalArgumentException("Descriptor is not long enough to be a standard endpoint descriptor.");
        }
        VideoEndpoint type = VideoEndpoint.fromAttributes(descriptor[bmAttributes]);
        switch (type) {
            case EP_ISOCHRONOUS:
                return new IsochronousEndpoint(usbInterface, descriptor);
            case EP_BULK:
                return new BulkEndpoint(usbInterface, descriptor);
            case EP_INTERRUPT:
                return new InterruptEndpoint(usbInterface, descriptor);
            default:
                throw new IllegalArgumentException("Descriptor is not for a recognized endpoint type.");
        }
    }

    protected Endpoint(UsbInterface usbInterface, byte[] descriptor) throws IllegalArgumentException {
        if (descriptor.length < LENGTH_STANDARD_DESCRIPTOR) {
            throw new IllegalArgumentException("The provided descriptor is not a valid standard endpoint descriptor.");
        }

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
        interval = descriptor[bInterval];
        maxPacketSize = ((0xFF & descriptor[wMaxPacketSize]) << 8) | (0xFF & descriptor[wMaxPacketSize + 1]);
        type = VideoEndpoint.fromAttributes(rawAttributes);
    }

    public void parseClassDescriptor(byte[] descriptor) throws IllegalArgumentException {
        Timber.d("Parsing Class Specific Endpoint Descriptor.");
        if (descriptor.length < LENGTH_CLASS_DESCRIPTOR) {
            throw new IllegalArgumentException("The provided descriptor is not a valid class endpoint descriptor.");
        }
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
        return "Endpoint{" +
            "endpoint=" + endpoint +
            ", type=" + type +
            ", rawAttributes=" + rawAttributes +
            ", endpointAddress=" + endpointAddress +
            ", interval=" + interval +
            ", maxPacketSize=" + maxPacketSize +
            '}';
    }

    public static enum VideoEndpoint {
        EP_CONTROL(0x0),
        EP_ISOCHRONOUS(0x01),
        EP_BULK(0x02),
        EP_INTERRUPT(0x03);

        public final byte code;

        private VideoEndpoint(int code) {
            this.code = (byte) (code & 0xFF);
        }

        public static VideoEndpoint fromAttributes(byte attributes) {
            int typeCode = attributes & 0x3;
            switch (typeCode) {
                case 0x0:
                    return EP_CONTROL;
                case 0x01:
                    return EP_ISOCHRONOUS;
                case 0x02:
                    return EP_BULK;
                case 0x03:
                    return EP_INTERRUPT;
            }
            return null;
        }
    }
}
