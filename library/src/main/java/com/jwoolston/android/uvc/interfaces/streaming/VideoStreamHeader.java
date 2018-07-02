package com.jwoolston.android.uvc.interfaces.streaming;

/**
 * @author Jared Woolston (Jared.Woolston@gmail.com)
 */
abstract class VideoStreamHeader {

    protected static final int bNumFormats      = 3; //p
    protected static final int wTotalLength     = 4;
    protected static final int bEndpointAddress = 6;

    private final int numberFormats;
    private final int endpointAddress;

    protected VideoStreamHeader(byte[] descriptor) throws IllegalArgumentException {
        numberFormats = (0xFF & descriptor[bNumFormats]);
        endpointAddress = (0xFF & descriptor[bEndpointAddress]);
    }

    int getNumberFormats() {
        return numberFormats;
    }

    int getEndpointAddress() {
        return endpointAddress;
    }
}
