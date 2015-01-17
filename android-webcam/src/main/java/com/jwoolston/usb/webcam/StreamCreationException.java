package com.jwoolston.usb.webcam;

public final class StreamCreationException extends Exception {

    public StreamCreationException() {
        super("Failed to create the requested stream.");
    }
}
