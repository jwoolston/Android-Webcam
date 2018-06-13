package com.jwoolston.android.uvc.interfaces.endpoints;

import android.hardware.usb.UsbInterface;

/**
 * @author Jared Woolston (Jared.Woolston@gmail.com)
 */
public class IsochronousEndpoint extends Endpoint {

    private final SynchronizationType synchronizationType;

    protected IsochronousEndpoint(UsbInterface usbInterface, byte[] descriptor) throws IllegalArgumentException {
        super(usbInterface, descriptor);
        synchronizationType = SynchronizationType.fromAttributes(getRawAttributes());
    }

    public SynchronizationType getSynchronizationType() {
        return synchronizationType;
    }

    public static enum SynchronizationType {
        NONE,
        ASYNCRONOUS,
        ADAPTIVE,
        SYNCHRONOUS;

        public static SynchronizationType fromAttributes(byte attributes) {
            int code = attributes & 0xC;
            switch (code) {
                case 0x0:
                    return NONE;
                case 0x4:
                    return ASYNCRONOUS;
                case 0x8:
                    return ADAPTIVE;
                case 0xC:
                    return SYNCHRONOUS;
            }
            return null;
        }
    }
}
