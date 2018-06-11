package com.jwoolston.android.uvc;

/**
 * @author Jared Woolston (Jared.Woolston@gmail.com)
 */
public final class StreamCreationException extends Exception {

    public StreamCreationException() {
        super("Failed to create the requested stream.");
    }
}
