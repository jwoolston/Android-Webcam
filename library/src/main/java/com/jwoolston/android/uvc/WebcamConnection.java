package com.jwoolston.android.uvc;

import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;
import com.jwoolston.android.libusb.DevicePermissionDenied;
import com.jwoolston.android.libusb.LibusbError;
import com.jwoolston.android.libusb.UsbDeviceConnection;
import com.jwoolston.android.libusb.UsbManager;
import com.jwoolston.android.uvc.interfaces.Descriptor;
import com.jwoolston.android.uvc.interfaces.InterfaceAssociationDescriptor;
import com.jwoolston.android.uvc.interfaces.VideoControlInterface;
import com.jwoolston.android.uvc.interfaces.VideoStreamingInterface;
import com.jwoolston.android.uvc.requests.control.PowerModeControl;
import com.jwoolston.android.uvc.requests.control.RequestErrorCode;
import java.io.OutputStream;
import java.util.List;
import java.util.Locale;
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
        Timber.v("Interface count: %d", iads.get(0).getInterfaceCount());
        VideoStreamingInterface streamingInterface = (VideoStreamingInterface) iads.get(0).getInterface(1);
        streamingInterface.selectAlternateSetting(usbDeviceConnection, 0);

        //clearStall(usbInterfaceControl.getEndpoint(0));

        PowerModeControl control = PowerModeControl.getCurrentPowerMode(
                (VideoControlInterface) iads.get(0).getInterface(0));
        Timber.v("Request: %s", control);

        int retval = usbDeviceConnection.controlTransfer(control.getRequestType(), control.getRequest(), control
                .getValue(), control.getIndex(), control.getData(), control.getLength(), 500);
        Timber.d("Request Result: %s", (retval > 0 ? retval : LibusbError.fromNative(retval)));

        Timber.d("Response data: 0x%s", Integer.toHexString(0xFF & control.getData()[0]).toUpperCase(Locale.US));

        Timber.d("Attempting to get current error code.");
        RequestErrorCode control2 = RequestErrorCode.getCurrentErrorCode(
                (VideoControlInterface) iads.get(0).getInterface(0));
        Timber.v("Request: %s", control2);

        retval = usbDeviceConnection.controlTransfer(control2.getRequestType(), control2.getRequest(), control2
                .getValue(), control2.getIndex(), control2.getData(), control2.getLength(), 500);
        Timber.d("Request Result: %s", (retval > 0 ? retval : LibusbError.fromNative(retval)));

        Timber.d("Response data: 0x%s", Integer.toHexString(0xFF & control2.getData()[0]).toUpperCase(Locale.US));
    }

    private void parseAssiociationDescriptors() {
        Timber.d("Parsing raw association descriptors.");
        final byte[] raw = usbDeviceConnection.getRawDescriptors();
        iads = Descriptor.parseDescriptors(usbDeviceConnection, raw);
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
