package com.jwoolston.android.uvc.interfaces.streaming;

import android.support.annotation.NonNull;

import java.util.HashSet;
import java.util.Set;

/**
 * @author Jared Woolston (Jared.Woolston@gmail.com)
 */
public class VideoFormat<T extends VideoFrame> {

    protected int     formatIndex;
    protected int     numberFrames;
    protected int     defaultFrameIndex;
    protected int     aspectRatioX;
    protected int     aspectRatioY;
    protected byte    interlaceFlags;
    protected boolean copyProtect;

    private VideoColorMatchingDescriptor colorMatchingDescriptor;

    protected final Set<T> videoFrames = new HashSet<>();

    VideoFormat(@NonNull byte[] descriptor) throws IllegalArgumentException {

    }

    public void setColorMatchingDescriptor(VideoColorMatchingDescriptor descriptor) {
        colorMatchingDescriptor = descriptor;
    }

    public VideoColorMatchingDescriptor getColorMatchingDescriptor() {
        return colorMatchingDescriptor;
    }

    public int getFormatIndex() {
        return formatIndex;
    }

    public int getNumberFrames() {
        return numberFrames;
    }

    public int getDefaultFrameIndex() {
        return defaultFrameIndex;
    }

    @NonNull
    public Set<T> getVideoFrames() {
        return videoFrames;
    }

    public int getAspectRatioX() {
        return aspectRatioX;
    }

    public int getAspectRatioY() {
        return aspectRatioY;
    }

    public byte getInterlaceFlags() {
        return interlaceFlags;
    }

    public boolean isCopyProtect() {
        return copyProtect;
    }
}
