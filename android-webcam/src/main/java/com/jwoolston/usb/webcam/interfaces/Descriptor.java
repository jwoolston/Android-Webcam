package com.jwoolston.usb.webcam.interfaces;

import android.hardware.usb.UsbDevice;
import android.util.Log;
import android.util.SparseArray;

import com.jwoolston.usb.webcam.util.Hexdump;

import java.util.ArrayList;

/**
 * @author Jared Woolston (jwoolston@idealcorp.com)
 */
public class Descriptor {

    private static final String TAG = "Descriptor";

    static final byte VIDEO_CLASS_CODE = ((byte) 0x0E);
    static final byte AUDIO_CLASS_CODE = ((byte) 0x01);

    private static final int INDEX_DESCRIPTOR_TYPE = 1;

    public static void parseDescriptors(UsbDevice device, byte[] rawDescriptor) {
        final ArrayList<byte[]> descriptors = new ArrayList<>();
        final ArrayList<VideoIAD> videoIADs = new ArrayList<>();
        final SparseArray<VideoClassInterface> videoInterfaces = new SparseArray<>();
        int length;
        byte[] desc;
        TYPE type;
        int i = 0;
        STATE state = null;
        AInterface aInterface = null;
        while (i < rawDescriptor.length) {
            length = rawDescriptor[i];
            desc = new byte[length];
            System.arraycopy(rawDescriptor, i, desc, 0, length);
            type = TYPE.getTYPE(desc);
            InterfaceAssociationDescriptor iad;
            switch (type) {
                case INTERFACE_ASSOCIATION:
                    if (state != null) throw new IllegalStateException("Tried parsing an IAD at an invalid time: " + state);
                    state = STATE.IAD;
                    Log.d(TAG, "Interface Association: " + Hexdump.dumpHexString(desc));
                    iad = InterfaceAssociationDescriptor.parseIAD(desc);
                    Log.d(TAG, "" + iad);
                    break;
                case INTERFACE:
                    if (state != STATE.IAD) throw new IllegalStateException("Tried parsing a STANDARD INTERFACE at an invalid time: " + state);
                    state = STATE.STANDARD_INTERFACE;
                    Log.d(TAG, "Standard Interface: " + Hexdump.dumpHexString(desc));
                    aInterface = AInterface.parseDescriptor(device, desc);
                    Log.d(TAG, "" + aInterface);
                    break;
                case CS_INTERFACE:
                    if (aInterface == null) throw new IllegalStateException("Tried parsing a class interface when no standard interface has been parsed.");
                    if (aInterface.isClassInterfaceHeader(desc)) {
                        if (state != STATE.STANDARD_INTERFACE) throw new IllegalStateException("Tried parsing a CLASS SPECIFIC INTERFACE at an invalid time: " + state);
                        state = STATE.CLASS_INTERFACE_HEADER;
                        aInterface.parseClassInterfaceHeader(desc);
                    } else if (aInterface.isTerminal(desc)) {
                        if (state != STATE.CLASS_INTERFACE_HEADER && state != STATE.CLASS_INTERFACE_UNIT_TERMINAL)
                            throw new IllegalStateException("Tried parsing a CLASS SPECIFIC INTERFACE at an invalid time: " + state);
                        state = STATE.CLASS_INTERFACE_UNIT_TERMINAL;
                        Log.d(TAG, "Terminal: " + Hexdump.dumpHexString(desc));
                        aInterface.parseTerminal(desc);
                    } else if (aInterface.isUnit(desc)) {
                        if (state != STATE.CLASS_INTERFACE_HEADER && state != STATE.CLASS_INTERFACE_UNIT_TERMINAL)
                            throw new IllegalStateException("Tried parsing a CLASS SPECIFIC INTERFACE at an invalid time: " + state);
                        state = STATE.CLASS_INTERFACE_UNIT_TERMINAL;
                        Log.d(TAG, "Unit: " + Hexdump.dumpHexString(desc));
                    } else {

                    }
                    Log.d(TAG, "Class Specific Interface: " + Hexdump.dumpHexString(desc));
                    break;
                case DEVICE:
                case DEVICE_QUALIFIER:
                case CONFIGURATION:
                    break;
                default:
                    Log.d(TAG, "Descriptor: " + Hexdump.dumpHexString(desc));
            }
            i += length;
        }
    }

    private static enum STATE {
        IAD, STANDARD_INTERFACE, CLASS_INTERFACE_HEADER, CLASS_INTERFACE_UNIT_TERMINAL
    }


    public static enum VIDEO_SUBCLASS {
        SC_UNDEFINED(0x00),
        SC_VIDEOCONTROL(0x01),
        SC_VIDEOSTREAMING(0x02),
        SC_VIDEO_INTERFACE_COLLECTION(0x03);

        final byte subclass;

        private VIDEO_SUBCLASS(int subclass) {
            this.subclass = (byte) subclass;
        }

        public static VIDEO_SUBCLASS getVIDEO_SUBCLASS(byte subclass) {
            for (VIDEO_SUBCLASS s : VIDEO_SUBCLASS.values()) {
                if (s.subclass == subclass) {
                    return s;
                }
            }
            return null;
        }
    }

    public static enum AUDIO_SUBCLASS {
        SC_UNDEFINED(0x00),
        SC_AUDIOCONTROL(0x01),
        SC_AUDIOSTREAMING(0x02),
        SC_AUDIO_INTERFACE_COLLECTION(0x03);

        final byte subclass;

        private AUDIO_SUBCLASS(int subclass) {
            this.subclass = (byte) subclass;
        }

        public static AUDIO_SUBCLASS getAUDIO_SUBCLASS(byte subclass) {
            for (AUDIO_SUBCLASS a : AUDIO_SUBCLASS.values()) {
                if (a.subclass == subclass) {
                    return a;
                }
            }
            return null;
        }
    }

    public static enum TYPE {
        DEVICE(0x01),
        CONFIGURATION(0x02),
        STRING(0x03),
        INTERFACE(0x04),
        ENDPOINT(0x05),
        DEVICE_QUALIFIER(0x06),
        INTERFACE_ASSOCIATION(0x0B),
        CS_UNDEFINED(0x20),
        CS_DEVICE(0x21),
        CS_CONFIGURATION(0x22),
        CS_STRING(0x23),
        CS_INTERFACE(0x24),
        CS_ENDPOINT(0x25);

        final byte type;

        private TYPE(int type) {
            this.type = (byte) type;
        }

        public static TYPE getTYPE(byte[] raw) {
            for (TYPE t : TYPE.values()) {
                if (t.type == raw[INDEX_DESCRIPTOR_TYPE]) {
                    return t;
                }
            }
            throw new IllegalArgumentException("Unknown descriptor? " + Hexdump.dumpHexString(raw));
        }
    }

    public static enum PROTOCOL {
        PC_PROTOCOL_UNDEFINED(0x00),
        PC_PROTOCOL_15(0x01);

        final byte protocol;

        private PROTOCOL(int protocol) {
            this.protocol = (byte) protocol;
        }
    }
}
