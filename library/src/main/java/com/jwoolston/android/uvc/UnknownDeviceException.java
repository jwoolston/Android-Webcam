package com.jwoolston.android.uvc;

/**
 * @author Jared Woolston (Jared.Woolston@gmail.com)
 */
public final class UnknownDeviceException extends Exception {

    public UnknownDeviceException() {
        super("Connected device is not a generic webcam.");
    }
}
