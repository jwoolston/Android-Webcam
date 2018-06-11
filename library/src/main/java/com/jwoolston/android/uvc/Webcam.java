package com.jwoolston.android.uvc;

import android.content.Context;
import android.hardware.usb.UsbDevice;
import android.net.Uri;
import android.support.annotation.NonNull;

/**
 * @author Jared Woolston (Jared.Woolston@gmail.com)
 */
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

    /**
     * Begin streaming from the device and retrieve the {@link Uri} for the data stream for this {@link Webcam}.
     *
     * @param context {@link Context} The application context.
     * @return {@link Uri} The data source {@link Uri}.
     * @throws StreamCreationException Thrown if there is a problem establishing the stream buffer.
     */
    public @NonNull Uri beginStreaming(@NonNull Context context) throws StreamCreationException;

    /**
     * Terminates streaming from the device.
     *
     * @param context {@link Context} The application context.
     */
    public void terminateStreaming(@NonNull Context context);
}
