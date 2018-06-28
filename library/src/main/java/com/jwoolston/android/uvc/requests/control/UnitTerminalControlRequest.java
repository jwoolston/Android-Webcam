package com.jwoolston.android.uvc.requests.control;

import android.support.annotation.NonNull;

import com.jwoolston.android.uvc.requests.Request;
import com.jwoolston.android.uvc.requests.VideoClassRequest;

/**
 * These used to set or read an attribute of a Control inside of a Unit or Terminal of the video function.
 * <p>
 * The bRequest field indicates which attribute the request is manipulating. The MIN, MAX and RES attributes are not
 * supported for the Set request. The wValue field specifies the Control Selector (CS) in the high byte, and zero in the
 * low byte. The Control Selector indicates which type of Control this request is manipulating. When processing all
 * Controls as part of a batch request (GET_###_ALL), wValue is not needed and must be set to 0. If the request
 * specifies an unknown or unsupported CS to that Unit or Terminal, the control pipe must indicate a protocol STALL.
 * The value of wLength must be calculated as follows. Use wIndex to determine the Unit or Terminal of interest. For
 * that Unit or Terminal, establish which Controls are supported using the bmControls field of the associated Unit or
 * Terminal Descriptor. wLength is the sum of the length of all supported Controls for the target Unit or Terminal. The
 * Data must be ordered according to the order of the Controls listed in the bmControls field of the target Unit or
 * Terminal descriptor. If the Unit or Terminal supports batch requests, then each Control in the Unit or Terminal must
 * contribute to the Data field, even if it does not support the associated single operation request.
 * <p>
 * If a Control supports GET_MIN, GET_MAX and GET_RES requests, the values of MAX, MIN and RES shall be constrained such
 * that (MAX-MIN)/RES is an integral number. Furthermore, the CUR value (returned by GET_CUR, or set via SET_CUR) shall
 * be constrained such that (CUR-MIN)/RES is an integral number. The device shall indicate protocol STALL and update the
 * Request Error Code Control with 0x04 “Out of Range” if an invalid CUR value is provided in a SET_CUR operation (see
 * section 2.4.4, “Control Transfer and Request Processing”).
 * <p>
 * There are special Terminal types (such as the Camera Terminal and Media Transport Terminal) that have type-specific
 * Terminal Controls defined. The controls for the Media Transport Terminal are defined in a companion specification
 * (see the USB Device Class Definition for Video Media Transport Terminal specification). The controls for the Camera
 * Terminal are defined in the following sections.
 * <p>
 * As this specification evolves, new controls in the Camera Terminal, Processing Unit, and Encoding Unit are added to
 * the list of associated Control Selectors at the end (Tables A-12 through A-14). However, in the sections below, the
 * description of the functionality is placed next to controls with associated functionality.
 *
 * @author Jared Woolston (Jared.Woolston@gmail.com)
 * @see <a href=http://www.usb.org/developers/docs/devclass_docs/USB_Video_Class_1_5.zip>UVC 1.5 Class
 * Specification §4.2.2</a>
 */
//TODO: all of the ***_ALL requests.
public abstract class UnitTerminalControlRequest extends VideoClassRequest {

    protected UnitTerminalControlRequest(@NonNull Request request, short value, short index,
                                         @NonNull byte[] data) {
        super((request == Request.SET_CUR || request == Request.SET_CUR_ALL)
                ? SET_REQUEST_INF_ENTITY : GET_REQUEST_INF_ENTITY, request, value, index, data);
    }
}
