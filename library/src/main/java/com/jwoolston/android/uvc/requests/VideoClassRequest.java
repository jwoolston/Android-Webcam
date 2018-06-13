package com.jwoolston.android.uvc.requests;

import android.support.annotation.NonNull;

/**
 * @author Jared Woolston (Jared.Woolston@gmail.com)
 * @see <a href=http://www.usb.org/developers/docs/devclass_docs/USB_Video_Class_1_5.zip>UVC 1.5 Class
 * Specification §4</a>
 */
public abstract class VideoClassRequest {

    protected static final byte VC_SET_REQUEST_TYPE = 0x21; // Set Request type targeting VideoControl Interface
    protected static final byte VS_SET_REQUEST_TYPE = 0x22; // Set Request type targeting VideoStreaming Interface
    protected static final byte VC_GET_REQUEST_TYPE = (byte) 0xA1; // Get Request type targeting VideoControl Interface
    protected static final byte VS_GET_REQUEST_TYPE = (byte) 0xA2; // Get Request type targeting VideoStreaming
                                                             // Interface

    private final byte    requestType;
    private final Request request;

    private short wValue;
    private short wIndex;
    private byte[] data;

    protected VideoClassRequest(byte requestType, @NonNull Request request, short value, short index,
                                @NonNull byte[] data) {
        this.requestType = requestType;
        this.request = request;
        wValue = value;
        wIndex = index;
        this.data = data;
    }

    public byte getRequestType() {
        return requestType;
    }

    public byte getRequest() {
        return request.code;
    }

    public short getValue() {
        return wValue;
    }

    public short getIndex() {
        return wIndex;
    }

    public byte[] getData() {
        return data;
    }

    public int getLength() {
        return data.length;
    }
}
