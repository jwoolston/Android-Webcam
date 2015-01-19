package com.jwoolston.usb.webcam.interfaces;

import android.util.Log;
import android.util.SparseArray;

/**
 * @author Jared Woolston (jwoolston@idealcorp.com)
 */
public class VideoIAD extends InterfaceAssociationDescriptor {

    private static final String TAG = "VideoIAD";

    private SparseArray<AVideoClassInterface> mInterfaces;

    VideoIAD(byte[] descriptor) throws IllegalArgumentException {
        super(descriptor);
        if (Descriptor.VIDEO_SUBCLASS.getVIDEO_SUBCLASS(descriptor[bFunctionSubClass]) != Descriptor.VIDEO_SUBCLASS.SC_VIDEO_INTERFACE_COLLECTION) {
            throw new IllegalArgumentException("The provided descriptor does not represent a Video Class Interface Association Descriptor.");
        }
        mInterfaces = new SparseArray<>();
    }

    @Override
    public void addInterface(AInterface aInterface) throws IllegalArgumentException {
        Log.d(TAG, "Adding Interface: " + aInterface);
        try {
            final AVideoClassInterface videoClassInterface = (AVideoClassInterface) aInterface;
                if (mInterfaces.get(videoClassInterface.getInterfaceNumber()) != null) {
                    throw new IllegalArgumentException("An interface with the same index as the provided interface already exists!");
                }
                mInterfaces.put(videoClassInterface.getInterfaceNumber(), videoClassInterface);
        } catch (ClassCastException e) {
            throw new IllegalArgumentException("The provided interface is not an instance of VideoClassInterface or its subclasses.");
        }
    }

    @Override
    public AVideoClassInterface getInterface(int index) {
        return mInterfaces.get(index);
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
