package com.jwoolston.android.uvc;

import android.content.Context;
import android.hardware.usb.UsbDevice;
import android.net.Uri;
import android.support.annotation.NonNull;
import com.jwoolston.android.uvc.interfaces.streaming.VideoFormat;
import java.util.List;

/**
 * @author Jared Woolston (Jared.Woolston@gmail.com)
 */
public interface Webcam {

    /**
     * The {@link UsbDevice} interface to this camera.
     *
     * @return camera {@link UsbDevice}
     */
    @NonNull
    UsbDevice getDevice();

    /**
     * Determine if an active connection to the camera exists.
     *
     * @return true if connected to the camera
     */
    boolean isConnected();

    /**
     * Begin streaming from the device and retrieve the {@link Uri} for the data stream for this {@link Webcam}.
     *
     * @param context {@link Context} The application context.
     * @param format  The {@link VideoFormat} to stream in.
     *
     * @return {@link Uri} The data source {@link Uri}.
     *
     * @throws StreamCreationException Thrown if there is a problem establishing the stream buffer.
     */
    @NonNull
    Uri beginStreaming(@NonNull Context context, @NonNull VideoFormat format) throws StreamCreationException;

    /**
     * Terminates streaming from the device.
     *
     * @param context {@link Context} The application context.
     */
    void terminateStreaming(@NonNull Context context);

    /**
     * Retrieves the list of available {@link VideoFormat}s.
     *
     * @return The available {@link VideoFormat}s on the device.
     */
    List<VideoFormat> getAvailableFormats();
}
