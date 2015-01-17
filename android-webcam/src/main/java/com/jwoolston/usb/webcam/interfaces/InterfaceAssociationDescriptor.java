package com.jwoolston.usb.webcam.interfaces;

import android.util.Log;
import android.util.SparseArray;

/**
 * @author Jared Woolston (jwoolston@idealcorp.com)
 */
public abstract class InterfaceAssociationDescriptor {

    private static final String TAG = "IAD";

    private static final int LENGTH_DESCRIPTOR = 8;

    protected static final int bFirstInterface   = 2;
    protected static final int bInterfaceCount   = 3;
    protected static final int bFunctionClass    = 4;
    protected static final int bFunctionSubClass = 5;
    protected static final int bFunctionProtocol = 6;
    protected static final int iFunction         = 7;

    private final int mFirstInterface;

    private final int mInterfaceCount;

    private final int mIndexFunction;

    private final SparseArray<AInterface> mInterfaces;

    protected static InterfaceAssociationDescriptor parseIAD(byte[] descriptor) throws IllegalArgumentException {
        Log.d(TAG, "Parsing Interface Association Descriptor.");
        if (descriptor.length < LENGTH_DESCRIPTOR) {
            throw new IllegalArgumentException("The provided descriptor is not long enough. Have " + descriptor.length + " need " + LENGTH_DESCRIPTOR);
        }
        if (descriptor[bFunctionClass] == Descriptor.VIDEO_CLASS_CODE) {
            if (descriptor[bFunctionProtocol] != Descriptor.PROTOCOL.PC_PROTOCOL_UNDEFINED.protocol) {
                throw new IllegalArgumentException("The provided descriptor has an invalid protocol: " + descriptor[bFunctionProtocol]);
            }
            return new VideoIAD(descriptor[bFirstInterface], descriptor[bInterfaceCount], descriptor[iFunction], Descriptor.VIDEO_SUBCLASS.getVIDEO_SUBCLASS(
                    descriptor[bFunctionSubClass]));
        } else if (descriptor[bFunctionClass] == Descriptor.AUDIO_CLASS_CODE) {
            // TODO: Parse audio IAD
            return null;
        } else {
            throw new IllegalArgumentException("The provided descriptor has an invalid function class: " + descriptor[bFunctionClass]);
        }
    }

    protected InterfaceAssociationDescriptor(int first, int count, int iFunction) {
        mInterfaces = new SparseArray<>();
        mFirstInterface = first;
        mInterfaceCount = count;
        mIndexFunction = iFunction;
    }

    public int getIndexFirstInterface() {
        return mFirstInterface;
    }

    public int getInterfaceCount() {
        return mInterfaceCount;
    }

    public int getIndexFunction() {
        return mIndexFunction;
    }

    @Override
    public String toString() {
        return "InterfaceAssociationDescriptor{" +
                "mFirstInterface=" + mFirstInterface +
                ", mInterfaceCount=" + mInterfaceCount +
                ", mIndexFunction=" + mIndexFunction +
                '}';
    }
}
