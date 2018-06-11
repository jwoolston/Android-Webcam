package com.jwoolston.android.uvc;

public final class StreamCreationException extends Exception {

    public StreamCreationException() {
        super("Failed to create the requested stream.");
    }
}
