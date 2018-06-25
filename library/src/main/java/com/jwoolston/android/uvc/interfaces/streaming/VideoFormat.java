package com.jwoolston.android.uvc.interfaces.streaming;

/**
 * @author Jared Woolston (Jared.Woolston@gmail.com)
 */
public class VideoFormat {

    protected int     formatIndex;
    protected int     numberFrames;
    protected int     defaultFrameIndex;
    protected int     aspectRatioX;
    protected int     aspectRatioY;
    protected byte    interlaceFlags;
    protected boolean copyProtect;

    private VideoColorMatchingDescriptor mColorMatchingDescriptor;

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
