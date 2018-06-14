package com.jwoolston.android.uvc.util;

import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbInterface;
import android.support.annotation.NonNull;

/**
 * @author Jared Woolston (Jared.Woolston@gmail.com)
 */
public class UsbHelper {

    private static final int SET_REQUEST_TYPE = 0x01;

    private static final int SET_INTERFACE = 0x11;

    /**
     * @param connection
     * @param usbInterface
     * @param alternateSetting
     * @param timeout
     *
     * @return
     * @see <a href=https://www.beyondlogic.org/usbnutshell/usb6.shtml#StandardInterfaceRequests>Standard Interface Request</a>
     */
    public static int selectInterface(@NonNull UsbDeviceConnection connection, @NonNull UsbInterface usbInterface,
                                      int alternateSetting, int timeout) {
        /*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            return connection.setInterface(usbInterface) ? 0 : -1;
        } else {*/
            return connection.controlTransfer(SET_REQUEST_TYPE, SET_INTERFACE, alternateSetting, usbInterface.getId(),
                new byte[0], 0, timeout);
        //}
    }
}
