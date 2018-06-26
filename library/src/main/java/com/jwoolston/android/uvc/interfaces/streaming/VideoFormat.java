package com.jwoolston.android.uvc.interfaces.streaming;

import android.util.SparseArray;

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

    private VideoColorMatchingDescriptor mColorMatchingDescriptor;

    protected SparseArray<T> videoFrames = new SparseArray<>();

    VideoFormat(byte[] descriptor) throws IllegalArgumentException {

    }

    public void setColorMatchingDescriptor(VideoColorMatchingDescriptor descriptor) {
        mColorMatchingDescriptor = descriptor;
    }

    public VideoColorMatchingDescriptor getColorMatchingDescriptor() {
        return mColorMatchingDescriptor;
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

    public T getVideoFrame(int index) {
        return videoFrames.get(index);
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
