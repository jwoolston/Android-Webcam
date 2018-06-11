package com.jwoolston.usb.webcam;

import android.content.Context;
import android.hardware.usb.UsbDevice;
import android.support.annotation.NonNull;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class WebcamManager {

    private static final Map<UsbDevice, Webcam> DEVICES = Collections.synchronizedMap(new HashMap<UsbDevice, Webcam>());

    private WebcamManager() {
    }

    /**
     * Get the {@link com.jwoolston.usb.webcam.Webcam} for the {@link android.hardware.usb.UsbDevice}
     * or create a new instance.
     *
     * @param context application context
     * @param usbDevice link for {@link com.jwoolston.usb.webcam.Webcam} instance
     * @return an existing or new {@link com.jwoolston.usb.webcam.Webcam} instance
     */
    public static
    @NonNull
    Webcam getOrCreateWebcam(@NonNull Context context, @NonNull UsbDevice usbDevice) {
        Webcam webcam = DEVICES.get(usbDevice);
        if (webcam == null) {
            webcam = new WebcamImpl(context.getApplicationContext(), usbDevice);
            DEVICES.put(usbDevice, webcam);
        }

        return webcam;
    }

}
