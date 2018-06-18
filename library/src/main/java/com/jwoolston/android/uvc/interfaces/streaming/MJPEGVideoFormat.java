package com.jwoolston.android.uvc.interfaces.streaming;

import android.util.SparseArray;

import com.jwoolston.android.uvc.util.Hexdump;

import timber.log.Timber;

/**
 * @author Jared Woolston (Jared.Woolston@gmail.com)
 */
public class MJPEGVideoFormat extends AVideoFormat {

    private static final String TAG = "MJPEGVideoFormat";

    private static final int LENGTH = 11;

    private static final int bFormatIndex = 3;
    private static final int bNumFrameDescriptors = 4;
    private static final int bmFlags = 5;
    private static final int bDefaultFrameIndex = 6;
    private static final int bAspectRatioX = 7;
    private static final int bAspectRatioY = 8;
    private static final int bmInterlaceFlags = 9;
    private static final int bCopyProtect = 10;

    private final boolean mFixedSampleSize;
    private final SparseArray<MJPEGVideoFrame> mVideoFrames;

    public MJPEGVideoFormat(byte[] descriptor) throws IllegalArgumentException {
        super(descriptor);
        if (descriptor.length < LENGTH) throw new IllegalArgumentException("The provided discriptor is not long enough for an MJPEG Video Format.");
        mVideoFrames = new SparseArray<>();
        mFormatIndex = (0xFF & descriptor[bFormatIndex]);
        mNumberFrames = (0xFF & descriptor[bNumFrameDescriptors]);
        mFixedSampleSize = descriptor[bmFlags] != 0;
        mDefaultFrameIndex = (0xFF & descriptor[bDefaultFrameIndex]);
        mAspectRatioX = (0xFF & descriptor[bAspectRatioX]);
        mAspectRatioY = (0xFF & descriptor[bAspectRatioY]);
        mInterlaceFlags = descriptor[bmInterlaceFlags];
        mCopyProtect = descriptor[bCopyProtect] != 0;
    }

    public void addMJPEGVideoFrame(MJPEGVideoFrame frame) {
        Timber.d("Adding video frame: %s", frame);
        mVideoFrames.put(frame.getFrameIndex(), frame);
    }

    public boolean getFixedSampleSize() {
        return mFixedSampleSize;
    }

    @Override
    public String toString() {
        return "MJPEGVideoFormat{" +
                "mFormatIndex=" + mFormatIndex +
                ", mNumberFrames=" + mNumberFrames +
                ", mFixedSampleSize=" + mFixedSampleSize +
                ", mDefaultFrameIndex=" + mDefaultFrameIndex +
                ", AspectRatio=" + mAspectRatioX + ":" + mAspectRatioY +
                ", mInterlaceFlags=0x" + Hexdump.toHexString(mInterlaceFlags) +
                ", mCopyProtect=" + mCopyProtect +
                '}';
    }
}
