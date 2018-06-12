package com.jwoolston.android.uvc.interfaces;

import android.util.Log;
import android.util.SparseArray;
import com.jwoolston.android.uvc.interfaces.Descriptor.VideoSubclass;

/**
 * @author Jared Woolston (Jared.Woolston@gmail.com)
 */
public class VideoIAD extends InterfaceAssociationDescriptor {

    private static final String TAG = "VideoIAD";

    private SparseArray<AVideoClassInterface> interfaces;

    VideoIAD(byte[] descriptor) throws IllegalArgumentException {
        super(descriptor);
        if (VideoSubclass.getVideoSubclass(descriptor[bFunctionSubClass])
            != VideoSubclass.SC_VIDEO_INTERFACE_COLLECTION) {
            throw new IllegalArgumentException(
                    "The provided descriptor does not represent a Video Class Interface Association Descriptor.");
        }
        interfaces = new SparseArray<>();
    }

    @Override
    public void addInterface(AInterface aInterface) throws IllegalArgumentException {
        Log.d(TAG, "Adding Interface: " + aInterface);
        try {
            final AVideoClassInterface videoClassInterface = (AVideoClassInterface) aInterface;
            if (interfaces.get(videoClassInterface.getInterfaceNumber()) != null) {
                throw new IllegalArgumentException(
                        "An interface with the same index as the provided interface already exists!");
            }
            interfaces.put(videoClassInterface.getInterfaceNumber(), videoClassInterface);
        } catch (ClassCastException e) {
            throw new IllegalArgumentException(
                    "The provided interface is not an instance of VideoClassInterface or its subclasses.");
        }
    }

    @Override
    public AVideoClassInterface getInterface(int index) {
        return interfaces.get(index);
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder("VideoIAD{" +
                                                  "FirstInterface=" + getIndexFirstInterface() +
                                                  ", InterfaceCount=" + getInterfaceCount() +
                                                  ", IndexFunction=" + getIndexFunction() +
                                                  ", \nInterfaces:\n");
        final int count = getInterfaceCount();
        for (int i = 0; i < count; ++i) {
            builder.append(getInterface(i)).append(',').append('\n');
        }
        builder.append('}');
        return builder.toString();
    }
}
