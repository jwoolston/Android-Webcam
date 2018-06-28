package com.jwoolston.android.uvc.streaming;

/**
 * @author Jared Woolston (Jared.Woolston@gmail.com)
 */
public final class FieldNotPresentException extends Exception {

    public FieldNotPresentException() {
        super("The requested field is not present.");
    }

    public FieldNotPresentException(String message) {
        super(message);
    }
}
