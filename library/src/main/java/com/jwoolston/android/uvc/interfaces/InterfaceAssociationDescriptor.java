package com.jwoolston.android.uvc.interfaces;

import android.util.Log;
import com.jwoolston.android.uvc.interfaces.Descriptor.Protocol;

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

    private final int firstInterface;

    private final int interfaceCount;

    private final int indexFunction;

    protected static InterfaceAssociationDescriptor parseIAD(byte[] descriptor) throws IllegalArgumentException {
        Log.d(TAG, "Parsing Interface Association Descriptor.");
        if (descriptor.length < LENGTH_DESCRIPTOR) {
            throw new IllegalArgumentException("The provided descriptor is not long enough. Have " + descriptor.length + " need " + LENGTH_DESCRIPTOR);
        }
        if (descriptor[bFunctionClass] == Descriptor.VIDEO_CLASS_CODE) {
            if (descriptor[bFunctionProtocol] != Protocol.PC_PROTOCOL_UNDEFINED.protocol) {
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
        firstInterface = descriptor[bFirstInterface];
        interfaceCount = descriptor[bInterfaceCount];
        indexFunction = descriptor[iFunction];
    }

    public int getIndexFirstInterface() {
        return firstInterface;
    }

    public int getInterfaceCount() {
        return interfaceCount;
    }

    public int getIndexFunction() {
        return indexFunction;
    }

    public abstract void addInterface(AInterface aInterface);

    public abstract AInterface getInterface(int index);

    @Override
    public String toString() {
        return "InterfaceAssociationDescriptor{" +
                "FirstInterface=" + firstInterface +
                ", InterfaceCount=" + interfaceCount +
                ", IndexFunction=" + indexFunction +
                '}';
    }
}
