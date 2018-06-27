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
import com.jwoolston.android.libusb.DevicePermissionDenied;
import com.jwoolston.android.uvc.StreamCreationException;
import com.jwoolston.android.uvc.UnknownDeviceException;
import com.jwoolston.android.uvc.Webcam;
import com.jwoolston.android.uvc.WebcamManager;
import com.jwoolston.android.uvc.interfaces.streaming.VideoFormat;
import java.util.List;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import timber.log.Timber;

public class MainActivity extends AppCompatActivity {

    private static final String KEY_OPEN_DEVICE = MainActivity.class.getCanonicalName() + ".KEY_OPEN_DEVICE";

    private UsbDevice openDevice;

    private Webcam            webcam;
    private List<VideoFormat> formats;
    private BroadcastReceiver deviceDisconnectedReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (savedInstanceState != null) {
            openDevice = savedInstanceState.getParcelable(KEY_OPEN_DEVICE);
        }

        deviceDisconnectedReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (webcam == null) {
                    return;
                }

                final UsbDevice usbDevice = intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);
                if (usbDevice.equals(webcam.getDevice())) {
                    Timber.d("Active Webcam detached. Terminating connection.");
                    stopStreaming();
                }
            }
        };
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        if (intent.hasExtra(UsbManager.EXTRA_DEVICE)) {
            openDevice = intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);
            handleDevice();
        }

        // Replace intent returned by getIntent()
        setIntent(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();

        registerReceiver(deviceDisconnectedReceiver, new IntentFilter(UsbManager.ACTION_USB_DEVICE_DETACHED));

        EventBus.getDefault().register(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onNewVideoFormatSelected(VideoFormatSelected selected) {
        for (VideoFormat format : formats) {
            if (format.getFormatIndex() == selected.index) {
                try {
                    webcam.beginStreaming(this, format);
                } catch (StreamCreationException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void handleDevice() {
        // Get the connected webcam if one is newly attached or already connected

        if (openDevice != null) {
            try {
                webcam = WebcamManager.getOrCreateWebcam(this, openDevice);
                formats = webcam.getAvailableFormats();
                showFormatPicker();
            } catch (UnknownDeviceException | DevicePermissionDenied e) {
                e.printStackTrace();
                Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
            }
        }
    }

    private void showFormatPicker() {
        FormatPickerDialog dialog = new FormatPickerDialog();
        final Bundle args = new Bundle();
        final String[] names = new String[formats.size()];
        final int[] indices = new int[formats.size()];
        for (int i = 0; i < formats.size(); ++i) {
            names[i] = formats.get(i).getClass().getSimpleName();
            indices[i] = formats.get(i).getFormatIndex();
        }
        args.putStringArray(FormatPickerDialog.ARGUMENT_VALUES, names);
        args.putIntArray(FormatPickerDialog.ARGUMENT_INDICES, indices);
        dialog.setArguments(args);
        dialog.show(getSupportFragmentManager(), FormatPickerDialog.TAG);
    }

    @Override
    protected void onPause() {
        super.onPause();

        unregisterReceiver(deviceDisconnectedReceiver);

        EventBus.getDefault().unregister(this);
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
