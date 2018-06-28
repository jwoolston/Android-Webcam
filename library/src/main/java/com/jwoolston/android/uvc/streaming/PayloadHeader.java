package com.jwoolston.android.uvc.streaming;

import android.support.annotation.NonNull;
import com.jwoolston.android.uvc.util.ArrayTools;
import java.nio.ByteBuffer;

/**
 * @author Jared Woolston (Jared.Woolston@gmail.com)
 */
public class PayloadHeader {

    private static final int Index_bHeaderLength = 0; // 1 byte
    private static final int Index_bmHeaderInfo  = 1; // 1 byte

    private static final int Mask_FrameID              = 0x01;
    private static final int Mask_EndOfFrame           = 0x02;
    private static final int Mask_PresentationTime     = 0x04;
    private static final int Mask_SourceClockReference = 0x08;
    private static final int Mask_PayloadSpecificBit   = 0x10;
    private static final int Mask_StillImage           = 0x20;
    private static final int Mask_Error                = 0x40;
    private static final int Mask_EndOfHeader          = 0x80;

    private final int     length;
    private final boolean frameId;
    private final boolean endOfFrame;
    private final boolean hasPresentationTime;
    private final boolean hasSourceClockReference;
    private final boolean payloadSpecificBit;
    private final boolean isStillImage;
    private final boolean hasError;
    private final boolean endOfHeader;

    private final int presentationTime;
    private final SourceClockReference sourceClockReference;

    private final ByteBuffer payload;

    public PayloadHeader(@NonNull byte[] payload) {
        length = payload[Index_bHeaderLength];
        frameId = (Mask_FrameID & payload[Index_bmHeaderInfo]) != 0;
        endOfFrame = (Mask_EndOfFrame & payload[Index_bmHeaderInfo]) != 0;
        hasPresentationTime = (Mask_PresentationTime & payload[Index_bmHeaderInfo]) != 0;
        hasSourceClockReference = (Mask_SourceClockReference & payload[Index_bmHeaderInfo]) != 0;
        payloadSpecificBit = (Mask_PayloadSpecificBit & payload[Index_bmHeaderInfo]) != 0;
        isStillImage = (Mask_StillImage & payload[Index_bmHeaderInfo]) != 0;
        hasError = (Mask_Error & payload[Index_bmHeaderInfo]) != 0;
        endOfHeader = (Mask_EndOfHeader & payload[Index_bmHeaderInfo]) != 0;
        this.payload = ByteBuffer.wrap(payload, length, payload.length - length);

        // Get the fields that exist
        int offset = length;
        if (hasPresentationTime) {
            presentationTime = ArrayTools.integerLE(payload, offset);
            offset += 4;
        } else {
            presentationTime = 0;
        }
        if (hasSourceClockReference) {
            sourceClockReference = new SourceClockReference(payload, offset);
            offset += 6;
        } else {
            sourceClockReference = null;
        }
    }

    public int getLength() {
        return length;
    }

    /**
     * For frame-based formats, this bit toggles between 0 and 1 every time a new video frame begins. For
     * stream-based formats, this bit toggles between 0 and 1 at the start of each new codec-specific segment. This
     * behavior is required for frame-based payload formats (e.g., DV) and is optional for stream-based payload
     * formats (e.g., MPEG-2 TS). For stream-based formats, support for this bit must be indicated via the
     * bmFramingInfo field of the Video Probe and Commit controls (see section 4.3.1.1, “Video Probe and Commit
     * Controls”).
     *
     * @return
     */
    public boolean isFrameIdSet() {
        return frameId;
    }

    /**
     * This bit is set if the following payload data marks the end of the current video or still image frame (for
     * frame-based formats), or to indicate the end of a codec-specific segment (for stream-based formats). This
     * behavior is optional for all payload formats. For stream-based formats, support for this bit must be indicated
     * via the bmFramingInfo field of the Video Probe and Commit Controls (see section 4.3.1.1, “Video Probe and
     * Commit Controls”).
     *
     * @return
     */
    public boolean isEndOfFrame() {
        return endOfFrame;
    }

    /**
     * This bit is set if the dwPresentationTime field is being sent as part of the header.
     *
     * @return
     */
    public boolean hasPresentationTime() {
        return hasPresentationTime;
    }

    /**
     * This bit is set if the dwSourceClock field is being sent as part of the header.
     *
     * @return
     */
    public boolean hasSourceClockReference() {
        return hasSourceClockReference;
    }

    /**
     * See individual payload specifications for use.
     *
     * @return
     */
    public boolean payloadSpecificBit() {
        return payloadSpecificBit;
    }

    /**
     * This bit is set if the following data is part of a still image frame, and is only used for methods 2 and 3 of
     * still image capture. For temporally encoded formats, this bit indicates the following data is part of an
     * intra-coded frame.
     *
     * @return
     */
    public boolean isStillImage() {
        return isStillImage;
    }

    /**
     * This bit is set if there was an error in the video or still image transmission for this payload. The Stream
     * Error Code control would reflect the cause of the error.
     *
     * @return
     */
    public boolean hasError() {
        return hasError;
    }

    /**
     * This bit is set if this is the last header group in the packet, where the header group refers to this field
     * and any optional fields identified by the bits in this field (Defined for future extension).
     *
     * @return
     */
    public boolean isEndOfHeader() {
        return endOfHeader;
    }

    /**
     * Presentation Time Stamp (PTS).
     * <p>
     * The source clock time in native device clock units when the raw frame capture begins. This field may be
     * repeated for multiple payload transfers comprising a single video frame, with the restriction that the value
     * shall remain the same throughout that video frame. The PTS is in the same units as specified in the
     * dwClockFrequency field of the Video Probe Control response.
     *
     * @return
     *
     * @throws FieldNotPresentException
     */
    public int getPresentationTime() throws FieldNotPresentException {
        if (!hasPresentationTime) {
            throw new FieldNotPresentException();
        }
        return presentationTime;
    }

    /**
     * A two-part Source Clock Reference (SCR) value
     * <p>
     * D31..D0: Source Time Clock in native device clock units
     * D42..D32: 1KHz SOF token counter
     * D47..D43: Reserved, set to zero.
     * <p>
     * The least-significant 32 bits (D31..D0) contain clock values sampled from the System Time Clock (STC) at the
     * source. The clock resolution shall be according to the dwClockFrequency field of the Probe and Commit response
     * of the device as defined in Table 4-75 of this specification. This value shall comply with the associated
     * stream payload specification.
     * <p>
     * The times at which the STC is sampled must be correlated with the USB Bus Clock. To that end, the next
     * most-significant 11 bits of the SCR (D42..D32) contain a 1 KHz SOF counter, representing the frame number at
     * the time the STC was sampled.
     * <p>
     * - STC must be captured when the first video data of a video frame is put on the USB bus.<br>
     * - SCR must remain constant for all payload transfers within a single video frame.<br>
     * <p>
     * The most-significant 5 bits (D47..D43) are reserved, and must be set to zero.
     * <p>
     * The maximum interval between Payload Headers containing SCR values is 100ms, or the video frame interval,
     * whichever is greater. Shorter intervals are permitted.
     *
     * @return
     *
     * @throws FieldNotPresentException
     */
    @NonNull
    public SourceClockReference getSourceClockReference() throws FieldNotPresentException {
        if (!hasSourceClockReference) {
            throw new FieldNotPresentException();
        }
        return sourceClockReference;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("PayloadHeader{");
        sb.append("length=").append(length);
        sb.append(", frameId=").append(frameId);
        sb.append(", endOfFrame=").append(endOfFrame);
        sb.append(", hasPresentationTime=").append(hasPresentationTime);
        sb.append(", hasSourceClockReference=").append(hasSourceClockReference);
        sb.append(", payloadSpecificBit=").append(payloadSpecificBit);
        sb.append(", isStillImage=").append(isStillImage);
        sb.append(", hasError=").append(hasError);
        sb.append(", endOfHeader=").append(endOfHeader);
        if (hasPresentationTime) {
            sb.append(", presentationTime=").append(presentationTime);
        }
        if (hasSourceClockReference) {
            sb.append(", sourceClockReference=").append(sourceClockReference);
        }
        sb.append('}');
        return sb.toString();
    }
}
