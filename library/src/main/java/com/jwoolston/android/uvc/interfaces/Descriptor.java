package com.jwoolston.android.uvc.interfaces;

import android.hardware.usb.UsbDevice;

import com.jwoolston.android.uvc.interfaces.endpoints.Endpoint;
import com.jwoolston.android.uvc.util.Hexdump;

import java.util.ArrayList;
import java.util.List;

import timber.log.Timber;

/**
 * @author Jared Woolston (Jared.Woolston@gmail.com)
 */
public class Descriptor {

    static final byte VIDEO_CLASS_CODE = ((byte) 0x0E);
    static final byte AUDIO_CLASS_CODE = ((byte) 0x01);

    private static final int INDEX_DESCRIPTOR_TYPE = 1;

    public static List<InterfaceAssociationDescriptor> parseDescriptors(UsbDevice device, byte[] rawDescriptor) {
        int length;
        byte[] desc;
        Type type;
        int i = 0;
        State state = null;
        InterfaceAssociationDescriptor iad = null;
        ArrayList<InterfaceAssociationDescriptor> iads = new ArrayList<>();
        AInterface aInterface = null;
        Endpoint aEndpoint = null;
        int endpointIndex = 1;
        while (i < rawDescriptor.length) {
            length = rawDescriptor[i];
            desc = new byte[length];
            System.arraycopy(rawDescriptor, i, desc, 0, length);
            type = Type.getType(desc);
            Timber.v("Current state: %s", state);

            switch (type) {
                case INTERFACE_ASSOCIATION:
                    if (state == State.STANDARD_ENDPOINT) {
                        i = rawDescriptor.length;
                        break;
                    }
                    if (state != null) {
                        throw new IllegalStateException("Tried parsing an IAD at an invalid time: " + state);
                    }
                    state = State.IAD;
                    iad = InterfaceAssociationDescriptor.parseIAD(desc);
                    iads.add(iad);
                    Timber.d("%s", iad);
                    break;
                case INTERFACE:
                    if (state != State.IAD && state != State.CLASS_INTERFACE && state != State.STANDARD_ENDPOINT
                        && state != State.CLASS_ENDPOINT) {
                        throw new IllegalStateException(
                                "Tried parsing a STANDARD INTERFACE at an invalid time: " + state);
                    }
                    state = State.STANDARD_INTERFACE;
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
                    Timber.d("%s", aInterface);
                    break;
                case CS_INTERFACE:
                    if (aInterface == null) {
                        throw new IllegalStateException(
                                "Tried parsing a class interface when no standard interface has been parsed.");
                    }
                    if (state != State.STANDARD_INTERFACE && state != State.CLASS_INTERFACE) {
                        throw new IllegalStateException("Tried parsing a CLASS INTERFACE at an invalid time: " + state);
                    }
                    state = State.CLASS_INTERFACE;
                    aInterface.parseClassDescriptor(desc);
                    break;
                case ENDPOINT:
                    if (aInterface == null) {
                        throw new IllegalStateException(
                                "Tried parsing a standard endpoint when no standard interface has been parsed.");
                    }
                    if (state != State.STANDARD_INTERFACE && state != State.CLASS_INTERFACE) {
                        throw new IllegalStateException(
                                "Tried parsing a STANDARD ENDPOINT at an invalid time: " + state);
                    }
                    state = State.STANDARD_ENDPOINT;
                    aEndpoint = Endpoint.parseDescriptor(aInterface.getUsbInterface(), desc);
                    aInterface.addEndpoint(endpointIndex, aEndpoint);
                    ++endpointIndex;
                    Timber.d("%s", aEndpoint);
                    break;
                case CS_ENDPOINT:
                    if (aEndpoint == null) {
                        throw new IllegalStateException(
                                "Tried parsing a class endpoint when no standard endpoint has been parsed.");
                    }
                    if (state != State.STANDARD_ENDPOINT) {
                        throw new IllegalStateException(
                                "Tried parsing a STANDARD ENDPOINT at an invalid time: " + state);
                    }
                    state = State.CLASS_ENDPOINT;
                    aEndpoint.parseClassDescriptor(desc);
                    break;
                case DEVICE:
                case DEVICE_QUALIFIER:
                case CONFIGURATION:
                    break;
                default:
                    Timber.d("Descriptor: %s", Hexdump.dumpHexString(desc));
            }
            i += length;
        }
        return iads;
    }

    private static enum State {
        IAD, STANDARD_INTERFACE, CLASS_INTERFACE, STANDARD_ENDPOINT, CLASS_ENDPOINT
    }


    public static enum VideoSubclass {
        SC_UNDEFINED(0x00),
        SC_VIDEOCONTROL(0x01),
        SC_VIDEOSTREAMING(0x02),
        SC_VIDEO_INTERFACE_COLLECTION(0x03);

        public final byte subclass;

        private VideoSubclass(int subclass) {
            this.subclass = (byte) (subclass & 0xFF);
        }

        public static VideoSubclass getVideoSubclass(byte subclass) {
            for (VideoSubclass s : VideoSubclass.values()) {
                if (s.subclass == subclass) {
                    return s;
                }
            }
            return null;
        }
    }

    public static enum AudioSubclass {
        SC_UNDEFINED(0x00),
        SC_AUDIOCONTROL(0x01),
        SC_AUDIOSTREAMING(0x02),
        SC_AUDIO_INTERFACE_COLLECTION(0x03);

        public final byte subclass;

        private AudioSubclass(int subclass) {
            this.subclass = (byte) (subclass & 0xFF);
        }

        public static AudioSubclass getAudioSubclass(byte subclass) {
            for (AudioSubclass a : AudioSubclass.values()) {
                if (a.subclass == subclass) {
                    return a;
                }
            }
            return null;
        }
    }

    public static enum Type {
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

        private Type(int type) {
            this.type = (byte) (type & 0xFF);
        }

        public static Type getType(byte[] raw) {
            for (Type t : Type.values()) {
                if (t.type == raw[INDEX_DESCRIPTOR_TYPE]) {
                    return t;
                }
            }
            throw new IllegalArgumentException("Unknown descriptor? " + Hexdump.dumpHexString(raw));
        }
    }

    public static enum Protocol {
        PC_PROTOCOL_UNDEFINED(0x00),
        PC_PROTOCOL_15(0x01);

        public final byte protocol;

        private Protocol(int protocol) {
            this.protocol = (byte) (protocol & 0xFF);
        }
    }
}
