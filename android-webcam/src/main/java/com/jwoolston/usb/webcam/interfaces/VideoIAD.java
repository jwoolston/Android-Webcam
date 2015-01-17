package com.jwoolston.usb.webcam.interfaces;

/**
 * @author Jared Woolston (jwoolston@idealcorp.com)
 */
public class VideoIAD extends InterfaceAssociationDescriptor {

    private static final String TAG = "VideoIAD";

    private final Descriptor.VIDEO_SUBCLASS mVideoSubclass;

    VideoIAD(int first, int count, int iFunction, Descriptor.VIDEO_SUBCLASS subclass) {
        super(first, count, iFunction);
        mVideoSubclass = subclass;
    }

    public Descriptor.VIDEO_SUBCLASS getVideoSubclass() {
        return mVideoSubclass;
    }

    @Override
    public String toString() {
        return "VideoIAD{" +
                "mFirstInterface=" + getIndexFirstInterface() +
                ", mInterfaceCount=" + getInterfaceCount() +
                ", mIndexFunction=" + getIndexFunction() +
                ", mVideoSubclass=" + mVideoSubclass +
                '}';
    }
}
