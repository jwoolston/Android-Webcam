package com.jwoolston.usb.webcam.interfaces;

import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbInterface;
import android.util.Log;

import com.jwoolston.usb.webcam.interfaces.endpoints.Endpoint;
import com.jwoolston.usb.webcam.interfaces.streaming.AVideoFormat;
import com.jwoolston.usb.webcam.interfaces.streaming.MJPEGVideoFormat;
import com.jwoolston.usb.webcam.interfaces.streaming.MJPEGVideoFrame;
import com.jwoolston.usb.webcam.interfaces.streaming.UncompressedVideoFormat;
import com.jwoolston.usb.webcam.interfaces.streaming.UncompressedVideoFrame;
import com.jwoolston.usb.webcam.interfaces.streaming.VideoColorMatchingDescriptor;
import com.jwoolston.usb.webcam.interfaces.streaming.VideoStreamInputHeader;
import com.jwoolston.usb.webcam.interfaces.streaming.VideoStreamOutputHeader;
import com.jwoolston.usb.webcam.util.Hexdump;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Jared Woolston (jwoolston@idealcorp.com)
 */
public class VideoStreamingInterface extends AVideoClassInterface {

    private static final String TAG = "VideoStreamingInterface";

    private static final int bLength = 0;
    private static final int bDescriptorType = 1;
    private static final int bDescriptorSubtype = 2;

    private VideoStreamInputHeader  mInputHeader;
    private VideoStreamOutputHeader mOutputHeader;

    private final List<AVideoFormat> mVideoFormats;

    private AVideoFormat mLastFormat;

    private VideoColorMatchingDescriptor mColorMatchingDescriptor;

    public static VideoStreamingInterface parseVideoStreamingInterface(UsbDevice device, byte[] descriptor) throws IllegalArgumentException {
        Log.d(TAG, "Parsing Video Class Interface header.");

        final UsbInterface usbInterface = AInterface.getUsbInterface(device, descriptor);
        if (usbInterface == null) throw new IllegalArgumentException("The provided descriptor refers to a non-existant interface.");
        return new VideoStreamingInterface(usbInterface, descriptor);
    }

    VideoStreamingInterface(UsbInterface usbInterface, byte[] descriptor) {
        super(usbInterface, descriptor);
        mVideoFormats = new ArrayList<>();
    }

    @Override
    public String toString() {
        return "VideoStreamingInterface{" +
                "mInputHeader=" + mInputHeader +
                ", mOutputHeader=" + mOutputHeader +
                ", mVideoFormats=" + mVideoFormats +
                ", mColorMatchingDescriptor=" + mColorMatchingDescriptor +
                ", Usb Interface=" + getUsbInterface() +
                '}';
    }

    @Override
    public void parseClassDescriptor(byte[] descriptor) {
        final VS_INTERFACE_SUBTYPE subtype = VS_INTERFACE_SUBTYPE.fromByte(descriptor[bDescriptorSubtype]);
        switch (subtype) {
            case VS_INPUT_HEADER:
                mInputHeader = new VideoStreamInputHeader(descriptor);
                break;
            case VS_OUTPUT_HEADER:
                mOutputHeader = new VideoStreamOutputHeader(descriptor);
                break;
            case VS_FORMAT_UNCOMPRESSED:
                final UncompressedVideoFormat uncompressedVideoFormat = new UncompressedVideoFormat(descriptor);
                Log.d(TAG, "Adding Video Format: " + uncompressedVideoFormat);
                mVideoFormats.add(uncompressedVideoFormat);
                mLastFormat = uncompressedVideoFormat;
                break;
            case VS_FRAME_UNCOMPRESSED:
                final UncompressedVideoFrame uncompressedVideoFrame = new UncompressedVideoFrame(descriptor);
                try {
                    ((UncompressedVideoFormat) mLastFormat).addUncompressedVideoFrame(uncompressedVideoFrame);
                } catch (ClassCastException e) {
                    throw new IllegalArgumentException("The parsed uncompressed frame descriptor is not valid for the previously parsed Format: " + mLastFormat.getClass().getName());
                }
                break;
            case VS_FORMAT_MJPEG:
                final MJPEGVideoFormat mjpegVideoFormat = new MJPEGVideoFormat(descriptor);
                Log.d(TAG, "Adding Video Format: " + mjpegVideoFormat);
                mVideoFormats.add(mjpegVideoFormat);
                mLastFormat = mjpegVideoFormat;
                break;
            case VS_FRAME_MJPEG:
                final MJPEGVideoFrame mjpegVideoFrame = new MJPEGVideoFrame(descriptor);
                try {
                    ((MJPEGVideoFormat) mLastFormat).addMJPEGVideoFrame(mjpegVideoFrame);
                } catch (ClassCastException e) {
                    throw new IllegalArgumentException("The parsed MJPEG frame descriptor is not valid for the previously parsed Format: " + mLastFormat.getClass().getName());
                }
                break;
            case VS_STILL_IMAGE_FRAME:
                Log.d(TAG, "VideoStream Still Image Frame Descriptor");
                Log.d(TAG, "" + Hexdump.dumpHexString(descriptor));
                //TODO: Handle STILL IMAGE FRAME descriptor section 3.9.2.5 Pg. 81
                break;
            case VS_COLORFORMAT:
                mColorMatchingDescriptor = new VideoColorMatchingDescriptor(descriptor);
                mLastFormat.setColorMatchingDescriptor(mColorMatchingDescriptor);
                Log.d(TAG, "" + mColorMatchingDescriptor);
                break;
            default:
                Log.d(TAG, "Unknown streaming interface descriptor: " + Hexdump.dumpHexString(descriptor));
        }
    }

    @Override
    public void parseAlternateFunction(byte[] descriptor) {
        Log.d(TAG, "Parsing alternate function for VideoStreamingInterface: " + getInterfaceNumber());
        mCurrentSetting = 0xFF & descriptor[bAlternateSetting];
        final int endpointCount = (0xFF & descriptor[bNumEndpoints]);
        mEndpoints.put(mCurrentSetting, new Endpoint[endpointCount]);
    }

    public static enum VS_INTERFACE_SUBTYPE {
        VS_UNDEFINED(0x00),
        VS_INPUT_HEADER(0x01),
        VS_OUTPUT_HEADER(0x02),
        VS_STILL_IMAGE_FRAME(0x03),
        VS_FORMAT_UNCOMPRESSED(0x04),
        VS_FRAME_UNCOMPRESSED(0x05),
        VS_FORMAT_MJPEG(0x06),
        VS_FRAME_MJPEG(0x07),
        RESERVED_0(0x08),
        RESERVED_1(0x09),
        VS_FORMAT_MPEG2TS(0x0A),
        RESERVED_2(0x0B),
        VS_FORMAT_DV(0x0C),
        VS_COLORFORMAT(0x0D),
        RESERVED_3(0x0E),
        RESERVED_4(0x0F),
        VS_FORMAT_FRAME_BASED(0x10),
        VS_FRAME_FRAME_BASED(0x11),
        VS_FORMAT_STREAM_BASED(0x12),
        VS_FORMAT_H264(0x13),
        VS_FRAME_H264(0x14),
        VS_FRAME_H264_SIMULCAST(0x15),
        VS_FORMAT_VP8(0x16),
        VS_FRAME_VP8(0x17),
        VS_FORMAT_VP8_SIMULCAST(0x18);

        public final byte code;

        private VS_INTERFACE_SUBTYPE(int code) {
            this.code = (byte) (0xFF & code);
        }

        public static VS_INTERFACE_SUBTYPE fromByte(byte code) {
            for (VS_INTERFACE_SUBTYPE subtype : VS_INTERFACE_SUBTYPE.values()) {
                if (subtype.code == code) {
                    return subtype;
                }
            }
            return null;
        }
    }
}
