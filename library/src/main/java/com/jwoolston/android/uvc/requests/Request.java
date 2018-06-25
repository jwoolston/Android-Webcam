package com.jwoolston.android.uvc.requests;

/**
 * @author Jared Woolston (Jared.Woolston@gmail.com)
 * @see <a href=http://www.usb.org/developers/docs/devclass_docs/USB_Video_Class_1_5.zip>UVC 1.5 Class
 * Specification §A.8</a>
 */
public enum Request {

    RC_UNDEFINED((byte) 0x00),
    SET_CUR((byte) 0x01),
    SET_CUR_ALL((byte) 0x11),
    GET_CUR((byte) 0x81),
    GET_MIN((byte) 0x82),
    GET_MAX((byte) 0x83),
    GET_RES((byte) 0x84),
    GET_LEN((byte) 0x85),
    GET_INFO((byte) 0x86),
    GET_DEF((byte) 0x87),
    GET_CUR_ALL((byte) 0x91),
    GET_MIN_ALL((byte) 0x92),
    GET_MAX_ALL((byte) 0x93),
    GET_RES_ALL((byte) 0x94),
    GET_DEF_ALL((byte) 0x97);

    public final byte code;

    Request(byte code) {
        this.code = code;
    }

    @Override
    public String toString() {
        return (name() + "(0x" + Integer.toHexString(0xFF & code) + ')');
    }
}
