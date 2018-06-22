package com.jwoolston.android.uvc;

import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.util.Log;
import com.jwoolston.android.libusb.DevicePermissionDenied;
import com.jwoolston.android.libusb.UsbDevice;
import com.jwoolston.android.libusb.UsbDeviceConnection;
import com.jwoolston.android.libusb.UsbManager;
import com.jwoolston.android.uvc.interfaces.Descriptor;
import com.jwoolston.android.uvc.interfaces.InterfaceAssociationDescriptor;
import com.jwoolston.android.uvc.libusb.UsbDeviceIsoConnection;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

/**
 * Helper class for abstracting communication to a camera. This implementation directly handles
 * configuration, state, and data transfer. The USB layer is constructed at instantiation and if
 * possible, communication begins immediately.
 *
 * @author Jared Woolston (Jared.Woolston@gmail.com)
 */
class WebcamConnection {

    private static final String TAG = "WebcamConnection";

    private static final int INTERFACE_CONTROL = 0;

    final UsbDeviceConnection usbDeviceConnection;
    final UsbManager          usbManager;
    final UsbDevice           usbDevice;

    private OutputStream bufferStream;

    WebcamConnection(Context context, @NonNull android.hardware.usb.UsbDevice usbDevice) throws UnknownDeviceException,
                                                                                                DevicePermissionDenied {
        this.usbManager = new UsbManager(context);
        //this.usbDevice = usbDevice;
        this.usbDevice = null;

        // A Webcam must have at least a control interface and a video interface
        if (usbDevice.getInterfaceCount() < 2) {
            throw new UnknownDeviceException();
        }

        // Claim the control interface
        usbDeviceConnection = usbManager.registerDevice(usbDevice);

        parseAssiociationDescriptors();

        Log.d(TAG, "Initializing native layer.");
        final UsbDeviceIsoConnection util = new UsbDeviceIsoConnection(context, usbDeviceConnection.getFileDescriptor());
    }

    private void parseAssiociationDescriptors() {
        Log.d(TAG, "Parsing raw association descriptors.");
        final byte[] raw = usbDeviceConnection.getRawDescriptors();
        final List<InterfaceAssociationDescriptor> iads =  Descriptor.parseDescriptors(usbDevice, raw);
        Log.i(TAG, "Determined IADs: " + iads);
    }

    boolean isConnected() {
        // FIXME
        return true;
    }

    /**
     * Begins streaming from the device.
     *
     * @param context {@link Context} The application context.
     * @return {@link Uri} pointing to the buffered stream.
     * @throws StreamCreationException Thrown if there is a problem establishing the stream buffer.
     */
    Uri beginConnectionStreaming(Context context) throws StreamCreationException {
        try {
            final File buffer = WebcamManager.getBufferFile(context, usbDevice);
            bufferStream = new FileOutputStream(buffer);
            // TODO: Configure and initiate stream
            return Uri.fromFile(buffer);
        } catch (IOException e) {
            throw new StreamCreationException();
        }
    }

    /**
     * Terminates streaming from the device.
     *
     * @param context {@link Context} The application context.
     */
    void terminateConnection(Context context) {
        WebcamManager.deleteBufferFile(context, usbDevice);
    }
}
