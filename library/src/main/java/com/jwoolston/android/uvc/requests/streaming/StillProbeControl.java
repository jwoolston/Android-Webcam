package com.jwoolston.android.uvc.requests.streaming;

import static com.jwoolston.android.uvc.requests.streaming.VSInterfaceControlRequest.ControlSelector.VS_COMMIT_CONTROL;

import android.support.annotation.IntRange;
import android.support.annotation.NonNull;
import android.support.annotation.Size;
import com.jwoolston.android.uvc.interfaces.VideoStreamingInterface;
import com.jwoolston.android.uvc.requests.Request;
import java.nio.ByteBuffer;

/**
 * @author Jared Woolston (Jared.Woolston@gmail.com)
 * @see <a href=http://www.usb.org/developers/docs/devclass_docs/USB_Video_Class_1_5.zip>UVC 1.5 Class
 * Specification §4.3.1.2</a>
 */
public class StillProbeControl extends VSInterfaceControlRequest {

    private static final int LENGTH_COMMIT_DATA = 11;

    private static final int Index_bFormatIndex               = 0; // 1 byte
    private static final int Index_bFrameIndex                = 1; // 1 byte
    private static final int Index_bCompressionIndex          = 2; // 1 bytes
    private static final int Index_dwMaxVideoFrameSize        = 3; // 4 bytes
    private static final int Index_dwMaxPayloadTransferSize   = 7; // 4 bytes

    private final ByteBuffer wrapper;

    @NonNull
    public static StillProbeControl getCurrentCommit(@NonNull VideoStreamingInterface streamingInterface) {
        return new StillProbeControl(Request.GET_CUR, (short) (0xFF & streamingInterface.getInterfaceNumber()),
                                     new byte[LENGTH_COMMIT_DATA]);
    }

    @NonNull
    public static StillProbeControl getMinCommit(@NonNull VideoStreamingInterface streamingInterface) {
        return new StillProbeControl(Request.GET_MIN, (short) (0xFF & streamingInterface.getInterfaceNumber()),
                                     new byte[LENGTH_COMMIT_DATA]);
    }

    @NonNull
    public static StillProbeControl getMaxCommit(@NonNull VideoStreamingInterface streamingInterface) {
        return new StillProbeControl(Request.GET_MAX, (short) (0xFF & streamingInterface.getInterfaceNumber()),
                                     new byte[LENGTH_COMMIT_DATA]);
    }

    @NonNull
    public static StillProbeControl getDefaultCommit(@NonNull VideoStreamingInterface streamingInterface) {
        return new StillProbeControl(Request.GET_DEF, (short) (0xFF & streamingInterface.getInterfaceNumber()),
                                     new byte[LENGTH_COMMIT_DATA]);
    }

    @NonNull
    public static StillProbeControl getLengthCommit(@NonNull VideoStreamingInterface streamingInterface) {
        return new StillProbeControl(Request.GET_LEN, (short) (0xFF & streamingInterface.getInterfaceNumber()),
                                     new byte[LENGTH_COMMIT_DATA]);
    }

    @NonNull
    public static StillProbeControl getInfoCommit(@NonNull VideoStreamingInterface streamingInterface) {
        return new StillProbeControl(Request.GET_INFO, (short) (0xFF & streamingInterface.getInterfaceNumber()),
                                     new byte[LENGTH_COMMIT_DATA]);
    }

    @NonNull
    public static StillProbeControl setCurrentCommit(@NonNull VideoStreamingInterface streamingInterface) {
        return new StillProbeControl(Request.SET_CUR, (short) (0xFF & streamingInterface.getInterfaceNumber()),
                                     new byte[LENGTH_COMMIT_DATA]);
    }

    private StillProbeControl(@NonNull Request request, short index, @NonNull @Size(value = LENGTH_COMMIT_DATA) byte[] data) {
        super(request, VS_COMMIT_CONTROL, index, data);
        wrapper = ByteBuffer.wrap(data);
    }

    public void setFormatIndex(@IntRange(from = 0, to = 7) int index) {
        wrapper.put(Index_bFormatIndex, (byte) (0xFF & index));
    }

    public void setFrameIndex(@IntRange(from = 0, to = 7) int index) {
        wrapper.put(Index_bFrameIndex, (byte) (0xFF & index));
    }

    public void setMaxVideoFrameSize(@IntRange(from = 0) long size) {
        wrapper.putInt(Index_dwMaxVideoFrameSize, (int) size);
    }

    public void setMaxPayloadTransferSize(@IntRange(from =0) long size) {
        wrapper.putInt(Index_dwMaxPayloadTransferSize, (int) size);
    }
}
