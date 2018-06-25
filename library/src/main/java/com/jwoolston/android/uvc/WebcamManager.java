package com.jwoolston.android.uvc;

import android.content.Context;
import android.hardware.usb.UsbDevice;
import android.support.annotation.NonNull;
import com.jwoolston.android.libusb.DevicePermissionDenied;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Jared Woolston (Jared.Woolston@gmail.com)
 */
public class WebcamManager {

    private static final String BUFFER_CACHE_DIR = "/buffer_data";

    private static final Map<UsbDevice, Webcam> CONNECTIONS =
            Collections.synchronizedMap(new HashMap<UsbDevice, Webcam>());

    /**
     * Constructor.
     *
     * @param context The application {@link Context}.
     */
    private WebcamManager(@NonNull Context context) {
        // We are going to need this a lot, so store a copy
        //TODO
    }

    /**
     * Get the {@link Webcam} for the {@link android.hardware.usb.UsbDevice}
     * or create a new instance.
     *
     * @param context   application context
     * @param device link for {@link Webcam} instance
     *
     * @return an existing or new {@link Webcam} instance
     */
    public static
    @NonNull
    Webcam getOrCreateWebcam(@NonNull Context context, @NonNull UsbDevice device) throws UnknownDeviceException,
                                                                                            DevicePermissionDenied {
        Webcam webcam = CONNECTIONS.get(device);
        if (webcam == null) {
            webcam = new WebcamImpl(context.getApplicationContext(), device);
            CONNECTIONS.put(device, webcam);
        }

        return webcam;
    }
}
