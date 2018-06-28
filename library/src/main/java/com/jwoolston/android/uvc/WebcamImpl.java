package com.jwoolston.android.uvc;

import android.content.Context;
import android.hardware.usb.UsbDevice;
import android.net.Uri;
import android.support.annotation.NonNull;
import com.jwoolston.android.libusb.DevicePermissionDenied;
import com.jwoolston.android.uvc.interfaces.streaming.VideoFormat;
import java.util.List;

/**
 * @author Jared Woolston (Jared.Woolston@gmail.com)
 */
class WebcamImpl implements Webcam {

    private final Context          context;
    private final UsbDevice        device;
    private final WebcamConnection webcamConnection;

    WebcamImpl(Context context, UsbDevice device) throws UnknownDeviceException, DevicePermissionDenied {
        this.context = context;
        this.device = device;

        webcamConnection = new WebcamConnection(context.getApplicationContext(), device);
    }

    @NonNull
    @Override
    public UsbDevice getDevice() {
        return device;
    }

    @Override
    public boolean isConnected() {
        return webcamConnection.isConnected();
    }

    /**
     * Retrieves the list of available {@link VideoFormat}s.
     *
     * @return The available {@link VideoFormat}s on the device.
     */
    public List<VideoFormat> getAvailableFormats() {
        return webcamConnection.getAvailableFormats();
    }

    @NonNull
    @Override
    public Uri beginStreaming(@NonNull Context context, @NonNull VideoFormat format) throws StreamCreationException {
        return webcamConnection.beginConnectionStreaming(context, format);
    }

    public void terminateStreaming(@NonNull Context context) {
        webcamConnection.terminateConnection(context);
    }
}
