package com.jwoolston.android.uvc.interfaces.endpoints;

import com.jwoolston.android.libusb.UsbInterface;

/**
 * @author Jared Woolston (Jared.Woolston@gmail.com)
 */
public class InterruptEndpoint extends Endpoint {

    protected InterruptEndpoint(UsbInterface usbInterface, byte[] descriptor) throws IllegalArgumentException {
        super(usbInterface, descriptor);
    }
}
