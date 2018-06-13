package com.jwoolston.android.uvc;

import android.content.Context;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbInterface;
import android.hardware.usb.UsbManager;
import android.net.Uri;
import android.util.Log;
import com.jwoolston.android.uvc.interfaces.Descriptor;
import com.jwoolston.android.uvc.interfaces.InterfaceAssociationDescriptor;
import com.jwoolston.android.uvc.interfaces.VideoControlInterface;
import com.jwoolston.android.uvc.requests.PowerModeControl;
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
    final UsbInterface        usbInterfaceControl;

    private OutputStream bufferStream;

    private List<InterfaceAssociationDescriptor> iads;

    WebcamConnection(Context context, UsbManager usbManager, UsbDevice usbDevice) throws UnknownDeviceException {
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
        //TODO: Throw exception if unable to claim interface

        parseAssiociationDescriptors();

        Log.d(TAG, "Initializing native layer.");
        //final IsochronousConnection util = new IsochronousConnection(context, usbDeviceConnection.getFileDescriptor
        // ());

        Log.d(TAG, "Attempting to set current power mode.");
        final PowerModeControl control = PowerModeControl.setFullPowerMode(
                (VideoControlInterface) iads.get(0).getInterface(0));
        int retval = usbDeviceConnection.controlTransfer(control.getRequestType(), control.getRequest(), control
                .getValue(), control.getIndex(), control.getData(), control.getLength(), 500);
        Log.v(TAG, "Control transfer length: " + retval);
    }

    private void parseAssiociationDescriptors() {
        Log.d(TAG, "Parsing raw association descriptors.");
        final byte[] raw = usbDeviceConnection.getRawDescriptors();
        iads = Descriptor.parseDescriptors(usbDevice, raw);
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
     *
     * @return {@link Uri} pointing to the buffered stream.
     *
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
