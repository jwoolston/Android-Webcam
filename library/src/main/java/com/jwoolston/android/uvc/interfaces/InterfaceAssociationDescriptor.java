package com.jwoolston.android.uvc.interfaces;

import android.util.Log;

/**
 * @author Jared Woolston (Jared.Woolston@gmail.com)
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

    protected static InterfaceAssociationDescriptor parseIAD(byte[] descriptor) throws IllegalArgumentException {
        Log.d(TAG, "Parsing Interface Association Descriptor.");
        if (descriptor.length < LENGTH_DESCRIPTOR) {
            throw new IllegalArgumentException("The provided descriptor is not long enough. Have " + descriptor.length + " need " + LENGTH_DESCRIPTOR);
        }
        if (descriptor[bFunctionClass] == Descriptor.VIDEO_CLASS_CODE) {
            if (descriptor[bFunctionProtocol] != Descriptor.PROTOCOL.PC_PROTOCOL_UNDEFINED.protocol) {
                throw new IllegalArgumentException("The provided descriptor has an invalid protocol: " + descriptor[bFunctionProtocol]);
            }
            return new VideoIAD(descriptor);
        } else if (descriptor[bFunctionClass] == Descriptor.AUDIO_CLASS_CODE) {
            // TODO: Parse audio IAD
            return null;
        } else {
            throw new IllegalArgumentException("The provided descriptor has an invalid function class: " + descriptor[bFunctionClass]);
        }
    }

    protected InterfaceAssociationDescriptor(byte[] descriptor) throws IllegalArgumentException {
        mFirstInterface = descriptor[bFirstInterface];
        mInterfaceCount = descriptor[bInterfaceCount];
        mIndexFunction = descriptor[iFunction];
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

    public abstract void addInterface(AInterface aInterface);

    public abstract AInterface getInterface(int index);

    @Override
    public String toString() {
        return "InterfaceAssociationDescriptor{" +
                "mFirstInterface=" + mFirstInterface +
                ", mInterfaceCount=" + mInterfaceCount +
                ", mIndexFunction=" + mIndexFunction +
                '}';
    }
}
