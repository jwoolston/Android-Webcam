package com.jwoolston.usb.webcam.interfaces.streaming;

/**
 * @author Jared Woolston (jwoolston@idealcorp.com)
 */
public class AVideoFormat {

    protected int     mFormatIndex;
    protected int     mNumberFrames;
    protected int     mDefaultFrameIndex;
    protected int     mAspectRatioX;
    protected int     mAspectRatioY;
    protected byte    mInterlaceFlags;
    protected boolean mCopyProtect;

    private VideoColorMatchingDescriptor mColorMatchingDescriptor;

    AVideoFormat(byte[] descriptor) throws IllegalArgumentException {

    }

    public void setColorMatchingDescriptor(VideoColorMatchingDescriptor descriptor) {
        mColorMatchingDescriptor = descriptor;
    }

    public VideoColorMatchingDescriptor getColorMatchingDescriptor() {
        return mColorMatchingDescriptor;
    }

    public int getFormatIndex() {
        return mFormatIndex;
    }

    public int getNumberFrames() {
        return mNumberFrames;
    }

    public int getDefaultFrameIndex() {
        return mDefaultFrameIndex;
    }

    public int getAspectRatioX() {
        return mAspectRatioX;
    }

    public int getAspectRatioY() {
        return mAspectRatioY;
    }

    public byte getInterlaceFlags() {
        return mInterlaceFlags;
    }

    public boolean isCopyProtect() {
        return mCopyProtect;
    }
}
