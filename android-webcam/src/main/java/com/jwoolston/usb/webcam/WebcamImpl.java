package com.jwoolston.usb.webcam;

import android.content.Context;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;
import android.support.annotation.NonNull;

class WebcamImpl implements Webcam {

    private final UsbDevice usbDevice;
    private final WebcamConnection webcamConnection;

    WebcamImpl(Context context, UsbDevice usbDevice) {
        this.usbDevice = usbDevice;

        final UsbManager usbManager = (UsbManager) context.getSystemService(Context.USB_SERVICE);
        webcamConnection = new WebcamConnection(usbManager, usbDevice);
    }

    @NonNull
    @Override
    public UsbDevice getUsbDevice() {
        return usbDevice;
    }

    @Override
    public boolean isConnected() {
        return webcamConnection.isConnected();
    }
}
