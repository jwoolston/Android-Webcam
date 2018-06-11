package com.jwoolston.usb.webcam;

import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbInterface;
import android.hardware.usb.UsbManager;

/**
 * Helper class for abstracting communication to a camera. This implementation directly handles
 * configuration, state, and data transfer. The USB layer is constructed at instantiation and if
 * possible, communication begins immediately.
 */
class WebcamConnection {

    private static final int INTERFACE_CONTROL = 0;

    final UsbDeviceConnection usbDeviceConnection;
    final UsbManager usbManager;
    final UsbDevice usbDevice;
    final UsbInterface usbInterfaceControl;

    WebcamConnection(UsbManager usbManager, UsbDevice usbDevice) throws UnknownDeviceException {
        this.usbManager = usbManager;
        this.usbDevice = usbDevice;

        // A Webcam must have at least a control interface and a video interface
        if (usbDevice.getInterfaceCount() < 2) {
            throw new UnknownDeviceException();
        }

        usbInterfaceControl = usbDevice.getInterface(INTERFACE_CONTROL);

        // Claim the control interface
        usbDeviceConnection = usbManager.openDevice(usbDevice);
        usbDeviceConnection.claimInterface(usbInterfaceControl, true);
    }

    boolean isConnected() {
        // FIXME
        return true;
    }

}
