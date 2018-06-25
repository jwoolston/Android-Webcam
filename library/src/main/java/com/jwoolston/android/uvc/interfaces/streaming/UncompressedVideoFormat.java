package com.jwoolston.android.uvc.interfaces.streaming;

import android.util.SparseArray;
import com.jwoolston.android.uvc.util.Hexdump;

import timber.log.Timber;

/**
 * The Uncompressed Video Format descriptor defines the characteristics of a specific video stream. It is used for
 * formats that carry uncompressed video information, including all YUV variants.
 * A Terminal corresponding to a USB IN or OUT endpoint, and the interface it belongs to, supports one or more format
 * definitions. To select a particular format, host software sends control requests to the corresponding interface.
 *
 * This specification defines uncompressed streams in YUV color spaces. Each frame is independently sent by the
 * device to the host.
 *
 * The vertical and horizontal dimensions of the image are constrained by the color component subsampling; image size
 * must be a multiple of macropixel block size. No padding is allowed. This Uncompressed video payload specification
 * supports any YUV format. The recommended YUV formats are one packed 4:2:2 YUV format (YUY2), one packed 4:2:0 YUV
 * format (M420), and two planar 4:2:0 YUV formats (NV12, I420).
 *
 * @author Jared Woolston (Jared.Woolston@gmail.com)
 * @see <a href=http://www.usb.org/developers/docs/devclass_docs/USB_Video_Class_1_5.zip>USB Video Payload
 * Uncompressed 1.5 Specification §2.2 Table 2-1</a>
 */
public class UncompressedVideoFormat extends VideoFormat {

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

    private final String  guid;
    private final int     bitsPerPixel;

    private final SparseArray<UncompressedVideoFrame> videoFrames;

    public void addUncompressedVideoFrame(UncompressedVideoFrame frame) {
        Timber.d("Adding video frame: %s", frame);
        videoFrames.put(frame.getFrameIndex(), frame);
    }

    public UncompressedVideoFormat(byte[] descriptor) throws IllegalArgumentException {
        super(descriptor);
        if (descriptor.length < LENGTH) {
            throw new IllegalArgumentException(
                    "The provided discriptor is not long enough for an Uncompressed Video Format.");
        }
        videoFrames = new SparseArray<>();
        formatIndex = (0xFF & descriptor[bFormatIndex]);
        numberFrames = (0xFF & descriptor[bNumFrameDescriptors]);
        byte[] GUIDBytes = new byte[16];
        System.arraycopy(descriptor, guidFormat, GUIDBytes, 0, GUIDBytes.length);
        bitsPerPixel = (0xFF & descriptor[bBitsPerPixel]);
        defaultFrameIndex = (0xFF & descriptor[bDefaultFrameIndex]);
        aspectRatioX = (0xFF & descriptor[bAspectRatioX]);
        aspectRatioY = (0xFF & descriptor[bAspectRatioY]);
        interlaceFlags = descriptor[bmInterlaceFlags];
        copyProtect = descriptor[bCopyProtect] != 0;

        // Parse the GUID bytes to String
        final StringBuilder builder = new StringBuilder();
        builder.append(Hexdump.toHexString(GUIDBytes[3])).append(Hexdump.toHexString(GUIDBytes[2]))
                .append(Hexdump.toHexString(GUIDBytes[1])).append(Hexdump.toHexString(GUIDBytes[0]));
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
        guid = builder.toString();
    }

    @Override
    public String toString() {
        return "UncompressedVideoFormat{" +
               "formatIndex=" + formatIndex +
               ", numberFrames=" + numberFrames +
               ", GUID=" + guid +
               ", bitsPerPixel=" + bitsPerPixel +
               ", defaultFrameIndex=" + defaultFrameIndex +
               ", AspectRatio=" + aspectRatioX + ":" + aspectRatioY +
               ", interlaceFlags=0x" + Hexdump.toHexString(interlaceFlags) +
               ", copyProtect=" + copyProtect +
               '}';
    }

    public String getGUID() {
        return guid;
    }

    public int getBitsPerPixel() {
        return bitsPerPixel;
    }
}
