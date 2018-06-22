package com.jwoolston.android.uvc.interfaces.endpoints;

import com.jwoolston.android.libusb.UsbInterface;

/**
 * @author Jared Woolston (Jared.Woolston@gmail.com)
 */
public class BulkEndpoint extends Endpoint {

    protected BulkEndpoint(UsbInterface usbInterface, byte[] descriptor) throws IllegalArgumentException {
        super(usbInterface, descriptor);
    }
}
