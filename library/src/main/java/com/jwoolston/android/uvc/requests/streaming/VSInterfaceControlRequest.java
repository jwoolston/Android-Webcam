package com.jwoolston.android.uvc.requests.streaming;

import android.support.annotation.NonNull;
import com.jwoolston.android.uvc.requests.Request;
import com.jwoolston.android.uvc.requests.VideoClassRequest;

/**
 * These requests are used to set or read an attribute of an interface Control inside the VideoStreaming interface of
 * the video function.
 *
 * The bRequest field indicates which attribute the request is manipulating. The MIN, MAX, and RES attributes are not
 * supported for the Set request. The wValue field specifies the Control Selector (CS) in the high byte, and the low
 * byte must be set to zero. The Control Selector indicates which type of Control this request is manipulating. If
 * the request specifies an unknown S to that endpoint, the control pipe must indicate a stall.
 *
 * @author Jared Woolston (Jared.Woolston@gmail.com)
 * @see <a href=http://www.usb.org/developers/docs/devclass_docs/USB_Video_Class_1_5.zip>UVC 1.5 Class
 * Specification §4.2.1</a>
 */
public abstract class VSInterfaceControlRequest extends VideoClassRequest {

    private static short valueFromControlSelector(@NonNull ControlSelector selector) {
        return ((short) (0xFFFF & (selector.code << 8)));
    }

    protected VSInterfaceControlRequest(@NonNull Request request, ControlSelector selector, short index,
                                        @NonNull byte[] data) {
        super(request == Request.SET_CUR ? SET_REQUEST_INF_ENTITY : GET_REQUEST_INF_ENTITY,
              request, valueFromControlSelector(selector), index, data);
    }

    public static enum ControlSelector {
        VS_CONTROL_UNDEFINED((byte) 0x00),
        VS_PROBE_CONTROL((byte) 0x01),
        VS_COMMIT_CONTROL((byte) 0x02),
        VS_STILL_PROBE_CONTROL((byte) 0x03),
        VS_STILL_COMMIT_CONTROL((byte) 0x04),
        VS_STILL_IMAGE_TRIGGER_CONTROL((byte) 0x05),
        VS_STREAM_ERROR_CODE_CONTROL((byte) 0x06),
        VS_GENERATE_KEY_FRAME_CONTROL((byte) 0x07),
        VS_UPDATE_FRAME_SEGMENT_CONTROL((byte) 0x08),
        VS_SYNC_DELAY_CONTROL((byte) 0x09);

        final byte code;

        ControlSelector(byte code) {
            this.code = code;
        }
    }
}
