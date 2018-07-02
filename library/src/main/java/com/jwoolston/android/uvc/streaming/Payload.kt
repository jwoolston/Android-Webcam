package com.jwoolston.android.uvc.streaming

import java.io.IOException
import java.nio.BufferUnderflowException
import java.nio.ByteBuffer
import java.nio.ByteOrder

/**
 * @author Jared Woolston (Jared.Woolston@gmail.com)
 */
class Payload @Throws(IOException::class)
constructor(payload: ByteBuffer, packetSize: Int) {

    val length: Int
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
    val isFrameIdSet: Boolean
    /**
     * This bit is set if the following payload data marks the end of the current video or still image frame (for
     * frame-based formats), or to indicate the end of a codec-specific segment (for stream-based formats). This
     * behavior is optional for all payload formats. For stream-based formats, support for this bit must be indicated
     * via the bmFramingInfo field of the Video Probe and Commit Controls (see section 4.3.1.1, “Video Probe and
     * Commit Controls”).
     *
     * @return
     */
    val isEndOfFrame: Boolean
    private val hasPresentationTime: Boolean
    private val hasSourceClockReference: Boolean
    private val payloadSpecificBit: Boolean
    /**
     * This bit is set if the following data is part of a still image frame, and is only used for methods 2 and 3 of
     * still image capture. For temporally encoded formats, this bit indicates the following data is part of an
     * intra-coded frame.
     *
     * @return
     */
    val isStillImage: Boolean
    private val hasError: Boolean
    /**
     * This bit is set if this is the last header group in the packet, where the header group refers to this field
     * and any optional fields identified by the bits in this field (Defined for future extension).
     *
     * @return
     */
    val isEndOfHeader: Boolean

    private val presentationTime: Int
    private val sourceClockReference: SourceClockReference?

    private val payload: ByteBuffer

    init {
        payload.order(ByteOrder.LITTLE_ENDIAN)
        val limit = payload.limit()
        payload.limit(payload.position() + packetSize)
        length = payload.get().toInt()
        isFrameIdSet = Mask_FrameID and payload.get().toInt() != 0
        isEndOfFrame = Mask_EndOfFrame and payload.get().toInt() != 0
        hasPresentationTime = Mask_PresentationTime and payload.get().toInt() != 0
        hasSourceClockReference = Mask_SourceClockReference and payload.get().toInt() != 0
        payloadSpecificBit = Mask_PayloadSpecificBit and payload.get().toInt() != 0
        isStillImage = Mask_StillImage and payload.get().toInt() != 0
        hasError = Mask_Error and payload.get().toInt() != 0
        isEndOfHeader = Mask_EndOfHeader and payload.get().toInt() != 0

        // Get the fields that exist
        if (hasPresentationTime) {
            presentationTime = payload.int
        } else {
            presentationTime = 0
        }
        if (hasSourceClockReference) {
            sourceClockReference = payload.asSourceClockReference
        } else {
            sourceClockReference = null
        }

        this.payload = ByteBuffer.wrap(ByteArray(payload.remaining()))
        try {
            this.payload.put(payload)
            this.payload.flip()
        } catch (e: BufferUnderflowException) {
            throw IOException(e)
        }

        payload.limit(limit)
    }

    fun dumpToBuffer(target: ByteBuffer) {
        target.put(payload)
    }

    /**
     * This bit is set if the dwPresentationTime field is being sent as part of the header.
     *
     * @return
     */
    fun hasPresentationTime(): Boolean {
        return hasPresentationTime
    }

    /**
     * This bit is set if the dwSourceClock field is being sent as part of the header.
     *
     * @return
     */
    fun hasSourceClockReference(): Boolean {
        return hasSourceClockReference
    }

    /**
     * See individual payload specifications for use.
     *
     * @return
     */
    fun payloadSpecificBit(): Boolean {
        return payloadSpecificBit
    }

    /**
     * This bit is set if there was an error in the video or still image transmission for this payload. The Stream
     * Error Code control would reflect the cause of the error.
     *
     * @return
     */
    fun hasError(): Boolean {
        return hasError
    }

    /**
     * Presentation Time Stamp (PTS).
     *
     *
     * The source clock time in native device clock units when the raw frame capture begins. This field may be
     * repeated for multiple payload transfers comprising a single video frame, with the restriction that the value
     * shall remain the same throughout that video frame. The PTS is in the same units as specified in the
     * dwClockFrequency field of the Video Probe Control response.
     *
     * @return
     *
     * @throws FieldNotPresentException
     */
    @Throws(FieldNotPresentException::class)
    fun getPresentationTime(): Int {
        if (!hasPresentationTime) {
            throw FieldNotPresentException()
        }
        return presentationTime
    }

    /**
     * A two-part Source Clock Reference (SCR) value
     *
     *
     * D31..D0: Source Time Clock in native device clock units
     * D42..D32: 1KHz SOF token counter
     * D47..D43: Reserved, set to zero.
     *
     *
     * The least-significant 32 bits (D31..D0) contain clock values sampled from the System Time Clock (STC) at the
     * source. The clock resolution shall be according to the dwClockFrequency field of the Probe and Commit response
     * of the device as defined in Table 4-75 of this specification. This value shall comply with the associated
     * stream payload specification.
     *
     *
     * The times at which the STC is sampled must be correlated with the USB Bus Clock. To that end, the next
     * most-significant 11 bits of the SCR (D42..D32) contain a 1 KHz SOF counter, representing the frame number at
     * the time the STC was sampled.
     *
     *
     * - STC must be captured when the first video data of a video frame is put on the USB bus.<br></br>
     * - SCR must remain constant for all payload transfers within a single video frame.<br></br>
     *
     *
     * The most-significant 5 bits (D47..D43) are reserved, and must be set to zero.
     *
     *
     * The maximum interval between Payload Headers containing SCR values is 100ms, or the video frame interval,
     * whichever is greater. Shorter intervals are permitted.
     *
     * @return
     *
     * @throws FieldNotPresentException
     */
    @Throws(FieldNotPresentException::class)
    fun getSourceClockReference(): SourceClockReference? {
        if (!hasSourceClockReference) {
            throw FieldNotPresentException()
        }
        return sourceClockReference
    }

    override fun toString(): String {
        var retval = "Payload{" +
                "length=" + length +
                ", frameId=" + isFrameIdSet +
                ", endOfFrame=" + isEndOfFrame +
                ", hasPresentationTime=" + hasPresentationTime +
                ", hasSourceClockReference=" + hasSourceClockReference +
                ", payloadSpecificBit=" + payloadSpecificBit +
                ", isStillImage=" + isStillImage +
                ", hasError=" + hasError +
                ", endOfHeader=" + isEndOfHeader
        if (hasPresentationTime) {
            retval += (", presentationTime=" + presentationTime)
        }
        if (hasSourceClockReference) {
            retval += (", sourceClockReference=" + sourceClockReference)
        }
        retval += (", payload=" + payload + '}')
        return retval
    }

    companion object {

        private val Index_bHeaderLength = 0 // 1 byte
        private val Index_bmHeaderInfo = 1 // 1 byte

        private val Mask_FrameID = 0x01
        private val Mask_EndOfFrame = 0x02
        private val Mask_PresentationTime = 0x04
        private val Mask_SourceClockReference = 0x08
        private val Mask_PayloadSpecificBit = 0x10
        private val Mask_StillImage = 0x20
        private val Mask_Error = 0x40
        private val Mask_EndOfHeader = 0x80
    }
}
