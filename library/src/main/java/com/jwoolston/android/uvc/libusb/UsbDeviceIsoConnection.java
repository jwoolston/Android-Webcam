package com.jwoolston.android.uvc.libusb;

import android.content.Context;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbManager;

import java.nio.ByteBuffer;

/**
 * @author Jared Woolston (jwoolston@idealcorp.com)
 */
public class UsbDeviceIsoConnection {

    static {
        System.loadLibrary("android-libusb");
    }

    /**
     * The application context.
     */
    private static Context sContext;

    /**
     * The {@link UsbManager} for this application.
     */
    private static UsbManager sUsbManager;

    /**
     * Initializes the libusb JNI wrapper. This should be provided the Application
     * {@link Context}, though it will call {@link Context#getApplicationContext()}
     * to be sure.
     *
     * @param context {@link Context} The application context.
     */
    public UsbDeviceIsoConnection(Context context) {
        sContext = context.getApplicationContext();
        sUsbManager = (UsbManager) sContext.getSystemService(Context.USB_SERVICE);
        initialize();
    }

    private native void initialize();

    public native void deinitialize();

    public native void isochronousTransfer(UsbDeviceConnection connection, ByteBuffer buffer);

}
