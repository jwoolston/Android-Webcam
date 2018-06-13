package com.jwoolston.android.uvc.requests;

import static com.jwoolston.android.uvc.requests.InterfaceControlRequest.ControlSelector.VC_VIDEO_POWER_MODE_CONTROL;

import android.support.annotation.NonNull;
import com.jwoolston.android.uvc.interfaces.VideoControlInterface;

/**
 * This control sets the device power mode. Power modes are defined in the following table.
 *
 * Table 4-5 Power Mode Control
 * <table>
 * <tr>
 * <th>Device Power Mode</th>
 * <th>Description</th>
 * </tr>
 * <tr>
 * <td>Full Power Mode</td>
 * <td>Device operates at full functionality in this mode. For example, the device can stream video data via USB, and
 * can
 * execute all requests that are supported by the device. This mode is mandatory, even if the device doesn’t support
 * VIDEO POWER MODE CONTROL.</td>
 * </tr>
 * <tr>
 * <td>Vendor-Dependent
 * Power Mode</td>
 * <td>Device operates in low power mode. In this mode, the device continues to operate, although not at full
 * functionality.
 * For example, as the result of setting the device to this power mode, the device will stop the Zoom function. To
 * avoid confusing the user, the device should issue an interrupt (GET_INFO) to notify the user that the Zoom
 * function is disabled.
 * In this mode, the device can stream video data, the functionality of USB is not affected, and the device can
 * execute all requests that it supports.
 * This mode is optional.</td>
 * </tr>
 * </table>
 *
 * The power mode that is supported by the device must be passed to the host, as well as the power source, since if
 * the device is working with battery power, the host can change the device power mode to “vendor-dependent power
 * mode” to reduce power consumption.
 * Information regarding power modes and power sources is communicated through the following bit fields. D7..D5
 * indicates which power source is currently used in the device. The D4 indicates that the device supports
 * “vendor-dependent power mode”. Bits D7..D4 are set by the device and are read-only. The host can change the device
 * power mode by setting a combination of D3..D0.
 * The host can update the power mode during video streaming.
 * The D3..D0 value of 0000B indicates that the device is in, or should transition to, full power mode. The D3..D0
 * value of 0001B indicates that the device is in, or should transition to, vendor-dependent power mode.
 * The host must specify D3..D0 only when the power mode is required to switch, and the other fields must be set to 0.
 *
 * @author Jared Woolston (Jared.Woolston@gmail.com)
 * @see <a href=http://www.usb.org/developers/docs/devclass_docs/USB_Video_Class_1_5.zip>UVC 1.5 Class
 * Specification §4.2.1.1</a>
 */
public class PowerModeControl extends InterfaceControlRequest {

    private static final byte SET_MASK = 0x0F;
    private static final byte GET_MASK = (byte) 0xF0;
    private static final byte FULL_POWER_MODE = 0x00;
    private static final byte VENDOR_SPECIFIC_MODE = 0x01;

    @NonNull
    public static PowerModeControl setFullPowerMode(@NonNull VideoControlInterface controlInterface) {
        return new PowerModeControl(Request.SET_CUR, (short) (0xFF & controlInterface.getIndexInterface()),
                                    new byte[] { FULL_POWER_MODE });
    }

    @NonNull
    public static PowerModeControl setVendorPowerMode(@NonNull VideoControlInterface controlInterface) {
        return new PowerModeControl(Request.SET_CUR, (short) (0xFF & controlInterface.getIndexInterface()),
                                    new byte[] { VENDOR_SPECIFIC_MODE });
    }

    @NonNull
    public static PowerModeControl getCurrentPowerMode(@NonNull VideoControlInterface controlInterface) {
        return new PowerModeControl(Request.GET_CUR, (short) (0xFF & controlInterface.getIndexInterface()),
                                    new byte[1]);
    }

    @NonNull
    public static PowerModeControl getInfoPowerMode(@NonNull VideoControlInterface controlInterface) {
        return new PowerModeControl(Request.GET_INFO, (short) (0xFF & controlInterface.getIndexInterface()),
                                    new byte[1]);
    }

    private PowerModeControl(@NonNull Request request, short index, @NonNull byte[] data) {
        super(request, VC_VIDEO_POWER_MODE_CONTROL, index, data);
    }
}
