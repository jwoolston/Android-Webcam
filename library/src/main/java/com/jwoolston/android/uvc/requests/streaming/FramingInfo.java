package com.jwoolston.android.uvc.requests.streaming;

import java.util.BitSet;

/**
 * Bitfield control supporting the following values:<br>
 * - <b>D0: If set to 1, the Frame ID (FID) field is required in the Payload Header (see description of D0 in section
 * 2.4.3.3, “Video and Still Image Payload Headers”). The sender is required to toggle the Frame ID at least every
 * dwMaxVideoFrameSize bytes.</b><br>
 * - <b>D1: If set to 1, indicates that the End of Frame (EOF) field may be present in the Payload Header (see
 * description of D1 in section 2.4.3.3, “Video and Still Image Payload Headers”). It is an error to specify this bit
 * without also specifying D0.</b><br>
 * - <b>D2: If set to 1, indicates that the End of Slice (EOS) field may be present in the Payload Header. It is an
 * error to specify this bit without also specifying D0.</b><br>
 * - <b>D7..3: Reserved (0)</b>
 * <p>
 * This control indicates to the function whether payload transfers will contain out-of-band framing information in
 * the Video Payload Header (see section 2.4.3.3, “Video and Still Image Payload Headers”).
 * For known frame-based formats (e.g., MJPEG, Uncompressed, DV), this field is ignored.
 * For known stream-based formats, this field allows the sender to indicate that it will identify segment boundaries
 * in the stream, enabling low-latency buffer handling by the receiver without the overhead of parsing the stream
 * itself.
 * When used in conjunction with an IN endpoint, this control is set by the device, and is read-only from the host.
 * When used in conjunction with an OUT endpoint, this parameter is set by the host, and is read-only from the device.
 * </p>
 */
public final class FramingInfo {

    private final int Index_frameIdRequired = 0;
    private final int Index_endOfFrameAllowed   = 1;
    private final int Index_endOfSliceAllowed     = 2;

    private final BitSet bitSet = new BitSet(8);

    public FramingInfo() {

    }

    public FramingInfo(byte raw) {
        for (int i = 0; i < 3; ++i) {
            bitSet.set(i, (raw & (0x01 << i)) == 1);
        }
    }

    byte getRaw() {
        byte value = 0;
        for (int i = 0; i < 3; ++i) {
            value |= ((bitSet.get(i) ? 0x1 : 0x0) << i);
        }
        return value;
    }

    public boolean getFrameIdRequired() {
        return bitSet.get(Index_frameIdRequired);
    }

    public boolean getKeyFrameRate() {
        return bitSet.get(Index_endOfFrameAllowed);
    }

    public boolean getPFrameRate() {
        return bitSet.get(Index_endOfSliceAllowed);
    }


    public void setFrameInterval(boolean state) {
        bitSet.set(Index_frameIdRequired, state);
    }

    public void setKeyFrameRate(boolean state) {
        if (state) {
            bitSet.set(Index_frameIdRequired, true);
        }
        bitSet.set(Index_endOfFrameAllowed, state);
    }

    public void setPFrameRate(boolean state) {
        if (state) {
            bitSet.set(Index_frameIdRequired, true);
        }
        bitSet.set(Index_endOfSliceAllowed, state);
    }
}
