package com.jwoolston.usb.webcam.interfaces.streaming;

import android.util.Log;
import android.util.SparseArray;

import com.jwoolston.usb.webcam.util.Hexdump;

import java.util.Arrays;

/**
 * @author Jared Woolston (jwoolston@idealcorp.com)
 * @see UVC USB_Video_Payload_Uncompressed.pdf v1.5 Table 3-1
 */
public class UncompressedVideoFormat extends AVideoFormat {

    private static final String TAG = "UncompressedVideoFormat";

    private static final int LENGTH = 27;

    //|-----------------------------------------------|
    //| Format | GUID                                 |
    //|-----------------------------------------------|
    //| YUY2   | 32595559-0000-0010-8000-00AA00389B71 |
    //| NV12   | 3231564E-0000-0010-8000-00AA00389B71 |
    //| M420   | 3032344D-0000-0010-8000-00AA00389B71 |
    //| I420   | 30323449-0000-0010-8000-00AA00389B71 |
    //|-----------------------------------------------|

    private static final String YUY2_GUID = "32595559-0000-0010-8000-00AA00389B71";
    private static final String NV12_GUID = "3231564E-0000-0010-8000-00AA00389B71";
    private static final String M420_GUID = "3032344D-0000-0010-8000-00AA00389B71";
    private static final String I420_GUID = "30323449-0000-0010-8000-00AA00389B71";

    private static final int bFormatIndex         = 3;
    private static final int bNumFrameDescriptors = 4;
    private static final int guidFormat           = 5;
    private static final int bBitsPerPixel        = 21;
    private static final int bDefaultFrameIndex   = 22;
    private static final int bAspectRatioX        = 23;
    private static final int bAspectRatioY        = 24;
    private static final int bmInterlaceFlags     = 25;
    private static final int bCopyProtect         = 26;

    private final int     mFormatIndex;
    private final int     mNumberFrames;
    private final String  mGUID;
    private final int     mBitsPerPixel;
    private final int     mDefaultFrameIndex;
    private final int     mAspectRatioX;
    private final int     mAspectRatioY;
    private final byte    mInterlaceFlags;
    private final boolean mCopyProtect;

    private final SparseArray<UncompressedVideoFrame> mVideoFrames;

    public UncompressedVideoFormat(byte[] descriptor) throws IllegalArgumentException {
        super(descriptor);
        if (descriptor.length < LENGTH) throw new IllegalArgumentException("The provided discriptor is not long enough for an Uncompressed Video Format.");
        mVideoFrames = new SparseArray<>();
        mFormatIndex = (0xFF & descriptor[bFormatIndex]);
        mNumberFrames = (0xFF & descriptor[bNumFrameDescriptors]);
        byte[] GUIDBytes = new byte[16];
        System.arraycopy(descriptor, guidFormat, GUIDBytes, 0, GUIDBytes.length);
        mBitsPerPixel = (0xFF & descriptor[bBitsPerPixel]);
        mDefaultFrameIndex = (0xFF & descriptor[bDefaultFrameIndex]);
        mAspectRatioX = (0xFF & descriptor[bAspectRatioX]);
        mAspectRatioY = (0xFF & descriptor[bAspectRatioY]);
        mInterlaceFlags = descriptor[bmInterlaceFlags];
        mCopyProtect = descriptor[bCopyProtect] != 0;

        // Parse the GUID bytes to String
        final StringBuilder builder = new StringBuilder();
        builder.append(Hexdump.toHexString(GUIDBytes[3])).append(Hexdump.toHexString(GUIDBytes[2])).append(Hexdump.toHexString(GUIDBytes[1])).append(Hexdump.toHexString(GUIDBytes[0]));
        builder.append('-').append(Hexdump.toHexString(GUIDBytes[5])).append(Hexdump.toHexString(GUIDBytes[4]));
        builder.append('-').append(Hexdump.toHexString(GUIDBytes[7])).append(Hexdump.toHexString(GUIDBytes[6]));
        builder.append('-');
        for (int i = 8; i < 10; ++i) {
            builder.append(Hexdump.toHexString(GUIDBytes[i]));
        }
        builder.append('-');
        for (int i = 10; i < 16; ++i) {
            builder.append(Hexdump.toHexString(GUIDBytes[i]));
        }
        mGUID = builder.toString();
    }

    public void addUncompressedVideoFrame(UncompressedVideoFrame frame) {
        Log.d(TAG, "Adding video frame: " + frame);
        mVideoFrames.put(frame.getFrameIndex(), frame);
    }

    @Override
    public String toString() {
        return "UncompressedVideoFormat{" +
                "mFormatIndex=" + mFormatIndex +
                ", mNumberFrames=" + mNumberFrames +
                ", GUID=" + mGUID +
                ", mBitsPerPixel=" + mBitsPerPixel +
                ", mDefaultFrameIndex=" + mDefaultFrameIndex +
                ", AspectRatio=" + mAspectRatioX + ":" + mAspectRatioY +
                ", mInterlaceFlags=0x" + Hexdump.toHexString(mInterlaceFlags) +
                ", mCopyProtect=" + mCopyProtect +
                '}';
    }

    public String getGUID() {
        return mGUID;
    }

    public int getBitsPerPixel() {
        return mBitsPerPixel;
    }
}
