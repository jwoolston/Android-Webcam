package com.jwoolston.usb.webcam.app;

import android.content.Intent;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;

import com.jwoolston.usb.webcam.Webcam;
import com.jwoolston.usb.webcam.WebcamManager;

public class MainActivity extends ActionBarActivity {

    private Webcam webcam;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
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

        // Get the connected webcam if one is newly attached or already connected
        final Intent intent = getIntent();
        if (intent.hasExtra(UsbManager.EXTRA_DEVICE)) {
            final UsbDevice usbDevice = intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);
            webcam = WebcamManager.getOrCreateWebcam(this, usbDevice);
        }
    }
}
