package com.jwoolston.android.uvc.interfaces;

import android.hardware.usb.UsbDevice;
import android.util.Log;

import com.jwoolston.android.uvc.interfaces.endpoints.Endpoint;
import com.jwoolston.android.uvc.util.Hexdump;

/**
 * @author Jared Woolston (jwoolston@idealcorp.com)
 */
public class Descriptor {

    private static final String TAG = "Descriptor";

    static final byte VIDEO_CLASS_CODE = ((byte) 0x0E);
    static final byte AUDIO_CLASS_CODE = ((byte) 0x01);

    private static final int INDEX_DESCRIPTOR_TYPE = 1;

    public static void parseDescriptors(UsbDevice device, byte[] rawDescriptor) {
        int length;
        byte[] desc;
        TYPE type;
        int i = 0;
        STATE state = null;
        InterfaceAssociationDescriptor iad = null;
        AInterface aInterface = null;
        Endpoint aEndpoint = null;
        int endpointIndex = 1;
        while (i < rawDescriptor.length) {
            length = rawDescriptor[i];
            desc = new byte[length];
            System.arraycopy(rawDescriptor, i, desc, 0, length);
            type = TYPE.getTYPE(desc);

            switch (type) {
                case INTERFACE_ASSOCIATION:
                    if (state == STATE.STANDARD_ENDPOINT) {
                        i = rawDescriptor.length;
                        break;
                    }
                    if (state != null) throw new IllegalStateException("Tried parsing an IAD at an invalid time: " + state);
                    state = STATE.IAD;
                    iad = InterfaceAssociationDescriptor.parseIAD(desc);
                    Log.d(TAG, "" + iad);
                    break;
                case INTERFACE:
                    if (state != STATE.IAD && state != STATE.CLASS_INTERFACE && state != STATE.STANDARD_ENDPOINT && state != STATE.CLASS_ENDPOINT) {
                        throw new IllegalStateException("Tried parsing a STANDARD INTERFACE at an invalid time: " + state);
                    }
                    state = STATE.STANDARD_INTERFACE;
                    endpointIndex = 1;
                    aInterface = AInterface.parseDescriptor(device, desc);
                    if (iad != null && aInterface != null) {
                        final AInterface existing = iad.getInterface(aInterface.getInterfaceNumber());
                        if (existing != null) {
                            existing.parseAlternateFunction(desc);
                        } else {
                            // We need to save the old one
                            iad.addInterface(aInterface);
                        }
                    }
                    Log.d(TAG, "" + aInterface);
                    break;
                case CS_INTERFACE:
                    if (aInterface == null) throw new IllegalStateException("Tried parsing a class interface when no standard interface has been parsed.");
                    if (state != STATE.STANDARD_INTERFACE && state != STATE.CLASS_INTERFACE) throw new IllegalStateException("Tried parsing a CLASS INTERFACE at an invalid time: " + state);
                    state = STATE.CLASS_INTERFACE;
                    aInterface.parseClassDescriptor(desc);
                    break;
                case ENDPOINT:
                    if (aInterface == null) throw new IllegalStateException("Tried parsing a standard endpoint when no standard interface has been parsed.");
                    if (state != STATE.STANDARD_INTERFACE && state != STATE.CLASS_INTERFACE) throw new IllegalStateException("Tried parsing a STANDARD ENDPOINT at an invalid time: " + state);
                    state = STATE.STANDARD_ENDPOINT;
                    aEndpoint = Endpoint.parseDescriptor(aInterface.getUsbInterface(), desc);
                    aInterface.addEndpoint(endpointIndex, aEndpoint);
                    ++endpointIndex;
                    Log.d(TAG, "" + aEndpoint);
                    break;
                case CS_ENDPOINT:
                    if (aEndpoint == null) throw new IllegalStateException("Tried parsing a class endpoint when no standard endpoint has been parsed.");
                    if (state != STATE.STANDARD_ENDPOINT) throw new IllegalStateException("Tried parsing a STANDARD ENDPOINT at an invalid time: " + state);
                    state = STATE.CLASS_ENDPOINT;
                    aEndpoint.parseClassDescriptor(desc);
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
        IAD, STANDARD_INTERFACE, CLASS_INTERFACE, STANDARD_ENDPOINT, CLASS_ENDPOINT
    }


    public static enum VIDEO_SUBCLASS {
        SC_UNDEFINED(0x00),
        SC_VIDEOCONTROL(0x01),
        SC_VIDEOSTREAMING(0x02),
        SC_VIDEO_INTERFACE_COLLECTION(0x03);

        public final byte subclass;

        private VIDEO_SUBCLASS(int subclass) {
            this.subclass = (byte) (subclass & 0xFF);
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

        public final byte subclass;

        private AUDIO_SUBCLASS(int subclass) {
            this.subclass = (byte) (subclass & 0xFF);
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

        public final byte type;

        private TYPE(int type) {
            this.type = (byte) (type & 0xFF);
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

        public final byte protocol;

        private PROTOCOL(int protocol) {
            this.protocol = (byte) (protocol & 0xFF);
        }
    }
}
