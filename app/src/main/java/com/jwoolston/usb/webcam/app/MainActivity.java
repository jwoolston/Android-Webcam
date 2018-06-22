package com.jwoolston.usb.webcam.app;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.jwoolston.android.uvc.UnknownDeviceException;
import com.jwoolston.android.uvc.Webcam;
import com.jwoolston.android.uvc.WebcamManager;

import timber.log.Timber;

public class MainActivity extends AppCompatActivity {

    private Webcam webcam;
    private BroadcastReceiver deviceDisconnectedReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        deviceDisconnectedReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (webcam == null)
                    return;

                final UsbDevice usbDevice = intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);
                if (usbDevice.equals(webcam.getUsbDevice())) {
                    Timber.d("Active Webcam detached. Terminating connection.");
                    stopStreaming();
                }
            }
        };
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        // Replace intent returned by getIntent()
        setIntent(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();

        registerReceiver(deviceDisconnectedReceiver, new IntentFilter(UsbManager.ACTION_USB_DEVICE_DETACHED));

        // Get the connected webcam if one is newly attached or already connected
        final Intent intent = getIntent();
        if (intent.hasExtra(UsbManager.EXTRA_DEVICE)) {
            final UsbDevice usbDevice = intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);
            try {
                webcam = WebcamManager.getOrCreateWebcam(this, usbDevice);
            } catch (UnknownDeviceException e) {
                e.printStackTrace();
                Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

        unregisterReceiver(deviceDisconnectedReceiver);
    }

    /**
     * Shutdown the active webcam device if one exists.
     */
    private void stopStreaming() {
        if (webcam != null) {
            webcam.terminateStreaming(this);
        }
    }
}
