package com.jwoolston.usb.webcam.interfaces;

import android.hardware.usb.UsbInterface;

/**
 * @author Jared Woolston (jwoolston@idealcorp.com)
 */
public class VideoStreamingInterface extends AVideoClassInterface {

    private static final int MIN_HEADER_LENGTH = 13;

    private static final int bLength = 0;
    private static final int bDescriptorType = 1;
    private static final int bDescriptorSubtype = 2;
    private static final int bNumFormats = 3; //p
    private static final int wTotalLength = 4;
    private static final int bEndpointAddress = 6;
    private static final int bmInfo = 7;
    private static final int bTerminalLink = 8;
    private static final int bStillCaptureMethod = 9;
    private static final int bTriggerSupport = 10;
    private static final int bTriggerUsage = 11;
    private static final int bControlSize = 12; //n
    private static final int bmaControls = 13; // Last index is 13 + p*n - n

    VideoStreamingInterface(UsbInterface usbInterface, byte[] descriptor) {
        super(usbInterface, descriptor);
    }
}
