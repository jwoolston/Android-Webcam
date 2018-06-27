package com.jwoolston.android.uvc.interfaces.streaming;

import android.support.annotation.NonNull;
import com.jwoolston.android.uvc.util.Hexdump;

import timber.log.Timber;

/**
 * @author Jared Woolston (Jared.Woolston@gmail.com)
 */
public class MJPEGVideoFormat extends VideoFormat<MJPEGVideoFrame> {

    private static final int LENGTH = 11;

    private static final int bFormatIndex = 3;
    private static final int bNumFrameDescriptors = 4;
    private static final int bmFlags = 5;
    private static final int bDefaultFrameIndex = 6;
    private static final int bAspectRatioX = 7;
    private static final int bAspectRatioY = 8;
    private static final int bmInterlaceFlags = 9;
    private static final int bCopyProtect = 10;

    private final boolean fixedSampleSize;

    public MJPEGVideoFormat(byte[] descriptor) throws IllegalArgumentException {
        super(descriptor);
        if (descriptor.length < LENGTH) {
            throw new IllegalArgumentException("The provided descriptor is not long enough for an MJPEG Video Format.");
        }
        formatIndex = (0xFF & descriptor[bFormatIndex]);
        numberFrames = (0xFF & descriptor[bNumFrameDescriptors]);
        fixedSampleSize = descriptor[bmFlags] != 0;
        defaultFrameIndex = (0xFF & descriptor[bDefaultFrameIndex]);
        aspectRatioX = (0xFF & descriptor[bAspectRatioX]);
        aspectRatioY = (0xFF & descriptor[bAspectRatioY]);
        interlaceFlags = descriptor[bmInterlaceFlags];
        copyProtect = descriptor[bCopyProtect] != 0;
    }

    public void addMJPEGVideoFrame(@NonNull MJPEGVideoFrame frame) {
        Timber.d("Adding video frame: %s", frame);
        videoFrames.add(frame);
    }

    public boolean getFixedSampleSize() {
        return fixedSampleSize;
    }

    @Override
    public String toString() {
        return "MJPEGVideoFormat{" +
               "formatIndex=" + formatIndex +
               ", numberFrames=" + numberFrames +
               ", fixedSampleSize=" + fixedSampleSize +
               ", defaultFrameIndex=" + defaultFrameIndex +
               ", AspectRatio=" + aspectRatioX + ":" + aspectRatioY +
               ", interlaceFlags=0x" + Hexdump.toHexString(interlaceFlags) +
               ", copyProtect=" + copyProtect +
               '}';
    }
}
