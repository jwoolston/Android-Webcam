package com.jwoolston.android.uvc.requests;

import android.support.annotation.NonNull;

import com.jwoolston.android.uvc.interfaces.VideoClassInterface;
import com.jwoolston.android.uvc.interfaces.terminals.VideoTerminal;

import java.util.Locale;

/**
 * @author Jared Woolston (Jared.Woolston@gmail.com)
 * @see <a href=http://www.usb.org/developers/docs/devclass_docs/USB_Video_Class_1_5.zip>UVC 1.5 Class
 * Specification §4</a>
 */
public abstract class VideoClassRequest {

    protected static final byte SET_REQUEST_INF_ENTITY = 0x21; // Set Request type targeting entity or interface
    protected static final byte SET_REQUEST_ENDPOINT   = 0x22; // Set Request type targeting endpoint
    protected static final byte GET_REQUEST_INF_ENTITY = (byte) 0xA1; // Get Request type targeting entity or interface
    protected static final byte GET_REQUEST_ENDPOINT   = (byte) 0xA2; // Get Request type targeting endpoint

    private final byte    requestType;
    private final Request request;

    private short wValue;
    private short wIndex;
    private byte[] data;

    protected static short getIndex(VideoTerminal terminal, VideoClassInterface classInterface) {
        return (short) (((0xFF & terminal.getTerminalID()) << 8) | (0xFF & classInterface.getUsbInterface().getId()));
    }

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

    @Override public String toString() {
        final StringBuffer sb = new StringBuffer(getClass().getSimpleName());
        sb.append("{");
        sb.append("requestType=0x").append(Integer.toHexString(0xFF & requestType).toUpperCase(Locale.US));
        sb.append(", request=").append(request);
        sb.append(", wValue=0x").append(Integer.toHexString(wValue).toUpperCase(Locale.US));
        sb.append(", wIndex=").append(wIndex);
        sb.append(", data=");
        if (data == null) {
            sb.append("null");
        } else {
            sb.append('[');
            for (int i = 0; i < data.length; ++i) {
                sb.append(i == 0 ? "" : ", ").append(data[i]);
            }
            sb.append(']');
        }
        sb.append('}');
        return sb.toString();
    }
}
