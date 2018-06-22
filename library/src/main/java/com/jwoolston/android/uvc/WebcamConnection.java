package com.jwoolston.android.uvc;

import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;
import com.jwoolston.android.libusb.DevicePermissionDenied;
import com.jwoolston.android.libusb.UsbDeviceConnection;
import com.jwoolston.android.libusb.UsbManager;
import com.jwoolston.android.uvc.interfaces.Descriptor;
import com.jwoolston.android.uvc.interfaces.InterfaceAssociationDescriptor;
import com.jwoolston.android.uvc.interfaces.VideoControlInterface;
import com.jwoolston.android.uvc.interfaces.VideoStreamingInterface;
import com.jwoolston.android.uvc.requests.PowerModeControl;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import timber.log.Timber;

/**
 * Helper class for abstracting communication to a camera. This implementation directly handles configuration, state,
 * and data transfer. The USB layer is constructed at instantiation and if possible, communication begins immediately.
 *
 * @author Jared Woolston (Jared.Woolston@gmail.com)
 */
class WebcamConnection {

    private static final int INTERFACE_CONTROL = 0;

    final UsbDeviceConnection usbDeviceConnection;
    final UsbManager          usbManager;

    private OutputStream bufferStream;

    private List<InterfaceAssociationDescriptor> iads;

    WebcamConnection(@NonNull Context context, @NonNull android.hardware.usb.UsbDevice usbDevice)
            throws UnknownDeviceException, DevicePermissionDenied {
        this.usbManager = new UsbManager(context);

        // A Webcam must have at least a control interface and a video interface
        if (usbDevice.getInterfaceCount() < 2) {
            throw new UnknownDeviceException();
        }

        // Claim the control interface
        Timber.d("Initializing native layer.");
        usbDeviceConnection = usbManager.registerDevice(usbDevice);
        parseAssiociationDescriptors();

        Timber.d("Attempting to select zero bandwidth stream interface.");
        //iads.get(0).getInterface(1).selectAlternateSetting(usbDeviceConnection, 0);
        VideoStreamingInterface streamingInterface = (VideoStreamingInterface) iads.get(0).getInterface(1);
        streamingInterface.selectAlternateSetting(usbDeviceConnection, 0);
        //util.selectAlternateSetting(streamingInterface.getInterfaceNumber(), 0);

        //clearStall(usbInterfaceControl.getEndpoint(0));

        Timber.d("Attempting to set current power mode.");
        final PowerModeControl control = PowerModeControl.getInfoPowerMode(
                (VideoControlInterface) iads.get(0).getInterface(0));
        Timber.v("Request: " + control);
        /*int retval = usbDeviceConnection.controlTransfer(control.getRequestType(), control.getRequest(), control
                .getValue(), control.getIndex(), control.getData(), control.getLength(), 500);*/
        /*int retval = util.controlTransfer(control.getRequestType(), control.getRequest(), control
                .getValue(), control.getIndex(), control.getData(), control.getLength(), 1000);
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        retval = util.controlTransfer(control.getRequestType(), control.getRequest(), control
                .getValue(), control.getIndex(), control.getData(), control.getLength(), 1000);*/

        /*Log.v(TAG, "Control transfer length: " + retval);
        clearStall(usbInterfaceControl.getEndpoint(0));
        retval = usbDeviceConnection.controlTransfer(control.getRequestType(), control.getRequest(), control
                .getValue(), control.getIndex(), control.getData(), control.getLength(), 500);
        Log.v(TAG, "Control transfer length: " + retval);*/
    }

    private void parseAssiociationDescriptors() {
        Timber.d("Parsing raw association descriptors.");
        final byte[] raw = usbDeviceConnection.getRawDescriptors();
        final List<InterfaceAssociationDescriptor> iads = Descriptor.parseDescriptors(usbDeviceConnection, raw);
        Timber.i("Determined IADs: %s", iads);
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
        return null;
    }

    /**
     * Terminates streaming from the device.
     *
     * @param context {@link Context} The application context.
     */
    void terminateConnection(Context context) {

    }
}
