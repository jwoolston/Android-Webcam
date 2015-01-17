package com.jwoolston.usb.webcam.interfaces.terminals;

/**
 * @author Jared Woolston (jwoolston@idealcorp.com)
 */
public abstract class VideoTerminal {

    protected static final int bLength            = 0;
    protected static final int bDescriptorType    = 1;
    protected static final int bDescriptorSubtype = 2;
    protected static final int bTerminalID    = 3;
    protected static final int wTerminalType  = 4;
    protected static final int bAssocTerminal = 6;
}
