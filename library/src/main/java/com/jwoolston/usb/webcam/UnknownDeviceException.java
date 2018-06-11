package com.jwoolston.usb.webcam;

public final class UnknownDeviceException extends Exception {

    public UnknownDeviceException() {
        super("Connected device is not a generic webcam.");
    }
}
