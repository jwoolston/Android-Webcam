package com.jwoolston.android.uvc.interfaces.streaming;

/**
 * @author Jared Woolston (jwoolston@idealcorp.com)
 */
abstract class AVideoStreamHeader {

    protected static final int bNumFormats      = 3; //p
    protected static final int wTotalLength     = 4;
    protected static final int bEndpointAddress = 6;

    private final int numberFormats;
    private final int endpointAddress;

    protected AVideoStreamHeader(byte[] descriptor) throws IllegalArgumentException {
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
