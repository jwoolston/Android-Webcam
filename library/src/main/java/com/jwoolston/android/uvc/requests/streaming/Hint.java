package com.jwoolston.android.uvc.requests.streaming;

import java.util.BitSet;

/**
 * Bitfield control indicating to the function what fields shall be kept fixed (indicative only):<br>
 * - <b>D0: dwFrameInterval</b><br>
 * - <b>D1: wKeyFrameRate</b><br>
 * - <b>D2: wPFrameRate</b><br>
 * - <b>D3: wCompQuality</b><br>
 * - <b>D4: wCompWindowSize</b><br>
 * - <b>D15..5: Reserved (0)</b>
 * <p>
 * The hint bitmap indicates to the video streaming interface which fields shall be kept constant during stream
 * parameter negotiation. For example, if the selection wants to favor frame rate over quality, the
 * dwFrameInterval bit will be set (1).
 * </p><p>
 * This field is set by the host, and is read-only for the video streaming interface.
 * </p>
 */
public final class Hint {

    private final int Index_dwFrameInterval = 0;
    private final int Index_wKeyFrameRate   = 1;
    private final int Index_wPFrameRate     = 2;
    private final int Index_wCompQuality    = 3;
    private final int Index_wCompWindowSize = 4;

    private final BitSet bitSet = new BitSet(16);

    public Hint() {

    }

    public Hint(short raw) {
        for (int i = 0; i < 5; ++i) {
            bitSet.set(i, (raw & (0x01 << i)) == 1);
        }
    }

    short getRaw() {
        short value = 0;
        for (int i = 0; i < 5; ++i) {
            value |= ((bitSet.get(i) ? 0x1 : 0x0) << i);
        }
        return value;
    }

    public boolean getFrameInterval() {
        return bitSet.get(Index_dwFrameInterval);
    }

    public boolean getKeyFrameRate() {
        return bitSet.get(Index_wKeyFrameRate);
    }

    public boolean getPFrameRate() {
        return bitSet.get(Index_wPFrameRate);
    }

    public boolean getCompQuality() {
        return bitSet.get(Index_wCompQuality);
    }

    public boolean getCompWindowSize() {
        return bitSet.get(Index_wCompWindowSize);
    }

    public void setFrameInterval(boolean state) {
        bitSet.set(Index_dwFrameInterval, state);
    }

    public void setKeyFrameRate(boolean state) {
        bitSet.set(Index_wKeyFrameRate, state);
    }

    public void setPFrameRate(boolean state) {
        bitSet.set(Index_wPFrameRate, state);
    }

    public void setCompQuality(boolean state) {
        bitSet.set(Index_wCompQuality, state);
    }

    public void setCompWindowSize(boolean state) {
        bitSet.set(Index_wCompWindowSize, state);
    }
}
