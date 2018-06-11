package com.jwoolston.android.uvc;

import android.content.Context;
import android.hardware.usb.UsbDevice;
import android.support.annotation.NonNull;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class WebcamManager {

    private static final String BUFFER_CACHE_DIR = "/buffer_data";

    private static final Map<UsbDevice, Webcam> DEVICES = Collections.synchronizedMap(new HashMap<UsbDevice, Webcam>());

    /**
     * Constructor.
     *
     * @param context The application {@link Context}.
     */
    private WebcamManager(@NonNull Context context) {
        // We are going to need this a lot, so store a copy

        final File cacheDirectory = getOrCreateBufferDirectory(context);
        // Delete any old stream cache files
        final File[] oldFiles = cacheDirectory.listFiles();
        for (File file : oldFiles) {
            file.delete();
        }
    }

    private static
    @NonNull
    File getOrCreateBufferDirectory(@NonNull Context context) {
        final File bufferDirectory = new File(context.getCacheDir().getAbsolutePath() + BUFFER_CACHE_DIR);
        if (!bufferDirectory.exists()) {
            bufferDirectory.mkdirs();
        }
        return bufferDirectory;
    }

    static
    @NonNull
    File getBufferFile(@NonNull Context context, @NonNull UsbDevice device) throws IOException {
        final File bufferDirectory = getOrCreateBufferDirectory(context);
        final File bufferFile = File.createTempFile(device.getDeviceName(), "dat", bufferDirectory);
        return bufferFile;
    }

    static void deleteBufferFile(@NonNull Context context, @NonNull UsbDevice device) {
        try {
            final File buffer = getBufferFile(context, device);
            buffer.delete();
        } catch (IOException e) {
            // Ignore it
        }
    }

    /**
     * Get the {@link Webcam} for the {@link android.hardware.usb.UsbDevice}
     * or create a new instance.
     *
     * @param context   application context
     * @param usbDevice link for {@link Webcam} instance
     *
     * @return an existing or new {@link Webcam} instance
     */
    public static
    @NonNull
    Webcam getOrCreateWebcam(@NonNull Context context, @NonNull UsbDevice usbDevice) throws UnknownDeviceException{
        Webcam webcam = DEVICES.get(usbDevice);
        if (webcam == null) {
            webcam = new WebcamImpl(context.getApplicationContext(), usbDevice);
            DEVICES.put(usbDevice, webcam);
        }

        return webcam;
    }
}
