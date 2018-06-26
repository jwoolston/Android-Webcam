package com.jwoolston.android.uvc.requests.streaming;

import static com.jwoolston.android.uvc.requests.streaming.VSInterfaceControlRequest.ControlSelector.VS_COMMIT_CONTROL;
import static com.jwoolston.android.uvc.requests.streaming.VSInterfaceControlRequest.ControlSelector.VS_PROBE_CONTROL;

import android.support.annotation.IntRange;
import android.support.annotation.NonNull;
import android.support.annotation.Size;
import com.jwoolston.android.uvc.interfaces.VideoStreamingInterface;
import com.jwoolston.android.uvc.requests.Request;
import java.nio.ByteBuffer;

/**
 * The Probe control allows retrieval and negotiation of streaming parameters.
 *
 * @author Jared Woolston (Jared.Woolston@gmail.com)
 * @see <a href=http://www.usb.org/developers/docs/devclass_docs/USB_Video_Class_1_5.zip>UVC 1.5 Class
 * Specification §4.3.1.1</a>
 */
public class ProbeControl extends VSInterfaceControlRequest {

    private static final int LENGTH_PROBE_DATA = 48;

    private static final int Index_bmHint                     = 0; // 2 bytes
    private static final int Index_bFormatIndex               = 2; // 1 byte
    private static final int Index_bFrameIndex                = 3; // 1 byte
    private static final int Index_dwFrameInterval            = 4; // 4 bytes
    private static final int Index_wKeyFrame_Rate             = 8; // 2 bytes
    private static final int Index_wPFrameRate                = 10; // 2 bytes
    private static final int Index_wCompQuality               = 12; // 2 bytes
    private static final int Index_wCompWindowSize            = 14; // 2 bytes
    private static final int Index_wDelay                     = 16; // 2 bytes
    private static final int Index_dwMaxVideoFrameSize        = 18; // 4 bytes
    private static final int Index_dwMaxPayloadTransferSize   = 22; // 4 bytes
    private static final int Index_dwClockFrequency           = 26; // 4 bytes
    private static final int Index_bmFramingInfo              = 30; // 1 byte
    private static final int Index_bPreferedVersion           = 31; // 1 byte
    private static final int Index_bMinVersion                = 32; // 1 byte
    private static final int Index_bMaxVersion                = 33; // 1 byte
    private static final int Index_bUsage                     = 34; // 1 byte
    private static final int Index_bBitDepthLuma              = 35; // 1 byte
    private static final int Index_bmSettings                 = 36; // 1 byte
    private static final int Index_bMaxNumberOfRefFramesPlus1 = 37; // 1 byte
    private static final int Index_bmRateControlModes         = 38; // 2 bytes
    private static final int Index_bmLayoutPerStream          = 40; // 8 bytes

    private final ByteBuffer wrapper;

    private Hint        hint;
    private FramingInfo framingInfo;

    @NonNull
    public static ProbeControl getCurrentProbe(@NonNull VideoStreamingInterface streamingInterface) {
        return new ProbeControl(Request.GET_CUR, (short) (0xFF & streamingInterface.getInterfaceNumber()), false,
                                new byte[LENGTH_PROBE_DATA]);
    }

    @NonNull
    public static ProbeControl getMinProbe(@NonNull VideoStreamingInterface streamingInterface) {
        return new ProbeControl(Request.GET_MIN, (short) (0xFF & streamingInterface.getInterfaceNumber()), false,
                                new byte[LENGTH_PROBE_DATA]);
    }

    @NonNull
    public static ProbeControl getMaxProbe(@NonNull VideoStreamingInterface streamingInterface) {
        return new ProbeControl(Request.GET_MAX, (short) (0xFF & streamingInterface.getInterfaceNumber()), false,
                                new byte[LENGTH_PROBE_DATA]);
    }

    @NonNull
    public static ProbeControl getResolutionProbe(@NonNull VideoStreamingInterface streamingInterface) {
        return new ProbeControl(Request.GET_RES, (short) (0xFF & streamingInterface.getInterfaceNumber()), false,
                                new byte[LENGTH_PROBE_DATA]);
    }

    @NonNull
    public static ProbeControl getDefaultProbe(@NonNull VideoStreamingInterface streamingInterface) {
        return new ProbeControl(Request.GET_DEF, (short) (0xFF & streamingInterface.getInterfaceNumber()), false,
                                new byte[LENGTH_PROBE_DATA]);
    }

    @NonNull
    public static ProbeControl getLengthProbe(@NonNull VideoStreamingInterface streamingInterface) {
        return new ProbeControl(Request.GET_LEN, (short) (0xFF & streamingInterface.getInterfaceNumber()), false,
                                new byte[LENGTH_PROBE_DATA]);
    }

    @NonNull
    public static ProbeControl getInfoProbe(@NonNull VideoStreamingInterface streamingInterface) {
        return new ProbeControl(Request.GET_INFO, (short) (0xFF & streamingInterface.getInterfaceNumber()), false,
                                new byte[LENGTH_PROBE_DATA]);
    }

    @NonNull
    public static ProbeControl setCurrentProbe(@NonNull VideoStreamingInterface streamingInterface) {
        return new ProbeControl(Request.SET_CUR, (short) (0xFF & streamingInterface.getInterfaceNumber()), false,
                                new byte[LENGTH_PROBE_DATA]);
    }


    private ProbeControl(@NonNull Request request, short index, boolean commit,
                         @NonNull @Size(value = LENGTH_PROBE_DATA) byte[] data) {
        super(request, commit ? VS_COMMIT_CONTROL : VS_PROBE_CONTROL, index, data);
        wrapper = ByteBuffer.wrap(data);
    }

    /**
     * Retrieves a copy of the current Probe parameters configured for submission as a Commit.
     * 
     * @return A new {@link ProbeControl} message structure with the control selector specified as
     * {@link ControlSelector#VS_COMMIT_CONTROL} and the request type being {@link Request#SET_CUR}.
     */
    @NonNull
    public ProbeControl getCommit() {
        return new ProbeControl(Request.SET_CUR, getIndex(), true, wrapper.array());
    }

    /**
     * The hint bitmap indicates to the video streaming interface which fields shall be kept constant during stream
     * parameter negotiation. For example, if the selection wants to favor frame rate over quality, the
     * dwFrameInterval bit will be set (1).
     * <p>
     * This field is set by the host, and is read-only for the video streaming interface.
     *
     * @return
     */
    @NonNull
    public Hint getHint() {
        if (hint == null) {
            hint = new Hint(wrapper.getShort(Index_bmHint));
        }
        return hint;
    }

    /**
     * Video format index from a Format descriptor for this video interface.
     * <p>
     * Select a specific video stream format by setting this field to the one-based index of the associated Format
     * descriptor. To select the first format defined by a device, a value one (1) is written to this field. This
     * field must be supported even if only one video format is supported by the device.
     * <p>
     * This field is set by the host.
     *
     * @return
     */
    public int getFormatIndex() {
        return (0xFF & wrapper.get(Index_bFormatIndex));
    }

    /**
     * Video frame index from a Frame descriptor.
     * <p>
     * This field selects the video frame resolution from the array of resolutions supported by the selected stream.
     * The index value ranges from 1 to the number of Frame descriptors following a particular Format descriptor. For
     * frame-based formats, this field must be supported even if only one video frame index is supported by the device.
     * For video payloads with no defined Frame descriptor, this field shall be set to zero (0).
     * <p>
     * This field is set by the host.
     *
     * @return
     */
    public int getFrameIndex() {
        return (0xFF & wrapper.get(Index_bFrameIndex));
    }

    /**
     * Frame interval in 100 ns units.
     * <p>
     * This field sets the desired video frame interval for the selected video stream and frame index. The frame
     * interval value is specified in 100 ns units. The device shall support the setting of all frame intervals
     * reported in the Frame Descriptor corresponding to the selected Video Frame Index. For frame-based formats,
     * this field must be implemented even if only one video frame interval is supported by the device.
     * <p>
     * When used in conjunction with an IN endpoint, the host shall indicate its preference during the Probe phase.
     * The value must be from the range of values supported by the device.
     * <p>
     * When used in conjunction with an OUT endpoint, the host shall accept the value indicated by the device.
     *
     * @return
     */
    public int getFrameInterval() {
        return wrapper.getInt(Index_dwFrameInterval);
    }

    /**
     * Key frame rate in key-frame per video-frame units.
     * <p>
     * This field is only applicable to sources (and formats) capable of streaming video with adjustable compression
     * parameters. Use of this control is at the discretion of the device, and is indicated in the VS Input or Output
     * Header descriptor.
     * <p>
     * The Key Frame Rate field is used to specify the compressor’s key frame rate. For example, if one of every ten
     * encoded frames in a video stream sequence is a key frame, this control would report a value of 10. A value of
     * 0 indicates that only the first frame is a key frame.
     * <p>
     * When used in conjunction with an IN endpoint, the host shall indicate its preference during the Probe phase.
     * The value must be from the range of values supported by the device.
     * <p>
     * When used in conjunction with an OUT endpoint, the host shall accept the value indicated by the device.
     *
     * @return
     */
    public int getKeyFrameRate() {
        return (0xFFFF & wrapper.getShort(Index_wKeyFrame_Rate));
    }

    /**
     * PFrame rate in PFrame/key frame units.
     * <p>
     * This field is only applicable to sources (and formats) capable of streaming video with adjustable compression
     * parameters. Use of this control is at the discretion of the device, and is indicated in the VS Input or Output
     * Header descriptor.
     * <p>
     * The P Frame Rate Control is used to specify the number of P frames per key frame. As an example of the
     * relationship between the types of encoded frames, suppose a key frame occurs once in every 10 frames, and
     * there are 3 P frames per key frame. The P frames will be spaced evenly between the key frames. The other 6
     * frames, which occur between the key frames and the P frames, will be bi-directional (B) frames.
     * <p>
     * When used in conjunction with an IN endpoint, the host shall indicate its preference during the Probe phase.
     * The value must be from the range of values supported by the device.
     * <p>
     * When used in conjunction with an OUT endpoint, the host shall accept the value indicated by the device.
     *
     * @return
     */
    public int getPFrameRate() {
        return (0xFFFF & wrapper.getShort(Index_wPFrameRate));
    }

    /**
     * Compression quality control in abstract units 1 (lowest) to 10000 (highest).
     * <p>
     * This field is only applicable to sources (and formats) capable of streaming video with adjustable compression
     * parameters. Use of this field is at the discretion of the device, and is indicated in the VS Input or Output
     * Header descriptor.
     * <p>
     * This field is used to specify the quality of the video compression. Values for this property range from 1 to
     * 10000 (1 indicates the lowest quality, 10000 the highest). The resolution reported by this control will
     * determine the number of discrete quality settings that it can support.
     * <p>
     * When used in conjunction with an IN endpoint, the host shall indicate its preference during the Probe phase.
     * The value must be from the range of values supported by the device.
     * <p>
     * When used in conjunction with an OUT endpoint, the host shall accept the value indicated by the device.
     *
     * @return
     */
    public int getCompQuality() {
        return (0xFFFF & wrapper.getShort(Index_wCompQuality));
    }

    /**
     * Window size for average bit rate control.
     * <p>
     * This field is only applicable to sources (and formats) capable of streaming video with adjustable compression
     * parameters. Use of this control is at the discretion of the device, and is indicated in the VS Input or Output
     * Header descriptor.
     * <p>
     * The Compression Window Size Control is used to specify the number of encoded video frames over which the
     * average size cannot exceed the specified data rate. For a window of size n, the average frame size of any
     * consecutive n frames will not exceed the stream's specified data rate. Individual frames can be larger or
     * smaller.
     * <p>
     * For example, if the data rate has been set to 100 kilobytes per second (KBps) on a 10 frames per second (fps)
     * movie with a compression window size of 10, the individual frames can be any size, as long as the average size
     * of a frame in any 10-frame sequence is less than or equal to 10 kilobytes.
     * <p>
     * When used in conjunction with an IN endpoint, the host shall indicate its preference during the Probe phase.
     * The value must be from the range of values supported by the device.
     * <p>
     * When used in conjunction with an OUT endpoint, the host shall accept the value indicated by the device.
     *
     * @return
     */
    public int getCompWindowSize() {
        return (0xFFFF & wrapper.getShort(Index_wCompWindowSize));
    }

    /**
     * Internal video streaming interface latency in ms from video data capture to presentation on the USB.
     * <p>
     * When used in conjunction with an IN endpoint, this field is set by the device and read only from the host.
     * <p>
     * When used in conjunction with an OUT endpoint, this field is set by the host and read only from the device.
     *
     * @return
     */
    public int getDelay() {
        return (0xFFFF & wrapper.getShort(Index_wDelay));
    }

    /**
     * Maximum video frame or codec-specific segment size in bytes.
     * <p>
     * For frame-based formats, this field indicates the maximum size of a single video frame. When streaming
     * simulcast this number reflects the maximum video frame size of the negotiated Frame descriptor. For
     * frame-based formats, this field must be supported.
     * <p>
     * For stream-based formats, and when this behavior is enabled via the bmFramingInfo field (below), this field
     * indicates the maximum size of a single codec-specific segment. The sender is required to indicate a segment
     * boundary via the FID bit in the payload header. This field is ignored (for stream-based formats) if the
     * bmFramingInfo bits are not enabled.
     * <p>
     * When used in conjunction with an IN endpoint, this field is set by the device and read only from the host.
     * When used in conjunction with an OUT endpoint, this field is set by the host and read only from the device.
     *
     * @return
     */
    public int getMaxVideoFrameSize() {
        return wrapper.getInt(Index_dwMaxVideoFrameSize);
    }

    /**
     * Specifies the maximum number of bytes that the device can transmit or receive in a single payload transfer.
     * This field must be supported.
     * <p>
     * This field is set by the device and read only from the host. Some host implementations restrict the maximum
     * value permitted for this field. The host shall avoid overshoot of single payload transfer size by
     * reconfiguring the device. (e.g. by updating bitrates, resolutions etc.)
     *
     * @return
     */
    public int getMaxPayloadTransferSize() {
        return wrapper.getInt(Index_dwMaxPayloadTransferSize);
    }

    /**
     * The device clock frequency in Hz for the specified format. This will specify the units used for the time
     * information fields in the Video Payload Headers in the data stream.
     * <p>
     * This parameter is set by the device and read only from the host.
     *
     * @return
     */
    public int getClockFrequency() {
        return wrapper.getInt(Index_dwClockFrequency);
    }

    /**
     * This control indicates to the function whether payload transfers will contain out-of-band framing information
     * in the Video Payload Header (see section 2.4.3.3, “Video and Still Image Payload Headers”).
     *
     * @return
     */
    @NonNull
    public FramingInfo getFramingInfo() {
        if (framingInfo == null) {
            framingInfo = new FramingInfo(wrapper.get(Index_bmFramingInfo));
        }
        return framingInfo;
    }

    /**
     * The preferred payload format version supported by the host or device for the specified bFormatIndex value.
     * This parameter allows the host and device to negotiate a mutually agreed version of the payload format
     * associated with the bFormatIndex field. The host initializes this and the following bMinVersion and
     * bMaxVersion fields to zero on the first Probe Set. Upon Probe Get, the device shall return its preferred
     * version, plus the minimum and maximum versions supported by the device (see bMinVersion and bMaxVersion below)
     * . The host may issue a subsequent Probe Set/Get sequence to specify its preferred version (within the ranges
     * returned in bMinVersion and bMaxVersion from the initial Probe Set/Get sequence). The host is not permitted to
     * alter the bMinVersion and bMaxVersion values.
     * <p>
     * This field will support up to 256 (1-255) versions of a single payload format. The version number is drawn
     * from the minor version of the Payload Format specification. For example, version 1.2 of a Payload Format
     * specification would result in a value of 2 for this parameter.
     *
     * @return
     */
    public int getPreferedVersion() {
        return (0xFF & wrapper.get(Index_bPreferedVersion));
    }

    /**
     * The minimum payload format version supported by the device for the specified bFormatIndex value.
     * <p>
     * This value is initialized to zero by the host and reset to a value in the range of 1 to 255 by the device. The
     * host is not permitted to modify this value (other than to restart the negotiation by setting
     * bPreferredVersion, bMinVersion and bMaxVersion to zero).
     *
     * @return
     */
    public int getMinVersion() {
        return (0xFF & wrapper.get(Index_bMinVersion));
    }

    /**
     * The maximum payload format version supported by the device for the specified bFormatIndex value.
     * <p>
     * This value is initialized to zero by the host and reset to a value in the range of 1 to 255 by the device. The
     * host is not permitted to modify this value (other than to restart the negotiation by setting
     * bPreferredVersion, bMinVersion and bMaxVersion to zero).
     *
     * @return
     */
    public int getMaxVersion() {
        return (0xFF & wrapper.get(Index_bMaxVersion));
    }

    /**
     * This bitmap enables features reported by the bmUsages field of the Video Frame Descriptor.
     * <p>
     * For temporally encoded video formats, this field must be supported, even if the device only supports a single
     * value for bUsage.
     *
     * @return
     */
    @NonNull
    public Usage getUsage() {
        return Usage.fromValue(0xFF & wrapper.get(Index_bUsage));
    }

    /**
     * Represents bit_depth_luma_minus8 + 8, which must be the same as bit_depth_chroma_minus8 + 8.
     *
     * @return
     */
    public int getBitDepthLuma() {
        return (0xFF & wrapper.get(Index_bBitDepthLuma));
    }

    /**
     * A bitmap of flags that is used to discover and control specific features of a temporally encoded video stream.
     * <p>
     * When it is supported, it is defined in the associated Payload specification. This bitmap enables features
     * reported by the bmCapabilities field of the Video Frame Descriptor.
     * <p>
     * For temporally encoded video formats, this field must be supported.
     *
     * @return
     */
    public byte getSettings() {
        return wrapper.get(Index_bmSettings);
    }

    /**
     * Host indicates the maximum number of frames stored for use as references.
     *
     * @return
     */
    public int getMaxNumberOfRefFramesPlus1() {
        return (0xFF & wrapper.get(Index_bMaxNumberOfRefFramesPlus1));
    }

    /**
     * This field contains 4 subfields, each of which is a 4 bit number.
     * <p>
     * It enables features reported by the bmSupportedRateControlModes field of the Video Format Descriptor.
     * Each 4 bit number indicates the rate-control mode for a stream of encoded video. If the video payload does not
     * support rate control, this entire field should be set to 0.
     * <p>
     * bmRateControlModes supports up to four simulcast streams. For simulcast transport the number of streams is
     * inferred from the bmLayoutPerStream field. Otherwise, the number of streams is 1.
     * <p>
     * D3-D0: Rate-control mode for the first simulcast stream (with stream_id=0.)<br>
     * D7-D4: Rate-control mode for the second simulcast stream (with stream_id=1).<br>
     * D11-D8: Rate control mode for the third simulcast stream (with stream_id=2).<br>
     * D15-D12: Rate control mode for the fourth simulcast stream (with stream_id=3.)<br>
     * <p>
     * When bmRateControlModes is non-zero, each 4-bit subfield can take one of the following values:
     * <p>
     * 0: Not applicable, because this stream is non-existent.<br>
     * 1: VBR with underflow allowed<br>
     * 2: CBR<br>
     * 3: Constant QP<br>
     * 4: Global VBR, underflow allowed<br>
     * 5: VBR without underflow<br>
     * 6: Global VBR without underflow<br>
     * 7-15: Reserved<br>
     * <p>
     * For temporally encoded video formats, this field must be supported, even if the device only supports a single
     * value for bmRateControlModes.
     *
     * @return
     */
    @NonNull @Size(value = 4)
    public int[] getRateControlModes() {
        final byte high = wrapper.get(Index_bmRateControlModes);
        final byte low = wrapper.get(Index_bmRateControlModes + 1);
        final int[] retval = new int[4];
        retval[0] = 0xF & low;
        retval[1] = (0xF0 & low) >> 4;
        retval[2] = 0xF & high;
        retval[3] = (0xF0 & high) >> 4;
        return retval;
    }

    /**
     * This field contains 4 subfields, each of which is a 2 byte number.
     * <p>
     * For simulcast transport, this field indicates the specific layering structure for each stream, up to four
     * simulcast streams. For a single, multi-layer stream, only the first two bytes are used. For a single stream
     * with no enhancement layers, this field shall be set to 0. See individual payload specification for how to
     * interpret each 2 byte sub-field.
     * <p>
     * For temporally encoded video formats, this field must be supported.
     *
     * @return
     */
    public long getLayoutPerStream() {
        return wrapper.getLong(Index_bmLayoutPerStream);
    }

    /**
     * The hint bitmap indicates to the video streaming interface which fields shall be kept constant during stream
     * parameter negotiation. For example, if the selection wants to favor frame rate over quality, the
     * dwFrameInterval bit will be set (1).
     * <p>
     * This field is set by the host, and is read-only for the video streaming interface.
     *
     * @param hint
     */
    @NonNull
    public void setHint(@NonNull Hint hint) {
        this.hint = hint;
        wrapper.putShort(Index_bmHint, hint.getRaw());
    }

    /**
     * Video format index from a Format descriptor for this video interface.
     * <p>
     * Select a specific video stream format by setting this field to the one-based index of the associated Format
     * descriptor. To select the first format defined by a device, a value one (1) is written to this field. This
     * field must be supported even if only one video format is supported by the device.
     * <p>
     * This field is set by the host.
     *
     * @param index
     */
    public void setFormatIndex(@IntRange(from = 0, to = 7) int index) {
        wrapper.put(Index_bFormatIndex, (byte) (0xFF & index));
    }

    /**
     * Video frame index from a Frame descriptor.
     * <p>
     * This field selects the video frame resolution from the array of resolutions supported by the selected stream.
     * The index value ranges from 1 to the number of Frame descriptors following a particular Format descriptor. For
     * frame-based formats, this field must be supported even if only one video frame index is supported by the device.
     * For video payloads with no defined Frame descriptor, this field shall be set to zero (0).
     * <p>
     * This field is set by the host.
     *
     * @param index
     */
    public void setFrameIndex(@IntRange(from = 0, to = 7) int index) {
        wrapper.put(Index_bFrameIndex, (byte) (0xFF & index));
    }

    /**
     * Frame interval in 100 ns units.
     * <p>
     * This field sets the desired video frame interval for the selected video stream and frame index. The frame
     * interval value is specified in 100 ns units. The device shall support the setting of all frame intervals
     * reported in the Frame Descriptor corresponding to the selected Video Frame Index. For frame-based formats,
     * this field must be implemented even if only one video frame interval is supported by the device.
     * <p>
     * When used in conjunction with an IN endpoint, the host shall indicate its preference during the Probe phase.
     * The value must be from the range of values supported by the device.
     * <p>
     * When used in conjunction with an OUT endpoint, the host shall accept the value indicated by the device.
     *
     * @param interval
     */
    public void setFrameInterval(@IntRange(from = 0) long interval) {
        wrapper.putInt(Index_dwFrameInterval, (int) interval);
    }

    /**
     * Key frame rate in key-frame per video-frame units.
     * <p>
     * This field is only applicable to sources (and formats) capable of streaming video with adjustable compression
     * parameters. Use of this control is at the discretion of the device, and is indicated in the VS Input or Output
     * Header descriptor.
     * <p>
     * The Key Frame Rate field is used to specify the compressor’s key frame rate. For example, if one of every ten
     * encoded frames in a video stream sequence is a key frame, this control would report a value of 10. A value of
     * 0 indicates that only the first frame is a key frame.
     * <p>
     * When used in conjunction with an IN endpoint, the host shall indicate its preference during the Probe phase.
     * The value must be from the range of values supported by the device.
     * <p>
     * When used in conjunction with an OUT endpoint, the host shall accept the value indicated by the device.
     *
     * @param rate
     */
    public void setKeyFrameRate(@IntRange(from = 0, to = 65536) int rate) {
        wrapper.putShort(Index_wKeyFrame_Rate, (short) (0xFFFF & rate));
    }

    /**
     * PFrame rate in PFrame/key frame units.
     * <p>
     * This field is only applicable to sources (and formats) capable of streaming video with adjustable compression
     * parameters. Use of this control is at the discretion of the device, and is indicated in the VS Input or Output
     * Header descriptor.
     * <p>
     * The P Frame Rate Control is used to specify the number of P frames per key frame. As an example of the
     * relationship between the types of encoded frames, suppose a key frame occurs once in every 10 frames, and
     * there are 3 P frames per key frame. The P frames will be spaced evenly between the key frames. The other 6
     * frames, which occur between the key frames and the P frames, will be bi-directional (B) frames.
     * <p>
     * When used in conjunction with an IN endpoint, the host shall indicate its preference during the Probe phase.
     * The value must be from the range of values supported by the device.
     * <p>
     * When used in conjunction with an OUT endpoint, the host shall accept the value indicated by the device.
     *
     * @param rate
     */
    public void setPFrameRate(@IntRange(from = 0, to = 65536) int rate) {
        wrapper.putShort(Index_wPFrameRate, (short) (0xFFFF & rate));
    }

    /**
     * Compression quality control in abstract units 1 (lowest) to 10000 (highest).
     * <p>
     * This field is only applicable to sources (and formats) capable of streaming video with adjustable compression
     * parameters. Use of this field is at the discretion of the device, and is indicated in the VS Input or Output
     * Header descriptor.
     * <p>
     * This field is used to specify the quality of the video compression. Values for this property range from 1 to
     * 10000 (1 indicates the lowest quality, 10000 the highest). The resolution reported by this control will
     * determine the number of discrete quality settings that it can support.
     * <p>
     * When used in conjunction with an IN endpoint, the host shall indicate its preference during the Probe phase.
     * The value must be from the range of values supported by the device.
     * <p>
     * When used in conjunction with an OUT endpoint, the host shall accept the value indicated by the device.
     *
     * @param quality
     */
    public void setCompQuality(@IntRange(from = 0, to = 65536) int quality) {
        wrapper.putShort(Index_wCompQuality, (short) (0xFFFF & quality));
    }

    /**
     * Window size for average bit rate control.
     * <p>
     * This field is only applicable to sources (and formats) capable of streaming video with adjustable compression
     * parameters. Use of this control is at the discretion of the device, and is indicated in the VS Input or Output
     * Header descriptor.
     * <p>
     * The Compression Window Size Control is used to specify the number of encoded video frames over which the
     * average size cannot exceed the specified data rate. For a window of size n, the average frame size of any
     * consecutive n frames will not exceed the stream's specified data rate. Individual frames can be larger or
     * smaller.
     * <p>
     * For example, if the data rate has been set to 100 kilobytes per second (KBps) on a 10 frames per second (fps)
     * movie with a compression window size of 10, the individual frames can be any size, as long as the average size
     * of a frame in any 10-frame sequence is less than or equal to 10 kilobytes.
     * <p>
     * When used in conjunction with an IN endpoint, the host shall indicate its preference during the Probe phase.
     * The value must be from the range of values supported by the device.
     * <p>
     * When used in conjunction with an OUT endpoint, the host shall accept the value indicated by the device.
     *
     * @param windowSize
     */
    public void setCompWindowSize(@IntRange(from = 0, to = 65536) int windowSize) {
        wrapper.putShort(Index_wCompWindowSize, (short) (0xFFFF & windowSize));
    }

    /**
     * Internal video streaming interface latency in ms from video data capture to presentation on the USB.
     * <p>
     * When used in conjunction with an IN endpoint, this field is set by the device and read only from the host.
     * <p>
     * When used in conjunction with an OUT endpoint, this field is set by the host and read only from the device.
     *
     * @param delay
     */
    public void setDelay(@IntRange(from = 0, to = 65536) int delay) {
        wrapper.putShort(Index_wDelay, (short) (0xFFFF & delay));
    }

    /**
     * Maximum video frame or codec-specific segment size in bytes.
     * <p>
     * For frame-based formats, this field indicates the maximum size of a single video frame. When streaming
     * simulcast this number reflects the maximum video frame size of the negotiated Frame descriptor. For
     * frame-based formats, this field must be supported.
     * <p>
     * For stream-based formats, and when this behavior is enabled via the bmFramingInfo field (below), this field
     * indicates the maximum size of a single codec-specific segment. The sender is required to indicate a segment
     * boundary via the FID bit in the payload header. This field is ignored (for stream-based formats) if the
     * bmFramingInfo bits are not enabled.
     * <p>
     * When used in conjunction with an IN endpoint, this field is set by the device and read only from the host.
     * When used in conjunction with an OUT endpoint, this field is set by the host and read only from the device.
     *
     * @param size
     */
    public void setMaxVideoFrameSize(@IntRange(from = 0) long size) {
        wrapper.putInt(Index_dwMaxVideoFrameSize, (int) size);
    }

    /**
     * Specifies the maximum number of bytes that the device can transmit or receive in a single payload transfer.
     * This field must be supported.
     * <p>
     * This field is set by the device and read only from the host. Some host implementations restrict the maximum
     * value permitted for this field. The host shall avoid overshoot of single payload transfer size by
     * reconfiguring the device. (e.g. by updating bitrates, resolutions etc.)
     *
     * @param size
     */
    public void setMaxPayloadTransferSize(@IntRange(from = 0) long size) {
        wrapper.putInt(Index_dwMaxPayloadTransferSize, (int) size);
    }

    /**
     * The device clock frequency in Hz for the specified format. This will specify the units used for the time
     * information fields in the Video Payload Headers in the data stream.
     * <p>
     * This parameter is set by the device and read only from the host.
     *
     * @param frequency
     */
    public void setClockFrequency(@IntRange(from = 0) long frequency) {
        wrapper.putInt(Index_dwClockFrequency, (int) frequency);
    }

    /**
     * This control indicates to the function whether payload transfers will contain out-of-band framing information
     * in the Video Payload Header (see section 2.4.3.3, “Video and Still Image Payload Headers”).
     *
     * @param info
     */
    public void setFramingInfo(@NonNull FramingInfo info) {
        framingInfo = info;
        wrapper.put(Index_bmFramingInfo, info.getRaw());
    }

    /**
     * The preferred payload format version supported by the host or device for the specified bFormatIndex value.
     * This parameter allows the host and device to negotiate a mutually agreed version of the payload format
     * associated with the bFormatIndex field. The host initializes this and the following bMinVersion and
     * bMaxVersion fields to zero on the first Probe Set. Upon Probe Get, the device shall return its preferred
     * version, plus the minimum and maximum versions supported by the device (see bMinVersion and bMaxVersion below)
     * . The host may issue a subsequent Probe Set/Get sequence to specify its preferred version (within the ranges
     * returned in bMinVersion and bMaxVersion from the initial Probe Set/Get sequence). The host is not permitted to
     * alter the bMinVersion and bMaxVersion values.
     * <p>
     * This field will support up to 256 (1-255) versions of a single payload format. The version number is drawn
     * from the minor version of the Payload Format specification. For example, version 1.2 of a Payload Format
     * specification would result in a value of 2 for this parameter.
     *
     * @param version
     */
    public void setPreferedVersion(@IntRange(from = 0, to = 7) int version) {
        wrapper.put(Index_bPreferedVersion, (byte) (0xFF & version));
    }

    /**
     * The minimum payload format version supported by the device for the specified bFormatIndex value.
     * <p>
     * This value is initialized to zero by the host and reset to a value in the range of 1 to 255 by the device. The
     * host is not permitted to modify this value (other than to restart the negotiation by setting
     * bPreferredVersion, bMinVersion and bMaxVersion to zero).
     *
     * @param version
     */
    public void setMinVersion(@IntRange(from = 0, to = 7) int version) {
        wrapper.put(Index_bMinVersion, (byte) (0xFF & version));
    }

    /**
     * The maximum payload format version supported by the device for the specified bFormatIndex value.
     * <p>
     * This value is initialized to zero by the host and reset to a value in the range of 1 to 255 by the device. The
     * host is not permitted to modify this value (other than to restart the negotiation by setting
     * bPreferredVersion, bMinVersion and bMaxVersion to zero).
     *
     * @param version
     */
    public void setMaxVersion(@IntRange(from = 0, to = 7) int version) {
        wrapper.put(Index_bMaxVersion, (byte) (0xFF & version));
    }

    /**
     * This bitmap enables features reported by the bmUsages field of the Video Frame Descriptor.
     * <p>
     * For temporally encoded video formats, this field must be supported, even if the device only supports a single
     * value for bUsage.
     *
     * @param usage
     */
    public void setUsage(@NonNull Usage usage) {
        wrapper.put(Index_bUsage, (byte) (0xFF & usage.getValue()));
    }

    /**
     * Represents bit_depth_luma_minus8 + 8, which must be the same as bit_depth_chroma_minus8 + 8.
     *
     * @param depth
     */
    public void setBitDepthLuma(@IntRange(from = 0, to = 7) int depth) {
        wrapper.put(Index_bBitDepthLuma, (byte) (0xFF & depth));
    }

    /**
     * A bitmap of flags that is used to discover and control specific features of a temporally encoded video stream.
     * <p>
     * When it is supported, it is defined in the associated Payload specification. This bitmap enables features
     * reported by the bmCapabilities field of the Video Frame Descriptor.
     * <p>
     * For temporally encoded video formats, this field must be supported.
     *
     * @param settings
     */
    public void setSettings(byte settings) {
        wrapper.put(Index_bmSettings, settings);
    }

    /**
     * Host indicates the maximum number of frames stored for use as references.
     *
     * @param number
     */
    public void setMaxNumberOfRefFramesPlus1(@IntRange(from = 0, to = 7) int number) {
        wrapper.put(Index_bMaxNumberOfRefFramesPlus1, (byte) (0xFF & number));
    }

    /**
     * This field contains 4 subfields, each of which is a 4 bit number.
     * <p>
     * It enables features reported by the bmSupportedRateControlModes field of the Video Format Descriptor.
     * Each 4 bit number indicates the rate-control mode for a stream of encoded video. If the video payload does not
     * support rate control, this entire field should be set to 0.
     * <p>
     * bmRateControlModes supports up to four simulcast streams. For simulcast transport the number of streams is
     * inferred from the bmLayoutPerStream field. Otherwise, the number of streams is 1.
     * <p>
     * D3-D0: Rate-control mode for the first simulcast stream (with stream_id=0.)<br>
     * D7-D4: Rate-control mode for the second simulcast stream (with stream_id=1).<br>
     * D11-D8: Rate control mode for the third simulcast stream (with stream_id=2).<br>
     * D15-D12: Rate control mode for the fourth simulcast stream (with stream_id=3.)<br>
     * <p>
     * When bmRateControlModes is non-zero, each 4-bit subfield can take one of the following values:
     * <p>
     * 0: Not applicable, because this stream is non-existent.<br>
     * 1: VBR with underflow allowed<br>
     * 2: CBR<br>
     * 3: Constant QP<br>
     * 4: Global VBR, underflow allowed<br>
     * 5: VBR without underflow<br>
     * 6: Global VBR without underflow<br>
     * 7-15: Reserved<br>
     * <p>
     * For temporally encoded video formats, this field must be supported, even if the device only supports a single
     * value for bmRateControlModes.
     *
     * @param controlModes
     */
    public void setRateControlModes(@Size(4) int[] controlModes) {
        byte high = (byte) ((0xF & controlModes[2]) | (controlModes[3] << 4));
        byte low = (byte) ((0xF & controlModes[0]) | (controlModes[1] << 4));
        wrapper.put(Index_bmRateControlModes, high);
        wrapper.put(Index_bmRateControlModes + 1, low);
    }

    /**
     * This field contains 4 subfields, each of which is a 2 byte number.
     * <p>
     * For simulcast transport, this field indicates the specific layering structure for each stream, up to four
     * simulcast streams. For a single, multi-layer stream, only the first two bytes are used. For a single stream
     * with no enhancement layers, this field shall be set to 0. See individual payload specification for how to
     * interpret each 2 byte sub-field.
     * <p>
     * For temporally encoded video formats, this field must be supported.
     *
     * @param layout
     */
    public void setLayoutPerStream(long layout) {
        wrapper.putLong(Index_bmLayoutPerStream, layout);
    }
}
