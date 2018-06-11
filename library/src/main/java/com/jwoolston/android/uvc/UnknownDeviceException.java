package com.jwoolston.android.uvc;

public final class UnknownDeviceException extends Exception {

    public UnknownDeviceException() {
        super("Connected device is not a generic webcam.");
    }
}
