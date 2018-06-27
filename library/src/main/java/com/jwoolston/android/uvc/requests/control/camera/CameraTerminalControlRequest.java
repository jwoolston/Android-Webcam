package com.jwoolston.android.uvc.requests.control.camera;

import android.support.annotation.NonNull;

import com.jwoolston.android.uvc.interfaces.terminals.CameraTerminal;
import com.jwoolston.android.uvc.requests.Request;
import com.jwoolston.android.uvc.requests.control.UnitTerminalControlRequest;

/**
 * @author Jared Woolston (Jared.Woolston@gmail.com)
 */
class CameraTerminalControlRequest extends UnitTerminalControlRequest {

    private static short valueFromControlSelector(@NonNull CameraTerminal.Control selector) {
        return ((short) (0xFFFF & (selector.code << 8)));
    }

    protected CameraTerminalControlRequest(@NonNull Request request, short value, short index, @NonNull byte[] data) {
        super(request, value, index, data);
    }

    protected CameraTerminalControlRequest(@NonNull Request request, short index, @NonNull byte[] data) {
        this(request, (short) 0, index, data);
    }
}
