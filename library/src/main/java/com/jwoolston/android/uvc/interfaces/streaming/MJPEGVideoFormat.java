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
        setFormatIndex((0xFF & descriptor[bFormatIndex]));
        setNumberFrames((0xFF & descriptor[bNumFrameDescriptors]));
        fixedSampleSize = descriptor[bmFlags] != 0;
        setDefaultFrameIndex((0xFF & descriptor[bDefaultFrameIndex]));
        setAspectRatioX((0xFF & descriptor[bAspectRatioX]));
        setAspectRatioY((0xFF & descriptor[bAspectRatioY]));
        setInterlaceFlags(descriptor[bmInterlaceFlags]);
        setIsCopyProtect(descriptor[bCopyProtect] != 0);
    }

    public void addMJPEGVideoFrame(@NonNull MJPEGVideoFrame frame) {
        Timber.d("Adding video frame: %s", frame);
        getVideoFrames().add(frame);
    }

    public boolean getFixedSampleSize() {
        return fixedSampleSize;
    }

    @Override
    public String toString() {
        return "MJPEGVideoFormat{" +
               "formatIndex=" + getFormatIndex() +
               ", numberFrames=" + getNumberFrames() +
               ", fixedSampleSize=" + fixedSampleSize +
               ", defaultFrameIndex=" + getDefaultFrameIndex() +
               ", AspectRatio=" + getAspectRatioX() + ":" + getAspectRatioY() +
               ", interlaceFlags=0x" + Hexdump.toHexString(getInterlaceFlags()) +
               ", copyProtect=" + getIsCopyProtect() +
               '}';
    }
}
