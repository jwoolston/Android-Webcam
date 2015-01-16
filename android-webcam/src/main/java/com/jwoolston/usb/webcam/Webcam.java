package com.jwoolston.usb.webcam;

import android.hardware.usb.UsbDevice;
import android.support.annotation.NonNull;

public interface Webcam {

    /**
     * The {@link android.hardware.usb.UsbDevice} interface to this camera.
     *
     * @return camera {@link android.hardware.usb.UsbDevice}
     */
    public
    @NonNull
    UsbDevice getUsbDevice();

    /**
     * Determine if an active connection to the camera exists.
     *
     * @return true if connected to the camera
     */
    public boolean isConnected();



}
