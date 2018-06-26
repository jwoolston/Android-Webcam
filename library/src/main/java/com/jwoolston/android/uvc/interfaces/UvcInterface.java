package com.jwoolston.android.uvc.interfaces;

import static com.jwoolston.android.uvc.interfaces.Descriptor.VideoSubclass;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.SparseArray;
import com.jwoolston.android.libusb.LibusbError;
import com.jwoolston.android.libusb.UsbDeviceConnection;
import com.jwoolston.android.libusb.UsbInterface;
import com.jwoolston.android.uvc.interfaces.Descriptor.Protocol;
import com.jwoolston.android.uvc.interfaces.endpoints.Endpoint;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import timber.log.Timber;

/**
 * @author Jared Woolston (Jared.Woolston@gmail.com)
 */
public abstract class UvcInterface {

    private static final int LENGTH_STANDARD_DESCRIPTOR = 9;

    protected static final int bLength            = 0;
    protected static final int bDescriptorType    = 1;
    protected static final int bInterfaceNumber   = 2;
    protected static final int bAlternateSetting  = 3;
    protected static final int bNumEndpoints      = 4;
    protected static final int bInterfaceClass    = 5;
    protected static final int bInterfaceSubClass = 6;
    protected static final int bInterfaceProtocol = 7;
    protected static final int iInterface         = 8;

    protected final SparseArray<UsbInterface> usbInterfaces;
    protected final SparseArray<Endpoint[]>   endpoints;

    protected int currentSetting = 0;

    protected static UsbInterface getUsbInterface(UsbDeviceConnection connection, byte[] descriptor) {
        final int indexNumber = (0xFF & descriptor[bInterfaceNumber]);
        final int alternateSetting = (0xFF & descriptor[bAlternateSetting]);
        return connection.getDevice().getInterface(indexNumber + alternateSetting);
    }

    public static UvcInterface parseDescriptor(UsbDeviceConnection connection, byte[] descriptor) throws
                                                                                                  IllegalArgumentException {
        // Check the length
        if (descriptor.length < LENGTH_STANDARD_DESCRIPTOR) {
            throw new IllegalArgumentException("Descriptor is not long enough to be a standard interface descriptor.");
        }
        // Check the class
        if (descriptor[bInterfaceClass] == Descriptor.VIDEO_CLASS_CODE) {
            // For video class, only PC_PROTOCOL_15 is permitted
            if (descriptor[bInterfaceProtocol] != Protocol.PC_PROTOCOL_15.protocol) {
                switch (VideoSubclass.getVideoSubclass(descriptor[bInterfaceSubClass])) {
                    // We could handle Interface Association Descriptors here, but they don't correspond to an
                    // accessable interface, so we
                    // treat them separately
                    case SC_VIDEOCONTROL:
                        return VideoControlInterface.parseVideoControlInterface(connection, descriptor);
                    case SC_VIDEOSTREAMING:
                        return VideoStreamingInterface.parseVideoStreamingInterface(connection, descriptor);
                    default:
                        throw new IllegalArgumentException(
                                "The provided descriptor has an invalid video interface subclass.");
                }
            } else {
                throw new IllegalArgumentException(
                        "The provided descriptor has an invalid protocol: " + descriptor[bInterfaceProtocol]);
            }
        } else if (descriptor[bInterfaceClass] == Descriptor.AUDIO_CLASS_CODE) {
            // TODO: Something with the audio class
            return null;
        } else {
            throw new IllegalArgumentException(
                    "The provided descriptor has an invalid interface class: " + descriptor[bInterfaceClass]);
        }
    }

    protected UvcInterface(UsbInterface usbInterface, byte[] descriptor) {
        usbInterfaces = new SparseArray<>();
        endpoints = new SparseArray<>();
        currentSetting = 0xFF & descriptor[bAlternateSetting];
        usbInterfaces.put(currentSetting, usbInterface);
        final int endpointCount = (0xFF & descriptor[bNumEndpoints]);
        endpoints.put(currentSetting, new Endpoint[endpointCount]);
    }

    public void selectAlternateSetting(@NonNull UsbDeviceConnection connection, int alternateSetting) throws
                                                                                                      UnsupportedOperationException {
        currentSetting = alternateSetting;
        final UsbInterface usbInterface = getUsbInterface();
        if (usbInterface == null) {
            throw new UnsupportedOperationException("There is not alternate setting: " + alternateSetting);
        }
        connection.claimInterface(usbInterface, true);
        final LibusbError result = connection.setInterface(usbInterface);
        Timber.d("Interface selection result: %s", result);
        Timber.d("Available Endpoints: %s", Arrays.toString(getCurrentEndpoints()));
    }

    public void addEndpoint(int index, @NonNull Endpoint endpoint) {
        Timber.d("Adding endpoint for current setting: %d/%d = %s", currentSetting, index, endpoint);
        Endpoint[] array = endpoints.get(currentSetting);
        Timber.v("Array: %s, %d", array, array.length);
        array[index - 1] = endpoint;
        Timber.w("Array after: %s", Arrays.toString(array));
        endpoints.put(currentSetting, array);
    }

    public Endpoint getEndpoint(int index) {
        return endpoints.get(currentSetting)[index - 1];
    }

    public Endpoint[] getCurrentEndpoints() {
        return endpoints.get(currentSetting);
    }

    public void printEndpoints() {
        for (int i = 0; i < endpoints.size(); ++i) {
            Timber.v("Alternate Function %d: %s", endpoints.keyAt(i),
                     Arrays.toString(endpoints.get(endpoints.keyAt(i))));
        }
    }

    public int getInterfaceNumber() {
        return usbInterfaces.get(currentSetting).getId();
    }

    @Nullable
    public UsbInterface getUsbInterface() {
        return usbInterfaces.get(currentSetting);
    }

    public List<UsbInterface> getUsbInterfaceList() {
        final LinkedList<UsbInterface> interfaces = new LinkedList<>();
        for (int i = 0; i < usbInterfaces.size(); ++i) {
            interfaces.add(usbInterfaces.get(usbInterfaces.keyAt(i)));
        }
        return interfaces;
    }

    public abstract void parseClassDescriptor(byte[] descriptor);

    public abstract void parseAlternateFunction(@NonNull UsbDeviceConnection connection, byte[] descriptor);

    @Override
    public String toString() {
        return "AInterface{" +
               "usbInterface=" + usbInterfaces +
               '}';
    }
}
