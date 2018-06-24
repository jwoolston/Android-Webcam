package com.jwoolston.android.uvc.requests.streaming;

/**
 * This bitmap enables features reported by the bmUsages field of the Video Frame Descriptor.
 * For temporally encoded video formats, this field must be supported, even if the device only supports a single
 * value for bUsage.
 *
 * @author Jared Woolston (Jared.Woolston@gmail.com)
 */
public enum Usage {
    REAL_TIME,
    BROADCAST,
    FILE_STORAGE,
    MULTIVIEW,
    RESERVED;

    public int getValue() {
        switch (ordinal()) {
            case 0:
                return 1;
            case 1:
                return 9;
            case 2:
                return 17;
            case 3:
                return 25;
            case 4:
            default:
                return 32;
        }
    }

    public static final Usage fromValue(int value) {
        if (value < 32) {
            if (value < 25) {
                if (value < 17) {
                    if (value < 9) {
                        return REAL_TIME;
                    } else {
                        return BROADCAST;
                    }
                } else {
                    return FILE_STORAGE;
                }
            } else {
                return MULTIVIEW;
            }
        } else {
            return RESERVED;
        }
    }
}
