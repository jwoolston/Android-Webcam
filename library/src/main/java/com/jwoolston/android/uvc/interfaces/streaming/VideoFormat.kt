package com.jwoolston.android.uvc.interfaces.streaming

import com.jwoolston.android.uvc.streaming.VideoSampleFactory
import java.util.*

/**
 * @author Jared Woolston (Jared.Woolston@gmail.com)
 */
abstract class VideoFormat<T : VideoFrame> @Throws(IllegalArgumentException::class)
internal constructor(descriptor: ByteArray) {

    var formatIndex: Int = 0
        protected set
    var numberFrames: Int = 0
        protected set
    var defaultFrameIndex: Int = 0
        protected set
    var aspectRatioX: Int = 0
        protected set
    var aspectRatioY: Int = 0
        protected set
    var interlaceFlags: Byte = 0
        protected set
    var isCopyProtect: Boolean = false
        protected set

    var colorMatchingDescriptor: VideoColorMatchingDescriptor? = null

    val videoFrames: MutableSet<T> = HashSet()

    val defaultFrame: VideoFrame
        @Throws(IllegalStateException::class)
        get() {
            for (frame in videoFrames) {
                if (frame.frameIndex == defaultFrameIndex) {
                    return frame
                }
            }
            throw IllegalStateException("No default frame was found!")
        }

    abstract val displayName : String

    abstract fun getSampleFactory(maxPayload: Int, maxFrameSize: Int) : VideoSampleFactory
}
