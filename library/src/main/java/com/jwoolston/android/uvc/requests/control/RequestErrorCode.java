package com.jwoolston.android.uvc.requests.control;

import static com.jwoolston.android.uvc.requests.control.VCInterfaceControlRequest.ControlSelector.VC_REQUEST_ERROR_CODE_CONTROL;

import android.support.annotation.NonNull;
import com.jwoolston.android.uvc.interfaces.VideoControlInterface;
import com.jwoolston.android.uvc.requests.Request;

/**
 * This read-only control indicates the status of each host-initiated request to a Terminal, Unit, interface or
 * endpoint of the video function. If the device is unable to fulfill the request, it will indicate a stall on the
 * control pipe and update this control with the appropriate code to indicate the cause. This control will be reset
 * to 0 (No error) upon the successful completion of any control request (including requests to this control). The
 * table below specifies the bRequestErrorCode error codes that the device must return from a
 * VC_REQUEST_ERROR_CODE_CONTROL request. Asynchronous control requests are a special case, where the initial request
 * will update this control, but the final result is delivered via the Status Interrupt Endpoint (see sections
 * 2.4.2.2, "Status Interrupt Endpoint" and 2.4.4, "Control Transfer and Request Processing").
 * <p>
 * 0x00: No error<br>
 * 0x01: Not ready<br>
 * 0x02: Wrong state<br>
 * 0x03: Power<br>
 * 0x04: Out of range<br>
 * 0x05: Invalid unit<br>
 * 0x06: Invalid control<br>
 * 0x07: Invalid Request<br>
 * 0x08: Invalid value within range<br>
 * 0x09-0xFE: Reserved for future use<br>
 * 0xFF: Unknown<br>
 * <p>
 * -No error: The request succeeded.<br>
 * -Not ready: The device has not completed a previous operation. The device will recover from this state as soon as
 * the previous operation has completed.<br>
 * -Wrong State: The device is in a state that disallows the specific request. The device will remain in this state
 * until a specific action from the host or the user is completed.<br>
 * -Power: The actual Power Mode of the device is not sufficient to complete the Request.<br>
 * -Out of Range: Result of a SET_CUR Request when attempting to set a value outside of the MIN and MAX range, or a
 * value that does not satisfy the constraint on resolution (see section 4.2.2, “Unit and Terminal Control Requests”).<br>
 * -Invalid Unit: The Unit ID addressed in this Request is not assigned.<br>
 * -Invalid Control: The Control addressed by this Request is not supported.<br>
 * -Invalid Request: This Request is not supported by the Control.<br>
 * -Invalid value with range: Results of a SET_CUR Request when attempting to set a value that is inside the MIN and
 * MAX range but is not supported.<br>
 *
 * @author Jared Woolston (Jared.Woolston@gmail.com)
 * @see <a href=http://www.usb.org/developers/docs/devclass_docs/USB_Video_Class_1_5.zip>UVC 1.5 Class
 * Specification §4.2.1.2</a>
 */
public class RequestErrorCode extends VCInterfaceControlRequest {

    @NonNull
    public static RequestErrorCode getCurrentErrorCode(@NonNull VideoControlInterface controlInterface) {
        return new RequestErrorCode(Request.GET_CUR, (short) (0xFF & controlInterface.getInterfaceNumber()),
                                    new byte[1]);
    }

    @NonNull
    public static RequestErrorCode getInfoErrorCode(@NonNull VideoControlInterface controlInterface) {
        return new RequestErrorCode(Request.GET_INFO, (short) (0xFF & controlInterface.getInterfaceNumber()),
                                    new byte[1]);
    }

    private RequestErrorCode(@NonNull Request request, short index, @NonNull byte[] data) {
        super(request, VC_REQUEST_ERROR_CODE_CONTROL, index, data);
    }
}
