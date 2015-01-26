package com.jwoolston.usb.webcam.libusb;

import android.content.Context;
import android.hardware.usb.UsbManager;

/**
 * @author Jared Woolston (jwoolston@idealcorp.com)
 */
public class UsbUtil {

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
    public static void initializeLibUSB(Context context) {
        System.loadLibrary("libandroid-libusb");
        sContext = context.getApplicationContext();
        sUsbManager = (UsbManager) sContext.getSystemService(Context.USB_SERVICE);
    }
}
