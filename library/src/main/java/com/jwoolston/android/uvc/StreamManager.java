package com.jwoolston.android.uvc;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.jwoolston.android.libusb.LibusbError;
import com.jwoolston.android.libusb.UsbDeviceConnection;
import com.jwoolston.android.libusb.async.IsochronousAsyncTransfer;
import com.jwoolston.android.libusb.async.IsochronousTransferCallback;
import com.jwoolston.android.uvc.interfaces.VideoControlInterface;
import com.jwoolston.android.uvc.interfaces.VideoStreamingInterface;
import com.jwoolston.android.uvc.interfaces.endpoints.Endpoint;
import com.jwoolston.android.uvc.interfaces.streaming.VideoFormat;
import com.jwoolston.android.uvc.interfaces.streaming.VideoFrame;
import com.jwoolston.android.uvc.requests.control.RequestErrorCode;
import com.jwoolston.android.uvc.requests.streaming.FramingInfo;
import com.jwoolston.android.uvc.requests.streaming.ProbeControl;
import com.jwoolston.android.uvc.util.Hexdump;
import java.io.IOException;
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

    private final UsbDeviceConnection     connection;
    private final VideoControlInterface   controlInterface;
    private final VideoStreamingInterface streamingInterface;

    public StreamManager(@NonNull UsbDeviceConnection connection, @NonNull VideoControlInterface controlInterface,
                         @NonNull VideoStreamingInterface streamingInterface) {
        this.connection = connection;
        this.controlInterface = controlInterface;
        this.streamingInterface = streamingInterface;
    }

    public void establishStreaming(@Nullable VideoFormat format, @Nullable VideoFrame frame) throws
                                                                                             StreamCreationException {
        final ProbeControl request = ProbeControl.setCurrentProbe(streamingInterface);
        final VideoFormat requestedFormat = format != null ? format : streamingInterface.getAvailableFormats().get(0);
        final VideoFrame requestedFrame = frame != null ? frame : requestedFormat.getDefaultFrame();

        Timber.v("Using video format: %s", format);
        Timber.v("Using video frame: %s", frame);
        request.setFormatIndex(requestedFormat.getFormatIndex());
        request.setFrameIndex(requestedFrame.getFrameIndex());
        request.setFrameInterval(requestedFrame.getDefaultFrameInterval());
        FramingInfo info = new FramingInfo();
        info.setFrameIdRequired(true);
        info.setEndOfFrameAllowed(true);
        request.setFramingInfo(info);

        int retval = connection.controlTransfer(request.getRequestType(), request.getRequest(), request.getValue(),
                                                request.getIndex(), request.getData(), request.getLength(), 500);

        if (retval < 0) {
            throw new StreamCreationException("Probe set request failed: " + LibusbError.fromNative(retval));
        }

        final ProbeControl current = ProbeControl.getCurrentProbe(streamingInterface);
        retval = connection.controlTransfer(current.getRequestType(), current.getRequest(), current.getValue(),
                                            current.getIndex(), current.getData(), current.getLength(), 500);
        if (retval < 0) {
            throw new StreamCreationException("Probe get request failed: " + LibusbError.fromNative(retval));
        }

        int maxPayload = current.getMaxPayloadTransferSize();
        int maxFrameSize = current.getMaxVideoFrameSize();

        final ProbeControl commit = current.getCommit();

        retval = connection.controlTransfer(commit.getRequestType(), commit.getRequest(),
                                            commit.getValue(), commit.getIndex(), commit.getData(), commit.getLength(),
                                            500);
        if (retval < 0) {
            throw new StreamCreationException("Commit request failed: " + LibusbError.fromNative(retval));
        }

        final RequestErrorCode requestErrorCode = RequestErrorCode.getCurrentErrorCode(controlInterface);
        retval = connection.controlTransfer(requestErrorCode.getRequestType(), requestErrorCode.getRequest(),
                                            requestErrorCode.getValue(), requestErrorCode.getIndex(),
                                            requestErrorCode.getData(), requestErrorCode.getLength(), 500);
        if (retval < 0 || requestErrorCode.getData()[0] != 0) {
            throw new StreamCreationException("Error state failed: " + (retval < 0 ? LibusbError.fromNative(retval)
            : "Current error code: 0x" + Hexdump.toHexString(requestErrorCode.getData()[0])));
        }

        Timber.d("Current error code: 0x%s", Hexdump.toHexString(requestErrorCode.getData()[0]));

        initiateStream(maxPayload, maxFrameSize);
    }

    public void initiateStream(int maxPayload, int maxFrameSize) {
        streamingInterface.selectAlternateSetting(connection, 6);
        ByteBuffer buffer = ByteBuffer.allocateDirect(maxPayload);
        Endpoint endpoint = streamingInterface.getCurrentEndpoints()[0];
        try {
            IsochronousAsyncTransfer transfer = new IsochronousAsyncTransfer(this, endpoint.getEndpoint(),
                                                                             connection, 20);
            transfer.submit(buffer, 500);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onIsochronousTransferComplete(@Nullable ByteBuffer data, int result) throws IOException {
        if (result < 0) {
            throw new IOException("Failure in isochronous callback:" + LibusbError.fromNative(result));
        } else {
            final byte[] raw = new byte[data.limit()];
            data.rewind();
            data.get(raw);
            Timber.d(" \n%s", Hexdump.dumpHexString(raw));
            Endpoint endpoint = streamingInterface.getCurrentEndpoints()[0];
            data.rewind();
            IsochronousAsyncTransfer transfer = new IsochronousAsyncTransfer(this, endpoint.getEndpoint(),
                                                                             connection, 20);
            transfer.submit(data, 500);
        }
    }
}
