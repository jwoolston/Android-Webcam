package com.jwoolston.android.uvc.requests.control;

import android.support.annotation.NonNull;

/**
 * These requests are used to set or read an attribute of an interface Control inside the VideoControl interface of
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
public abstract class VCInterfaceControlRequest extends VideoClassRequest {

    private static short valueFromControlSelector(@NonNull ControlSelector selector) {
        return ((short) (0xFFFF & (selector.code << 8)));
    }

    protected VCInterfaceControlRequest(@NonNull Request request, ControlSelector selector, short index,
                                        @NonNull byte[] data) {
        super(request == Request.SET_CUR ? SET_REQUEST_INF_ENTITY : GET_REQUEST_INF_ENTITY,
              request, valueFromControlSelector(selector), index, data);
    }

    public static enum ControlSelector {
        VC_CONTROL_UNDEFINED((byte) 0x00),
        VC_VIDEO_POWER_MODE_CONTROL((byte) 0x01),
        VC_REQUEST_ERROR_CODE_CONTROL((byte) 0x02),
        RESERVED((byte) 0x03);

        final byte code;

        ControlSelector(byte code) {
            this.code = code;
        }
    }
}
