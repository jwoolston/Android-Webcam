package com.jwoolston.android.uvc;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.jwoolston.android.libusb.LibusbError;
import com.jwoolston.android.libusb.UsbDeviceConnection;
import com.jwoolston.android.libusb.UsbEndpoint;
import com.jwoolston.android.libusb.async.IsochronousAsyncTransfer;
import com.jwoolston.android.libusb.async.IsochronousTransferCallback;
import com.jwoolston.android.uvc.interfaces.VideoStreamingInterface;
import com.jwoolston.android.uvc.interfaces.endpoints.Endpoint;
import com.jwoolston.android.uvc.interfaces.streaming.VideoFormat;
import com.jwoolston.android.uvc.interfaces.streaming.VideoFrame;
import com.jwoolston.android.uvc.requests.streaming.ProbeControl;
import com.jwoolston.android.uvc.util.Hexdump;
import java.nio.ByteBuffer;
import timber.log.Timber;

/**
 * Probe and Commit Operational Model
 * <p>
 * Unsupported fields shall be set to zero by the device. Fields left for streaming parameters negotiation shall be set
 * to zero by the host. For example, after a SET_CUR request initializing the FormatIndex and FrameIndex, the device
 * will return the new negotiated field values for the supported fields when retrieving the Probe control GET_CUR
 * attribute. In order to avoid negotiation loops, the device shall always return streaming parameters with decreasing
 * data rate requirements. Unsupported streaming parameters shall be reset by the streaming interface to supported
 * values according to the negotiation loop avoidance rules. This convention allows the host to cycle through supported
 * values of a field.
 * <p>
 * During Probe and Commit, the following fields, if supported, shall be negotiated in order of decreasing priority:
 * <p>
 * <b>- bFormatIndex</b>
 * <b>- bFrameIndex</b>
 * <b>- dwMaxPayloadTransferSize</b>
 * <b>- bUsage</b>
 * <b>- bmLayoutPerStream</b>
 * <b>- Fields set to zero by the host with their associated bmHint bit set to 1</b>
 * <b>- All the remaining fields set to zero by the host</b>
 * <p>
 * For simplicity when streaming temporally encoded video, the required bandwidth for each streaming interface shall be
 * estimated using the maximum bit rate for the selected profile/resolution and the number of simulcast streams. The USB
 * bandwidth reserved shall be the calculated by the host as the advertised dwMaxBitRate from the selected Frame
 * Descriptor multiplied times the number of simulcast streams as defined in the bmLayoutPerStream field. The interface
 * descriptor for the video function should have multiple alternate settings that support the required bandwidths
 * calculated in the manner above.
 *
 * @author Jared Woolston (Jared.Woolston@gmail.com)
 * @see <a href=http://www.usb.org/developers/docs/devclass_docs/USB_Video_Class_1_5.zip>UVC 1.5 Class
 * Specification §4.3.1.1.1</a>
 */
public class StreamManager implements IsochronousTransferCallback {

    private final UsbDeviceConnection connection;
    private final VideoStreamingInterface streamingInterface;

    public StreamManager(@NonNull UsbDeviceConnection connection, @NonNull VideoStreamingInterface streamingInterface) {
        this.connection = connection;
        this.streamingInterface = streamingInterface;
    }

    public void establishStreaming(@Nullable VideoFormat format, @Nullable VideoFrame frame) {
        final ProbeControl current = ProbeControl.getCurrentProbe(streamingInterface);

        int retval = connection.controlTransfer(current.getRequestType(), current.getRequest(),
            current.getValue(), current.getIndex(), current.getData(), current.getLength(), 500);
        Timber.d("Get Result: %s", (retval > 0 ? retval : LibusbError.fromNative(retval)));
        Timber.d("Response data: %s\n", Hexdump.dumpHexString(current.getData()));

        final ProbeControl request = ProbeControl.setCurrentProbe(streamingInterface);
        /*final VideoFormat requestedFormat = format != null ? format : streamingInterface.getAvailableFormats().get(0);

        Timber.v("Format: %s", requestedFormat);
        final VideoFrame requestedFrame = frame != null ? frame : requestedFormat.getVideoFrame(requestedFormat
                                                                                                        .getDefaultFrameIndex());
        request.setFormatIndex(requestedFormat.getFormatIndex());
        request.setFrameIndex(requestedFrame.getFrameIndex());
        request.setFrameInterval(requestedFrame.getDefaultFrameInterval());
        request.setMaxVideoFrameSize(614400);*/

        request.setFormatIndex(2);
        request.setFrameIndex(2);
        request.setFrameInterval(333333);
        request.setMaxVideoFrameSize(1843200);
        request.setMaxPayloadTransferSize(3072);

        final ProbeControl commit = request.getCommit();

        retval = connection.controlTransfer(commit.getRequestType(), commit.getRequest(),
                                            commit.getValue(), commit.getIndex(), commit.getData(), commit.getLength(), 500);
        Timber.d("Commit Result: %s", (retval > 0 ? retval : LibusbError.fromNative(retval)));

        initiateStream();
    }

    public void initiateStream() {
        streamingInterface.selectAlternateSetting(connection, 6);
        ByteBuffer buffer = ByteBuffer.allocateDirect(4096);
        Endpoint endpoint = streamingInterface.getCurrentEndpoints()[0];
        UsbEndpoint usbEndpoint = streamingInterface.getUsbInterface().getEndpoint(0);
        streamingInterface.printEndpoints();
        try {
            IsochronousAsyncTransfer transfer = new IsochronousAsyncTransfer(this, usbEndpoint,
                                                                             connection, buffer, 20);
            int result = connection.isochronousTransfer(this, transfer, usbEndpoint, buffer, 500);
            Timber.d("Submit result: %s", result > 0 ? result : LibusbError.fromNative(result));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onIsochronousTransferComplete(@Nullable ByteBuffer data, int result) {
        Timber.d("Callback result: %s", result > 0 ? result : LibusbError.fromNative(result));
        final byte[] raw = new byte[data.capacity()];
        data.rewind();
        data.get(raw);
        Timber.d("\n%s", Hexdump.dumpHexString(raw));
    }
}
