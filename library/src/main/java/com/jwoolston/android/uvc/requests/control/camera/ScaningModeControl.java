package com.jwoolston.android.uvc.requests.control.camera;

import android.support.annotation.NonNull;

import com.jwoolston.android.uvc.interfaces.VideoControlInterface;
import com.jwoolston.android.uvc.interfaces.terminals.CameraTerminal;
import com.jwoolston.android.uvc.requests.Request;
import com.jwoolston.android.uvc.requests.VideoClassRequest;

import static com.jwoolston.android.uvc.interfaces.terminals.CameraTerminal.Control.SCANNING_MODE;

/**
 * The Scanning Mode Control setting is used to control the scanning mode of the camera sensor. A value of 0 indicates
 * that the interlace mode is enabled, and a value of 1 indicates that the progressive or the non-interlace mode is
 * enabled.
 *
 * @author Jared Woolston (Jared.Woolston@gmail.com)
 * @see <a href=http://www.usb.org/developers/docs/devclass_docs/USB_Video_Class_1_5.zip>UVC 1.5 Class
 * Specification §4.2.2.1.1</a>
 */
public class ScaningModeControl extends CameraTerminalControlRequest {

    private static final int Index_bScanningMode = 0; // 1 byte

    private static final byte BYTE_TRUE = 0x01;
    private static final byte BYTE_FALSE = 0x00;

    private final byte[] data;

    public static ScaningModeControl getCurrentScanningMode(@NonNull CameraTerminal terminal,
                                                            @NonNull VideoControlInterface controlInterface) {
        return new ScaningModeControl(Request.GET_CUR, SCANNING_MODE.valueFromControl(),
            VideoClassRequest.getIndex(terminal, controlInterface), new byte[1]);
    }

    public static ScaningModeControl getScanningModeInfo(@NonNull CameraTerminal terminal,
                                                            @NonNull VideoControlInterface controlInterface) {
        return new ScaningModeControl(Request.GET_INFO, SCANNING_MODE.valueFromControl(),
            VideoClassRequest.getIndex(terminal, controlInterface), new byte[1]);
    }

    public static ScaningModeControl setCurrentScanningMode(@NonNull CameraTerminal terminal,
                                                            @NonNull VideoControlInterface controlInterface,
                                                            boolean progressive) {
        return new ScaningModeControl(Request.GET_CUR, SCANNING_MODE.valueFromControl(),
            VideoClassRequest.getIndex(terminal, controlInterface),
            new byte[] { progressive ? BYTE_TRUE : BYTE_FALSE });
    }

    private ScaningModeControl(@NonNull Request request, short value, short index, @NonNull byte[] data) {
        super(request, value, index, data);
        this.data = data;
    }

    public boolean isProgressive() {
        return data[0] != BYTE_FALSE;
    }
}
