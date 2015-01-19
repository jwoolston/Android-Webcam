package com.jwoolston.usb.webcam.interfaces;

/**
 * @author Jared Woolston (jwoolston@idealcorp.com)
 */
public class VideoIAD extends InterfaceAssociationDescriptor {

    private static final String TAG = "VideoIAD";

    private AVideoClassInterface mControlInterface;

    private AVideoClassInterface[] mStreamingInterfaces;

    VideoIAD(byte[] descriptor) throws IllegalArgumentException {
        super(descriptor);
        if (Descriptor.VIDEO_SUBCLASS.getVIDEO_SUBCLASS(descriptor[bFunctionSubClass]) != Descriptor.VIDEO_SUBCLASS.SC_VIDEO_INTERFACE_COLLECTION) {
            throw new IllegalArgumentException("The provided descriptor does not represent a Video Class Interface Association Descriptor.");
        }
    }

    @Override
    public void addInterface(AInterface aInterface) throws IllegalArgumentException {
        try {
            final AVideoClassInterface videoClassInterface = (AVideoClassInterface) aInterface;

        } catch (ClassCastException e) {
            throw new IllegalArgumentException("The provided interface is not an instance of VideoClassInterface or its subclasses.");
        }
    }

    @Override
    public String toString() {
        return "VideoIAD{" +
                "mFirstInterface=" + getIndexFirstInterface() +
                ", mInterfaceCount=" + getInterfaceCount() +
                ", mIndexFunction=" + getIndexFunction() +
                '}';
    }
}
