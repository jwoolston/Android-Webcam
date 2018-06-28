package com.jwoolston.android.uvc.streaming;

/**
 * @author Jared Woolston (Jared.Woolston@gmail.com)
 */
public final class StreamCreationException extends Exception {

    public StreamCreationException() {
        super("Failed to create the requested stream.");
    }

    public StreamCreationException(String message) {
        super(message);
    }

    public StreamCreationException(Throwable throwable) {
        super("Failed to create the requested stream.", throwable);
    }
}
